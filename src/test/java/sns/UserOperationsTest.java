package sns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import sns.Application;
import sns.entity.Message;
import sns.entity.User;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserOperationsTest {
	@Autowired
	private TestRestTemplate restTemplate;
	private List<User> users;

	@PostConstruct
	public void init() {
		Integer intitialUserStorageSize = restTemplate.getForEntity("/camel/users/count", Integer.class).getBody();

		Integer n = 1000;
		System.out.println("Start!");
		users = new ArrayList<User>();
		Random r = new Random();
		for (int i = 0; i < n; i++) {
			User user = new User();
			user.setBdate(r.nextInt(30) + "." + r.nextInt(12) + "." + (1900 + r.nextInt(118)));
			user.setCity("City" + r.nextInt(1000));
			user.setName("User" + i + UUID.randomUUID().toString());
			users.add(user);
			ResponseEntity<String> responseEntity = restTemplate.postForEntity("/camel/users/add", user, String.class);
			Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		}

		while (true) {
			Integer currentUserStorageSize = restTemplate.getForEntity("/camel/users/count", Integer.class).getBody();
			System.out.println(intitialUserStorageSize);
			System.out.println(currentUserStorageSize);
			if ((intitialUserStorageSize + n) == currentUserStorageSize) {
				break;
			}
			try {
				Thread.sleep(3000l);
				System.out.println("Waiting for all users to be created...");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		for (int i = 0; i < n * 2; i++) {
			int firstUserIndex = r.nextInt(n - 2);
			int secondUserIndex = r.nextInt(n - 2);
			User user1 = users.get(firstUserIndex);
			User user2 = users.get(secondUserIndex);
			restTemplate.postForEntity("/camel/users/" + user1.getName() + "/friends/add/" + user2.getName(), null,
					String.class);
			restTemplate.postForEntity(
					"/camel/users/" + user2.getName() + "/friends/invitations/accept/" + user1.getName(), null,
					String.class);
		}

		for (int i = 0; i < n * 5; i++) {
			int nnn = r.nextInt(n - 2);

			User user1 = users.get(nnn);

			restTemplate.postForEntity("/camel/users/" + user1.getName() + "/messages/post", new Message("msg"),
					String.class);

		}
	}

	@Test
	public void testUserOperationsPerformance() {
		int tolerance = 5000;
		for (User u : users) {
			testGetUserFriends(u, tolerance);
			testGetUserFriendsMessages(u, tolerance);
			testFindUserByNameAndCity(u, tolerance);
		}
		cleanup();
	}

	private void testGetUserFriends(User u, long tolerance) {
		long start = System.currentTimeMillis();
		restTemplate.getForEntity("/camel/users/" + u.getName() + "/friends/explore/users", List.class);
		long end = System.currentTimeMillis();
		System.out.println(end - start);
		Assert.assertTrue((end - start) < tolerance);
	}

	private void testGetUserFriendsMessages(User u, long tolerance) {
		long start = System.currentTimeMillis();
		restTemplate.getForEntity("/camel/users/" + u.getName() + "/messages/friends", List.class);
		long end = System.currentTimeMillis();
		System.out.println(end - start);
		Assert.assertTrue((end - start) < tolerance);
	}

	private void testFindUserByNameAndCity(User u, long tolerance) {
		Map<String, String> args = new HashMap<String, String>();
		args.put("name", u.getName());
		args.put("city", u.getCity());
		long start = System.currentTimeMillis();
		restTemplate.getForEntity("/camel/users/find?name={name}&city={city}", List.class, args);

		long end = System.currentTimeMillis();
		Assert.assertTrue((end - start) < tolerance);
	}

	public void cleanup() {
		for (User u : users) {
			final String uri = "/camel/users/delete/{name}";

			Map<String, String> params = new HashMap<String, String>();
			params.put("name", u.getName());
			restTemplate.delete(uri, params);

		}
	}
}