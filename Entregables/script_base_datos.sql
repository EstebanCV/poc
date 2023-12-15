CREATE TABLE users (
       id           NUMBER(10) NOT NULL,
       name         VARCHAR2(50),
       email        VARCHAR2(50),
       password     VARCHAR2(50),
       token        VARCHAR2(255),
       active       VARCHAR2(1),
       created      VARCHAR2(50),
       last_login   VARCHAR2(50),
       modified     VARCHAR2(50)
);

CREATE TABLE contact (
         id               NUMBER(10) NOT NULL,
         city_code        VARCHAR2(50),
         country_code     VARCHAR2(50),
         number_user      VARCHAR2(50),
         id_user          RAW(20)
);

ALTER TABLE users ADD CONSTRAINT users_pk PRIMARY KEY (id);
ALTER TABLE contact ADD CONSTRAINT contacts_pk PRIMARY KEY (id);

ALTER TABLE contact ADD CONSTRAINT users_fk FOREIGN KEY (id_user) REFERENCES users (id);