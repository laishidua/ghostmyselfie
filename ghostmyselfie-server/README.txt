
SERVER COMPILATION AND EXECUTION

- Open server source as gradle project.
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

Depends on server "ghostmyselfie" and "ghost" directory can get different path. In tomcat server the defautl path could be <TOMCAT HOME PATH>/








