CREATE DATABASE IF NOT EXISTS hpccbuilder;
  
USE hpccbuilder;

DROP TABLE IF EXISTS eclbuilderuser;

CREATE TABLE eclbuilderuser (
  userIndex int(11) NOT NULL DEFAULT '0',
  userid varchar(45) DEFAULT NULL,
  password varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS eclbuilder;

CREATE TABLE eclbuilder (
  author varchar(45) NOT NULL,
  name varchar(45) NOT NULL,
  logicalFiles varchar(2000) DEFAULT NULL,
  lastmodifieddate TIMESTAMP,
  eclbuildercode varchar DEFAULT '',
  hpccConnId varchar(45) DEFAULT NULL,
  wuid varchar(45) DEFAULT NULL,
  datasetFields varchar DEFAULT '',
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO eclbuilderuser VALUES(0,'raja', 'raja');
INSERT INTO eclbuilderuser VALUES(1,'ashoka', 'ashoka');
INSERT INTO eclbuilderuser VALUES(2,'narasimha', 'narasimha');
INSERT INTO eclbuilderuser VALUES(3,'bhuvi', 'bhuvi');
INSERT INTO eclbuilderuser VALUES(3,'arjuna', 'arjuna');
INSERT INTO eclbuilderuser VALUES(3,'user1', 'user1pw');
