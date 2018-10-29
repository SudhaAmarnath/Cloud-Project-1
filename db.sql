create table APP_USER (
   id BIGINT NOT NULL AUTO_INCREMENT,
   sso_id VARCHAR(30) NOT NULL,
   password VARCHAR(100) NOT NULL,
   first_name VARCHAR(30) NOT NULL,
   last_name  VARCHAR(30) NOT NULL,
   email VARCHAR(30) NOT NULL,
   PRIMARY KEY (id),
   UNIQUE (sso_id)
);
   
create table USER_PROFILE(
   id BIGINT NOT NULL AUTO_INCREMENT,
   type VARCHAR(30) NOT NULL,
   PRIMARY KEY (id),
   UNIQUE (type)
);
   
CREATE TABLE APP_USER_USER_PROFILE (
    user_id BIGINT NOT NULL,
    user_profile_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, user_profile_id),
    CONSTRAINT FK_APP_USER FOREIGN KEY (user_id) REFERENCES APP_USER (id),
    CONSTRAINT FK_USER_PROFILE FOREIGN KEY (user_profile_id) REFERENCES USER_PROFILE (id)
);

create table USER_DOCUMENT(
   id BIGINT NOT NULL AUTO_INCREMENT,
   user_id BIGINT NOT NULL,
   name  VARCHAR(100) NOT NULL,
   description VARCHAR(255) ,
   type VARCHAR(100) NOT NULL,
   content longblob NOT NULL,
   PRIMARY KEY (id),
   CONSTRAINT document_user FOREIGN KEY (user_id) REFERENCES APP_USER (id) ON UPDATE CASCADE ON DELETE CASCADE
);
  
INSERT INTO USER_PROFILE(type)
VALUES ('USER');
  
INSERT INTO USER_PROFILE(type)
VALUES ('ADMIN');
  
INSERT INTO USER_PROFILE(type)
VALUES ('DBA');

CREATE TABLE persistent_loginsapp_user (
    username VARCHAR(64) NOT NULL,
    series VARCHAR(64) NOT NULL,
    token VARCHAR(64) NOT NULL,
    last_used TIMESTAMP NOT NULL,
    PRIMARY KEY (series)
);

  
INSERT INTO APP_USER(sso_id, password, first_name, last_name, email)
VALUES ('admin','$2a$10$KzBFVBAu/0JAOoNjKojntu9b0jawEM0doupArLzRHK0HyfVbAqEuy', 'Admin','User','admin@gmail.com');
  
INSERT INTO APP_USER_USER_PROFILE (user_id, user_profile_id)
  SELECT user.id, profile.id FROM APP_USER user, USER_PROFILE profile
  where user.sso_id='admin' and profile.type='ADMIN';


  
