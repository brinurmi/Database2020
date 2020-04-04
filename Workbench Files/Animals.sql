### --| ANIMALS |-- ###

DROP DATABASE IF EXISTS TermProject;
CREATE DATABASE TermProject;
USE TermProject;

DROP TABLE animals;

CREATE TABLE animals(
		animalID INTEGER NOT NULL AUTO_INCREMENT,
		name varchar(30),
		species varchar(10) NOT NULL,
		birthDate varchar(10),
		adoptionPrice INTEGER DEFAULT 0,
		ownerUsername varchar(30),
		PRIMARY KEY (animalID),
        FOREIGN KEY (ownerUsername) REFERENCES Users (username)
);

INSERT INTO animals (name, species, birthDate, adoptionPrice, ownerUsername) values
		('Animal_1', 'Cat', '2018/1/01', 10, 'user_1');

INSERT INTO animals(name, species, birthDate, adoptionPrice, ownerUsername) values
		('Animal_1', 'Cat', '2018/1/01', 10, 'user_1'),
		('Animal_2', 'Dog', '2018/2/01', 20, 'user_1'),
		('Animal_3', 'Cat', '2018/3/01', 30, 'user_1'),
		('Animal_4', 'Dog', '2018/4/01', 40, 'user_1'),
		('Animal_5', 'Cat', '2019/5/01', 50, 'user_1'),
		('Animal_6', 'Dog', '2019/6/01', 60, 'user_2'),
		('Animal_7', 'Cat', '2019/7/01', 70, 'user_2'),
		('Animal_8', 'Dog', '2019/8/01', 80, 'user_2'),
		('Animal_9', 'Cat', '2020/9/01', 90, 'owner_9'),
		('Animal_10', 'Dog', '2020/10/01', 100, 'owner_10') ; 

SELECT * FROM animals;
SELECT COUNT(*) FROM animals WHERE ownerUsername = 'user_1';

# To Test/Demonstrate adding Traits:
INSERT INTO animals (name, species, birthDate, adoptionPrice, ownerUsername) values
		('ID_Test', 'Pangolin', '2018/1/01', 0, 'User_9');
        
SELECT LAST_INSERT_ID() FROM animals;


