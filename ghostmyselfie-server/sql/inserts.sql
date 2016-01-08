insert into user(id, email, password, username) values (1, "host@ghostmyselfie.com", "gffgftfu", "host@ghostmyselfie.com");
insert into role(id, name) values (1,"ROLE_CLIENT");
insert into role(id, name) values (2,"ROLE_TRUSTED_CLIENT");
insert into user_roles(user_id, roles_id) values (1,1);
insert into user_roles(user_id, roles_id) values (1,2);
