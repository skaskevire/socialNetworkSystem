System provides following REST API:

1.	System allows registering new users. Registration info consists of name, birth date, city.
	POST /users/add
	body:
	{
		"name":"name_ex",
		"bdate":"01.01.1970",
		"city":"Minsk"
	}
	
	result: 200, OK

2.	System allows searching for users by name, age, city. E.g.:
a.	Find everyone living in Krakow
b.	Find everyone living in Krakow, older than 20 and younger than 30
c.	Find everyone named Jan Kowalski
d.	Find everyone named Jan Kowalski living in Krakow
e.	Find everyone whose name contains ‘Kowal’ (so Jan Kowal and Michal Kowalski would match this search)

 GET /users/find?name=name_ex&bdateRangeFloor=01.01.1970&bdateRangeCeiling=01.01.1971&city=Minsk

   GET /users/find?name=name_ex
   GET /users/find?name=name_ex&bdateRangeFloor=&bdateRangeCeiling=&city=

   GET /users/find?bdateRangeFloor=01.01.1970&bdateRangeCeiling=01.01.1971
   GET /users/find?bdateRangeFloor=01.01.1970&bdateRangeCeiling=01.01.1971&name=&city=
   
   GET /users/find?city=Minsk
   GET /users/find?city=Minsk&name=&bdateRangeFloor=&bdateRangeCeiling=
   
  
	result: 200, OK
	body:
	{
		{
			"name":"name_ex",
			"bdate":"01.01.1970",
			"city":"Minsk"
		},
		{
			"name":"name_ex",
			"bdate":"01.01.1970",
			"city":"Minsk"
		}
	}
   
3.	System allows a user to send invitation to another user to become friends

 POST /users/frends/add
	body:
	{
		"sourceUser": "initiator",
		"targetUser": "recipient",
	}
	
	result: 200, OK
	
4.	System allows a user to see invitations sent to him

 GET /users/friends/invitations/get
 result: 200,OK
 {
	{"from":"username1"},
	{"from":"username2"},
	{"from":"username3"},
	{"from":"username4"},
	{"from":"username5"}
 }
 
5.	System allows a user to accept an invitation, becoming friends

 POST /users/friends/invitations/accept
 {
	"acceptorName": "John",
	"initiatorName":"username1"
 }
 
6.	System allows a user to see his network (friends, friends of his friends, and so on)

GET /users/{name}/friends/explore?density=1
result: 200,OK
{
	"name_ex1",
	"name_ex2",
	"name_ex3",
	"name_ex4"
}

GET /users/{name}/friends/explore?density=2
result: 200,OK
{
	"name_ex1": {"name_ex5","name_ex6","name_ex7"},
	"name_ex2": {"name_ex8","name_ex9","name_ex10"},
	"name_ex3": {"name_ex11"},
	"name_ex4": {"name_ex12"}
}

7.	System allows un-friending (user A cancels friendship with user B)
 DELETE /users/frends/remove
	body:
	{
		"user": "username",
	}
	
	result: 200, OK

8.	System allows checking a ‘distance factor’ in a network, which is:
a.	1 if user B is a friend of user A
b.	2 if user B is a friend of a friend of user A
c.	And so on
	
GET /users/friends/distanceFactor?sourceUser=u1&targetUser=u2

result: 200,OK
{
	"distanceFactor": 3;
}

9.	System allows a user to post a message
POST /users/messages/post
{
	"user":"username",
	"message":"message"
}
result: 200,OK

10.	System allows a user to retrieve a chronological list of messages from his friends
GET /users/{name}/messages/friends

result: 200,OK
{
	{
	"user":"username1",
	"message":"message1"
	},
	{
	"user":"username2",
	"message":"message2"
	},
	{
	"user":"username3",
	"message":"message3"
	},
}

11.	System allows a user to retrieve a chronological list of messages from his network

GET /users/{name}/messages/network

result: 200,OK
{
	{
	"user":"username1",
	"message":"message1"
	},
	{
	"user":"username2",
	"message":"message2"
	},
	{
	"user":"username3",
	"message":"message3"
	},
}
