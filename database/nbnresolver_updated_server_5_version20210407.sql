DROP DATABASE `nbnresolver`;

CREATE DATABASE  IF NOT EXISTS `nbnresolver` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `nbnresolver`;
-- MySQL dump 10.13  Distrib 8.0.18, for macos10.14 (x86_64)
--
-- Host: localhost    Database: nbnresolver
-- ------------------------------------------------------
-- Server version	5.7.23

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `credentials`
--

DROP TABLE IF EXISTS `credentials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `credentials` (
  `credentials_id` int(11) NOT NULL AUTO_INCREMENT,
  `registrant_id` int(11) NOT NULL,
  `org_prefix` varchar(45) NOT NULL,
  `username` varchar(150) DEFAULT NULL,
  `password` varchar(150) DEFAULT NULL,
  `token` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`credentials_id`),
  KEY `FK_registrant_id_idx` (`registrant_id`),
  CONSTRAINT `FK_registrant_id` FOREIGN KEY (`registrant_id`) REFERENCES `registrant` (`registrant_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `credentials`
--

LOCK TABLES `credentials` WRITE;
/*!40000 ALTER TABLE `credentials` DISABLE KEYS */;
/*!40000 ALTER TABLE `credentials` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `identifier`
--

DROP TABLE IF EXISTS `identifier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `identifier` (
  `identifier_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `identifier_value` varchar(510) NOT NULL,
  PRIMARY KEY (`identifier_id`),
  UNIQUE KEY `identifier_value_UNIQUE` (`identifier_value`),
  KEY `idxIdentifierValue` (`identifier_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `identifier`
--

LOCK TABLES `identifier` WRITE;
/*!40000 ALTER TABLE `identifier` DISABLE KEYS */;
/*!40000 ALTER TABLE `identifier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `identifier_location`
--

DROP TABLE IF EXISTS `identifier_location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `identifier_location` (
  `location_id` bigint(20) NOT NULL,
  `identifier_id` bigint(20) NOT NULL,
  `last_modified` timestamp(4) NOT NULL DEFAULT CURRENT_TIMESTAMP(4),
  `isFailover` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`identifier_id`,`location_id`),
  KEY `FkLocation_idx` (`location_id`),
  CONSTRAINT `FkIdentifier` FOREIGN KEY (`identifier_id`) REFERENCES `identifier` (`identifier_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FkLocation` FOREIGN KEY (`location_id`) REFERENCES `location` (`location_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `identifier_location`
--

LOCK TABLES `identifier_location` WRITE;
/*!40000 ALTER TABLE `identifier_location` DISABLE KEYS */;
/*!40000 ALTER TABLE `identifier_location` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `identifier_registrant`
--

DROP TABLE IF EXISTS `identifier_registrant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `identifier_registrant` (
  `identifier_id` bigint(20) NOT NULL,
  `registrant_id` int(11) NOT NULL,
  PRIMARY KEY (`identifier_id`,`registrant_id`),
  KEY `FK_tbl_registrant_idx` (`registrant_id`),
  CONSTRAINT `FK_tbl_identifier` FOREIGN KEY (`identifier_id`) REFERENCES `identifier` (`identifier_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_tbl_registrant` FOREIGN KEY (`registrant_id`) REFERENCES `registrant` (`registrant_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `identifier_registrant`
--

LOCK TABLES `identifier_registrant` WRITE;
/*!40000 ALTER TABLE `identifier_registrant` DISABLE KEYS */;
/*!40000 ALTER TABLE `identifier_registrant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `location`
--

DROP TABLE IF EXISTS `location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `location` (
  `location_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `location_url` varchar(1022) NOT NULL,
  PRIMARY KEY (`location_id`),
  UNIQUE KEY `location_url_UNIQUE` (`location_url`),
  KEY `idxLocationUrl` (`location_url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `location`
--

LOCK TABLES `location` WRITE;
/*!40000 ALTER TABLE `location` DISABLE KEYS */;
/*!40000 ALTER TABLE `location` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `location_registrant`
--

DROP TABLE IF EXISTS `location_registrant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `location_registrant` (
  `location_id` bigint(20) NOT NULL,
  `registrant_id` int(11) NOT NULL,
  PRIMARY KEY (`location_id`,`registrant_id`),
  KEY `FK_Registrant_idx` (`registrant_id`),
  CONSTRAINT `FK_Location` FOREIGN KEY (`location_id`) REFERENCES `location` (`location_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_Registrant` FOREIGN KEY (`registrant_id`) REFERENCES `registrant` (`registrant_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `location_registrant`
--

LOCK TABLES `location_registrant` WRITE;
/*!40000 ALTER TABLE `location_registrant` DISABLE KEYS */;
/*!40000 ALTER TABLE `location_registrant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registrant`
--

DROP TABLE IF EXISTS `registrant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `registrant` (
  `registrant_id` int(11) NOT NULL AUTO_INCREMENT,
  `registrant_groupid` varchar(255) NOT NULL,
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`registrant_id`),
  UNIQUE KEY `registrant_groupid_UNIQUE` (`registrant_groupid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registrant`
--

LOCK TABLES `registrant` WRITE;
/*!40000 ALTER TABLE `registrant` DISABLE KEYS */;
/*!40000 ALTER TABLE `registrant` ENABLE KEYS */;
UNLOCK TABLES;

USE `nbnresolver` ;

-- -----------------------------------------------------
-- procedure deleteNbnObject
-- -----------------------------------------------------

DELIMITER $$
USE `nbnresolver`$$
CREATE DEFINER=`nbnresolver_reg`@`localhost` PROCEDURE `deleteNbnObject`(IN nbn_value VARCHAR(150))
BEGIN

START TRANSACTION;

SET FOREIGN_KEY_CHECKS=0;

DELETE I, IL, IR, L, LR
from location L
INNER JOIN location_registrant LR ON LR.location_id = L.location_id
inner join identifier_location IL ON IL.location_id = L.location_id
INNER JOIN identifier I ON i.identifier_id = IL.identifier_id
INNER join identifier_registrant IR ON IR.identifier_id = I.identifier_id
WHERE I.identifier_value = nbn_value ;

SET FOREIGN_KEY_CHECKS=1;

COMMIT;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure insertNbnObject
-- -----------------------------------------------------

DELIMITER $$
USE `nbnresolver`$$
CREATE DEFINER=`nbnresolver_reg`@`localhost` PROCEDURE `insertNbnObject`(IN nbn_value VARCHAR(150),IN nbn_location VARCHAR(150), IN registrant_id INT(11), IN failover BOOLEAN)
BEGIN

DECLARE identifier_id BIGINT(20);
DECLARE location_id BIGINT(20);

START TRANSACTION;
SET identifier_id = (SELECT identifier.identifier_id from nbnresolver.identifier where identifier.identifier_value = nbn_value);

IF (identifier_id IS NULL) THEN
INSERT INTO identifier (identifier.identifier_value) VALUES (nbn_value);
SET identifier_id = LAST_INSERT_ID();
INSERT INTO identifier_registrant (identifier_registrant.identifier_id, identifier_registrant.registrant_id) VALUES (identifier_id, registrant_id );
END IF;

INSERT INTO location (location.location_url) VALUES (nbn_location);
SET location_id = LAST_INSERT_ID();

INSERT INTO identifier_location (identifier_location.location_id, identifier_location.identifier_id, identifier_location.last_modified, identifier_location.isFailover) VALUES (location_id , identifier_id , NOW() , failover);
INSERT INTO location_registrant (location_registrant.location_id, location_registrant.registrant_id) VALUES (location_id, registrant_id );

COMMIT;
END$$

DELIMITER ;


/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-04-07 14:26:53
