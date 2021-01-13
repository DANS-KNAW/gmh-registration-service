CREATE DATABASE  IF NOT EXISTS `nbnresolver` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `nbnresolver`;
-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: tgharvester31.dans.knaw.nl    Database: tst_nbnresolver
-- ------------------------------------------------------
-- Server version	5.5.5-10.3.17-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `identifier`
--

DROP TABLE IF EXISTS `identifier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `identifier` (
  `identifier_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `identifier_value` varchar(510) NOT NULL,
  PRIMARY KEY (`identifier_id`),
  UNIQUE KEY `identifier_value_UNIQUE` (`identifier_value`),
  KEY `idxIdentifierValue` (`identifier_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `identifier_location`
--

DROP TABLE IF EXISTS `identifier_location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `identifier_location` (
  `location_id` bigint(20) NOT NULL,
  `identifier_id` bigint(20) NOT NULL,
  `last_modified` timestamp(4) NOT NULL DEFAULT current_timestamp(4),
  `isFailover` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`identifier_id`,`location_id`),
  KEY `FkLocation_idx` (`location_id`),
  CONSTRAINT `FkIdentifier` FOREIGN KEY (`identifier_id`) REFERENCES `identifier` (`identifier_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FkLocation` FOREIGN KEY (`location_id`) REFERENCES `location` (`location_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `identifier_registrant`
--

DROP TABLE IF EXISTS `identifier_registrant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
-- Table structure for table `location`
--

DROP TABLE IF EXISTS `location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `location` (
  `location_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `location_url` varchar(1022) NOT NULL,
  PRIMARY KEY (`location_id`),
  UNIQUE KEY `location_url_UNIQUE` (`location_url`),
  KEY `idxLocationUrl` (`location_url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `location_registrant`
--

DROP TABLE IF EXISTS `location_registrant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
-- Table structure for table `registrant`
--

DROP TABLE IF EXISTS `registrant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `registrant` (
  `registrant_id` int(11) NOT NULL AUTO_INCREMENT,
  `registrant_groupid` varchar(255) NOT NULL,
  `created` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`registrant_id`),
  UNIQUE KEY `registrant_groupid_UNIQUE` (`registrant_groupid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-03-10 14:23:24
