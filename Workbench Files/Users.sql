### --| USERS |-- ###

DROP DATABASE IF EXISTS TermProject;
CREATE DATABASE TermProject;
USE TermProject;

DROP TABLE users;

CREATE TABLE users(
		id INTEGER NOT NULL AUTO_INCREMENT,
		username varchar(30) NOT NULL,
		password varchar(30) NOT NULL,
		firstName varchar(20) DEFAULT 'Anonymous',
		lastName varchar(20),
		email varchar(60) NOT NULL,
		PRIMARY KEY (id),
		UNIQUE KEY (username),
        UNIQUE KEY (email)
);

INSERT INTO users (username, password, firstName, lastName, email) values 
		('user_0', 'pass_0', 'FName0', 'LName0', 'email.com') ;

INSERT INTO users (username, password, firstName, lastName, email) values 
		('user_0', 'pass_0', 'FName0', 'LName0', '0@email.com'),
		('user_1', 'pass_1', 'FName1', 'LName1', '1@email.com'),
		('user_2', 'pass_2', 'FName2', 'LName2', '2@email.com'),
		('user_3', 'pass_3', 'FName3', 'LName3', '3@email.com'),
		('user_4', 'pass_4', 'FName4', 'LName4', '4@email.com'),
		('user_5', 'pass_5', 'FName5', 'LName5', '5@email.com'),
		('user_6', 'pass_6', 'FName6', 'LName6', '6@email.com'),
		('user_7', 'pass_7', 'FName7', 'LName7', '7@email.com'),
		('user_8', 'pass_8', 'FName8', 'LName8', '8@email.com'),
		('user_9', 'pass_9', 'FName9', 'LName9', '9@email.com') ;

SELECT * FROM users;