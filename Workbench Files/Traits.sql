### --| TRAITS |-- ###

DROP DATABASE IF EXISTS TermProject;
CREATE DATABASE TermProject;
USE TermProject;

DROP TABLE traits;

CREATE TABLE traits(
	trait varchar(60) NOT NULL,
	animalID INTEGER NOT NULL,
	PRIMARY KEY (trait, animalID),
	FOREIGN KEY (animalID) REFERENCES Animals (animalID) ON DELETE CASCADE
);

INSERT INTO traits (trait, animalID) values
		('happy hyper', 1),
        ('happy drooly hyper', 2),
        ('rude', 3),
        ('calm scratchy soft', 4),
        ('happy drooly hyper', 5),
        ('happy drooly hyper', 6),
        ('happy drooly hyper', 7),
        ('dramatic', 8),
        ('happy drooly hyper', 9),
        ('happy drooly hyper', 10);
        
SELECT * FROM traits;

SELECT LAST_INSERT_ID() FROM animals;