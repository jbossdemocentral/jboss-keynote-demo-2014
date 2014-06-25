
DROP TABLE IF EXISTS tweets;

CREATE TABLE tweets (
	message 		VARCHAR(250)  NULL, 
	salesforce 	VARCHAR(50)  NULL, 
	tag   			VARCHAR(50)  NULL, 
	tweetDate		TIMESTAMP, 
	uniqueName	VARCHAR(50)  NULL, 
	user				VARCHAR(250)  NULL, 
	id					BIGINT,
  PRIMARY KEY (id)
);


DROP TABLE IF EXISTS tweetUsers;


CREATE TABLE tweetUsers (
	id	VARCHAR(250) Not NULL,
	email VARCHAR(250)  NULL,
	mobilePhone VARCHAR(250)  NULL,
	firstName VARCHAR(250)  NULL,
	lastName VARCHAR(250)  NULL,
	dept VARCHAR(250)  NULL, 
	
	PRIMARY KEY (id)
);

