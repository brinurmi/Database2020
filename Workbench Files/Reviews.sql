### --| REVIEWS |-- ###

DROP DATABASE IF EXISTS TermProject;
CREATE DATABASE TermProject;
USE TermProject;

DROP TABLE reviews;

CREATE TABLE reviews(
	animalID INTEGER NOT NULL,
	authorUsername varchar(30) NOT NULL,
	rating varchar(9),
	comments varchar(140),
	PRIMARY KEY (animalID),
	FOREIGN KEY (animalID) REFERENCES Animals(animalID) ON DELETE CASCADE,
	FOREIGN KEY (authorUsername) REFERENCES Users(username) ON DELETE CASCADE
);

INSERT INTO reviews (animalID, authorUsername, rating, comments) values
		(1, 'user_2', 'Adorbs', '14/10 would boop again!');

SELECT * FROM reviews;