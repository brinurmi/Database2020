### --| FavBreeders |-- ###

DROP DATABASE IF EXISTS TermProject;
CREATE DATABASE TermProject;
USE TermProject;

DROP TABLE FavBreeders;

CREATE TABLE IF NOT EXISTS FavBreeders(
	breederUsername varchar(30) NOT NULL,
	whoFavdBreeder varchar(30) NOT NULL,
	PRIMARY KEY (breederUsername, whoFavdBreeder),
	FOREIGN KEY (breederUsername) REFERENCES Users(username) ON DELETE CASCADE,
	FOREIGN KEY (whoFavdBreeder) REFERENCES Users(username) ON DELETE CASCADE
); 

INSERT INTO FavBreeders (breederUsername, whoFavdBreeder) values
		('user_2', 'user_8');

SELECT * FROM FavBreeders;
