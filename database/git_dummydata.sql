-- This script adds dummy data to the schema for testing purposes

-- Student Dummy Data
INSERT INTO gitdb.Student(email)
VALUES ("hbak0001@student.monash.edu");

INSERT INTO gitdb.Student(email)
VALUES ("ewil0007@student.monash.edu");

INSERT INTO gitdb.Student(email)
VALUES ("kses0002@student.monash.edu");

INSERT INTO gitdb.Student(email)
VALUES ("sysoo9@student.monash.edu");

-- Project Dummy Data
INSERT INTO gitdb.Project(projectId)
VALUES (1);

INSERT INTO gitdb.Project(projectId)
VALUES (2);

INSERT INTO gitdb.Project(projectId)
VALUES (3);

INSERT INTO gitdb.Project(projectId)
VALUES (4);

-- StudentProject Dummy Data

INSERT INTO gitdb.StudentProject(emailStudent, projectId)
VALUES ("hbak0001@student.monash.edu", 1);

INSERT INTO gitdb.StudentProject(emailStudent, projectId)
VALUES ("hbak0001@student.monash.edu", 2);

INSERT INTO gitdb.StudentProject(emailStudent, projectId)
VALUES ("hbak0001@student.monash.edu", 3);

INSERT INTO gitdb.StudentProject(emailStudent, projectId)
VALUES ("ewil0007@student.monash.edu", 3);

INSERT INTO gitdb.StudentProject(emailStudent, projectId)
VALUES ("sysoo9@student.monash.edu", 4);

-- Repo Data

INSERT INTO gitdb.Repository(url, service)
VALUES ("https://github.com/HBAK0001/fit3157-asgn2", "GitHub");

INSERT INTO gitdb.Repository(url, service)
VALUES ("https://github.com/LaiSteph/FIT3157-Assignment3", "GitHub");

-- ProjectRepo Data

INSERT INTO gitdb.ProjectRepo(idRepo, projectId)
VALUES ("https://github.com/HBAK0001/fit3157-asgn2", 1);

INSERT INTO gitdb.ProjectRepo(idRepo, projectId)
VALUES ("https://github.com/LaiSteph/FIT3157-Assignment3", 1);