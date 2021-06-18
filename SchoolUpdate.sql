CREATE TABLE UserType (
    uuid int PRIMARY KEY,
    name varchar(255)
);

CREATE TABLE SchoolClass (
    uuid int PRIMARY KEY,
    name varchar(255)
);

CREATE TABLE User (
    uuid int PRIMARY KEY,
    username varchar(255),
    password varchar(255),
    userTypeID int,
    schoolClassID int,
    loggedIn boolean,
	FOREIGN KEY(userTypeID) REFERENCES UserType(uuid),
	FOREIGN KEY(schoolClassID) REFERENCES SchoolClasses(uuid)
);

INSERT INTO UserType(uuid, name) VALUES(1, 'teacher')
INSERT INTO UserType(uuid, name) VALUES(2, 'student')
INSERT INTO UserType(uuid, name) VALUES(3, 'admin')

INSERT INTO SchoolClass(uuid, name) VALUES(1, 'Math')
INSERT INTO SchoolClass(uuid, name) VALUES(2, 'English')
INSERT INTO SchoolClass(uuid, name) VALUES(3, 'Art')
INSERT INTO SchoolClass(uuid, name) VALUES(4, 'None')

INSERT INTO User(uuid, username, password, userTypeID, schoolClassID, loggedIn) VALUES(1, 'michael', 'passwd', 1, 4, false)
INSERT INTO User(uuid, username, password, userTypeID, schoolClassID, loggedIn) VALUES(2, 'sarah', '123456', 1, 4, false)
INSERT INTO User(uuid, username, password, userTypeID, schoolClassID, loggedIn) VALUES(3, 'bob', 'helloworld', 3, 4, false)
INSERT INTO User(uuid, username, password, userTypeID, schoolClassID, loggedIn) VALUES(4, 'bill', 'Password123', 2, 1, false)
INSERT INTO User(uuid, username, password, userTypeID, schoolClassID, loggedIn) VALUES(5, 'thomas', 'passpasspass', 2, 2, false)
INSERT INTO User(uuid, username, password, userTypeID, schoolClassID, loggedIn) VALUES(6, 'daisy', 'pa55w0rd', 2, 2, false)
INSERT INTO User(uuid, username, password, userTypeID, schoolClassID, loggedIn) VALUES(7, 'phil', '123', 2, 1, false)
INSERT INTO User(uuid, username, password, userTypeID, schoolClassID, loggedIn) VALUES(8, 'william', '456', 2, 3, false)

select * from User;

CREATE TABLE Test (
    uuid int PRIMARY KEY,
    schoolClassID int,
	FOREIGN KEY(schoolClassID) REFERENCES SchoolClasses(uuid)
);

CREATE TABLE Result (
    uuid int PRIMARY KEY,
    schoolClassID int,
    testID int,
    firstName varchar(250),
    lastName varchar(250),
    DoB date,
	FOREIGN KEY(schoolClassID) REFERENCES SchoolClasses(uuid),
	FOREIGN KEY(testID) REFERENCES Test(uuid)
);

CREATE TABLE TestQuestionText (
    uuid int PRIMARY KEY autoincrement,
    questionNum int,
    testID int,
    question varchar(250),
    answer varchar(250),
	FOREIGN KEY(testID) REFERENCES Test(uuid)
);

CREATE TABLE TestQuestionMultichoice (
    uuid int PRIMARY KEY autoincrement,
    questionNum int,
    testID int,
    question varchar(250),
    incorrectAnswers varchar(250),
    correctAnswers varchar(250),
	FOREIGN KEY(testID) REFERENCES Test(uuid)
);

select * from Test;