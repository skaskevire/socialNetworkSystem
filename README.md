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
e.	Find everyone whose name contains â€�Kowalâ€™ (so Jan Kowal and Michal Kowalski would match this search)


	GET /users/find?name=name_ex&bdateRangeFloor=01.01.1970&bdateRangeCeiling=01.01.1971&city=Minsk
	GET /users/find?name=name_ex
	GET /users/find?name=name_ex&bdateRangeFloor=&bdateRangeCeiling=&city=
	GET /users/find?bdateRangeFloor=01.01.1970&bdateRangeCeiling=01.01.1971
	GET /users/find?bdateRangeFloor=01.01.1970&bdateRangeCeiling=01.01.1971&name=&city=   
	GET /users/find?city=Minsk
	GET /users/find?city=Minsk&name=&bdateRangeFloor=&bdateRangeCeiling=   
  
	result: 200, OK
	body:
	[
    {
        "id": "5ae2c34ca06e1b1a4c2b95cf",
        "name": "Name20ce198a0-d5c9-45e1-a492-c21a42490640",
        "bdate": 1524810572765,
        "city": "City1",
        "messages": [
            {
                "date": 1524810899869,
                "message": "53363302-25ef-4964-bbc0-55f2516a0780"
            }
        ]
    },
    {
        "id": "5ae2c34da06e1b1a4c2b95e1",
        "name": "Name208e12ad51-e3c1-4ed1-8791-9cabccf21f32",
        "bdate": 1524810573550,
        "city": "City1",
        "messages": [
            {
                "date": 1524810900440,
                "message": "97518000-c559-4c13-ad2f-5546b88ecfc8"
            }
        ]
    },
    {
        "id": "5ae2c34da06e1b1a4c2b95e2",
        "name": "Name21f82e290e-1812-4622-856b-9d3ab5396b21",
        "bdate": 1524810573574,
        "city": "City1"
    }]

3.	System allows a user to send invitation to another user to become friends

	POST /users/{username}/friends/add/{targetUser}
	
	result: 200, OK
	
4.	System allows a user to see invitations sent to him

	GET /users/{username}/friends/invitations/get
	result: 200,OK
	[
	"username1",
	"username2",
	"username3",
	"username4",
	"username5"
	]
 
5.	System allows a user to accept an invitation, becoming friends

	POST /users/{username}/friends/invitations/accept/{requestor}
	
	result: 200, OK
 
6.	System allows a user to see his network (friends, friends of his friends, and so on)

	GET /users/{username}/friends/explore/users
	result: 200,OK
	[
	"username1",
	"username2",
	"username3",
	"username4",
	"username5"
	]

		GET /users/{username}/friends/explore/network
	result: 200,OK
	[
	"username1",
	"username2",
	"username3",
	"username4",
	"username5"
	]

7.	System allows un-friending (user A cancels friendship with user B)

	DELETE /users/{username}/frends/remove/{friendToRemove}
	
	result: 200, OK

8.	System allows checking a distance factor in a network, which is:
a.	1 if user B is a friend of user A
b.	2 if user B is a friend of a friend of user A
c.	And so on

	GET /users/{username}/friends/distanceFactor/{targetUser}
	
	result: 200,OK
	{
	"distanceFactor": 3;
	}

9.	System allows a user to post a message

	POST /users/{username}/messages/post
	{
	"message":"message"
	}
	result: 200,OK

10.	System allows a user to retrieve a chronological list of messages from his friends

	GET /users/{name}/messages/friends

	result: 200,OK
	[
    {
		"user": "user1",
        "date": 1524810899385,
        "message": "1c49cc4a-0613-4261-90f8-c2075537c419"
    },
    {
		"user": "user2",
        "date": 1524810899409,
        "message": "1465df91-8d24-42a4-afc2-83498782d8ce"
    },
    {
		"user": "user3",
        "date": 1524810899458,
        "message": "555256aa-4ef3-4fad-a215-77435a53f0d6"
    },
    {
		"user": "user4",
        "date": 1524810899467,
        "message": "097d5b30-d5ea-4c51-99df-0ea3612d3ebf"
    },
    {
		"user": "user5",
        "date": 1524810899590,
        "message": "47c87bb9-9114-40f3-b956-891b1302b509"
    },
	]

11.	System allows a user to retrieve a chronological list of messages from his network

	GET /users/{name}/messages/network

	result: 200,OK
	[
    {
		"user": "user1",
        "date": 1524810899385,
        "message": "1c49cc4a-0613-4261-90f8-c2075537c419"
    },
    {
		"user": "user2",
        "date": 1524810899409,
        "message": "1465df91-8d24-42a4-afc2-83498782d8ce"
    },
    {
		"user": "user3",
        "date": 1524810899458,
        "message": "555256aa-4ef3-4fad-a215-77435a53f0d6"
    },
    {
		"user": "user4",
        "date": 1524810899467,
        "message": "097d5b30-d5ea-4c51-99df-0ea3612d3ebf"
    },
    {
		"user": "user5",
        "date": 1524810899590,
        "message": "47c87bb9-9114-40f3-b956-891b1302b509"
    },
	]
