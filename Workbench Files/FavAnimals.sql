### ---| FavAnimals |--- ###

DROP DATABASE IF EXISTS TermProject;
CREATE DATABASE TermProject;
USE TermProject;

DROP TABLE FavAnimals;

CREATE TABLE FavAnimals(
	animalID INTEGER NOT NULL,
	whoFavdAnimal varchar(30) NOT NULL, 
	PRIMARY KEY (animalID, whoFavdAnimal),
	FOREIGN KEY (animalID) REFERENCES Animals(animalID) ON DELETE CASCADE,
	FOREIGN KEY (whoFavdAnimal) REFERENCES Users(username) ON DELETE CASCADE
); 

INSERT INTO FavAnimals (animalID, whoFavd) values
		(1, 'user_2');

SELECT * FROM FavAnimals;