
1.- Document explaining in detail how is implemented the Concurrent Daily Selfie specification:

ConcurrentDailySelfieFinalSpecification.pdf

2.- PDF with that maps the project requirements:

ConcurrentDailySelfieMap.pdf

3.- The complete project source code and associated files needed to build the project:

CLIENT:

ghostmyselfie-client folder.

SERVER:

ghostmyselfie-server folder.

4.- Extra if you want to install the app and test it in your device:

apk folder. 

Just for devices with Android 4.4.2 to 5.1.1.

--------------------------------------------------------- 
FOR CLIENT:

APP SUPPORTS FROM 19 TO 22 ANDROID SDK VERSION.

Open client source code as android project.

COMPILE AND EXECUTE:  

ADD BUTTERKNIFE LIBS: http://jakewharton.github.io/butterknife/ide-eclipse.html

Change the path in com.laishidua.utils.Constants class for the server URL.

Deploy to android device as usual.

-----------------------------------------------------------

FOR SERVER:

SERVER COMPILATION AND EXECUTION

- Open server source code as gradle project.
- Create a MySQL database and name it ghostmyselfie
- In src/main/resources change database credentials in application.properties and hibernate.properties.
- In com.laishidua.mobilecloud.ghostmyselfie package change database credentials too.
- Execute the server using build.gradle file with "wrapper deploy" command.

Once the server is executed and has created the tables, execute in the database:

insert into user(id, email, password, username) values (1, "host@ghostmyselfie.com", "gffgftfu", "host@ghostmyselfie.com");
insert into role(id, name) values (1,"ROLE_CLIENT");
insert into role(id, name) values (2,"ROLE_TRUSTED_CLIENT");
insert into user_roles(user_id, roles_id) values (1,1);
insert into user_roles(user_id, roles_id) values (1,2);

This will insert a user to register other users using OAUTH 2.

Depends on server "ghostmyselfie" and "ghost" directory can get different path. In tomcat server the defautl path could be <TOMCAT HOME PATH>/

-----------------------------------------------------------------

To test the client and server you may want to change your server SSL keystore and put it in the src/main/resources/keystore from the ghostmyselfie-server.





