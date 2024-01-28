# event-planner
This repo contains event planner application backend codebase.
----------------
Technical Stacks
----------------
- Java 11
- Spring Boot 2
- Oracle 21C database

----------------
Database Schema
----------------
There 2 tables used in this application
- EVENT - Store the event details for which the organizer collecting the user openions
- EVENT_USER_RESPONSE - Responses of all the users are store in this table with respect to the event id.

Please refer the ER Diagram to check the complete schema of the database

--------------
API Contracts
--------------
In total there are 4 REST APIs in this application

- POST <baseurl>/v1/event : This API is to create an event in the database. Organizer of the event will invoke this API

- PUT <baseurl>/v1/event : Organizer will use this API to update the existing event session status - Open session / Close session

- GET <baseurl>/v1/event/{eventId} : This API will get all the details of the event including all the responses the event received till that time

- POST <baseurl>/v1/event/{eventId} : User will use this API to post his response to the perticular event in session time

Complete API information including request and response object details are provided in the swagger.yaml file.

-------------------
Setup Instructions
-------------------
This application require maven and oracle database as pre-requisites.

- Installation and setup instructions for official maven can be found here https://maven.apache.org/install.html

- Oracle database installation and setup instructions can be found here https://docs.oracle.com/en/database/oracle/oracle-database/21/install-and-upgrade.html
	
	Steps to create user in oracle database
		1. alter session set "_ORACLE_SCRIPT"=true;
		2. CREATE USER <user_name> IDENTIFIED BY <password>;
		3. GRANT ALL PRIVILEGES TO <user_name>

-----------------------
Evnironmental variables
-----------------------
After installing oracle database need to create a user and import the DB_Schema.sql into the database by connecting to the user. Update the properties inside application.properties file with respective values 

spring.datasource.url=<Replace this with your jdbc url. example "jdbc:oracle:thin:@localhost:1521:xe">
spring.datasource.username=<user_name>
spring.datasource.password=<password>

--------
Logging
--------
All the logs in the application including incoming requests and responses will be save into a file. Change the logging properties by updating the below environmental variables inside application.properties

logging.file.path=/logs/
logging.file.name=event-planner.log
logging.level.org.springframework=INFO
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

---------------------
Solution description (How the application works)
---------------------
 a) By calling create event API (i.e POST <baseurl>/v1/event) the organizer will create event inside the database with the details like event name, organizer name, organizer email
 
   Backend will create an event entry into event table with the organizer details as well as an auto generated event secret and Session status as 'NEW'
 
   The event secret will be usefull to authorize the organizer at the time of changing session status.
   
   Organizer will share event id with all the users offline.
   
 b) An event with 'NEW' status won't accept any responses from the user. The organizer will call an API (i.e PUT <baseurl>/v1/event) to change the session status to 'ACTIVE' with event secret, event id and session status as 'ACTIVE'.
	
	Now the event with 'ACTIVE' session status will start accepting the responses from the user.
	
	- Reason behind the status change implementation is to give the organizer enough time to share the event id will all the customers offline before starting the session
 
 c) User will call GET <baseurl>/v1/event/{eventId} with the eventId shared by organizer to check the event details and also other user responses till that time.
 
 d) After checking the details and other's responses the user will call POST <baseurl>/v1/event/{eventId} with his name, email and response to record.
 
 e) Once the organizer feels that it is time to close the session he/she will call the same status change API called in step b with session status as 'CLOSE'
 
	As soon as the status change to close backend logic will pick a random response from all the responses and store it into the event table as finalized response.
	
	The event with 'CLOSE' session status won't accept the responses from users.
	
	Organizer can reopen the event for the responses whenever he/she want to. All the events and responses will be stored in the database.

----------------------
Alternative Solutions	
----------------------
There are few alternative solutions that come to my mind when I first receive the requirements but I ruled out them due to some reasons.

1) Long Polling solution : Normally to notify the user about the final decision without making him to call an other API we can use this Long Polling solution. But the below points ruled out this solution
		- Long polling is a technique of holding the API response until any update come or request timeout. This will give the application too much burden as the request freequency will be higher in some cases when users are more and session will last for lesser time
		- As we don't have any user interface now it is difficult to demostrate this solution
		- Also to be production ready as per my research some deployment platforms will not allow the request after certain time in idle in such situations user has to call the API again and again till they receive the response
		
2) Caching the responses inside application memory using Redis: This will save time of storing the responses in database. But this also will get the below issues
		- Normally cache will give high performance when there is high volume of read operations. But in our case we need to write the user responses into the cache and also read responses. Thought cache is not a better solution for read-write operations
		- As we store the event details in db and responses in cache in this solution. We need to make 2 transactions to the 2 different sources for every API call. This will be an overhead for the application
		
3) In-Memory DB also a solution in this kind of requirement to boost the performance but In-Memory DB is not reliable in production evnironments as we will be building the application production ready.		
 
