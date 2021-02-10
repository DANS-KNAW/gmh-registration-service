-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema nbnresolver
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema nbnresolver
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `nbnresolver` DEFAULT CHARACTER SET utf8 ;
USE `nbnresolver` ;

-- -----------------------------------------------------
-- Table `nbnresolver`.`registrant`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nbnresolver`.`registrant` (
  `registrant_id` INT(11) NOT NULL AUTO_INCREMENT,
  `registrant_groupid` VARCHAR(255) NOT NULL,
  `created` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`registrant_id`),
  UNIQUE INDEX `registrant_groupid_UNIQUE` (`registrant_groupid` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `nbnresolver`.`credentials`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nbnresolver`.`credentials` (
  `credentials_id` INT(11) NOT NULL AUTO_INCREMENT,
  `registrant_id` INT(11) NOT NULL,
  `org_prefix` VARCHAR(45) NOT NULL,
  `username` VARCHAR(150) NULL DEFAULT NULL,
  `password` VARCHAR(150) NULL DEFAULT NULL,
  `token` VARCHAR(200) NULL DEFAULT NULL,
  PRIMARY KEY (`credentials_id`),
  INDEX `FK_registrant_id_idx` (`registrant_id` ASC) VISIBLE,
  CONSTRAINT `FK_registrant_id`
    FOREIGN KEY (`registrant_id`)
    REFERENCES `nbnresolver`.`registrant` (`registrant_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `nbnresolver`.`identifier`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nbnresolver`.`identifier` (
  `identifier_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `identifier_value` VARCHAR(510) NOT NULL,
  PRIMARY KEY (`identifier_id`),
  UNIQUE INDEX `identifier_value_UNIQUE` (`identifier_value` ASC) VISIBLE,
  INDEX `idxIdentifierValue` (`identifier_value` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `nbnresolver`.`location`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nbnresolver`.`location` (
  `location_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `location_url` VARCHAR(1022) NOT NULL,
  PRIMARY KEY (`location_id`),
  INDEX `idxLocationUrl` (`location_url` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `nbnresolver`.`identifier_location`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nbnresolver`.`identifier_location` (
  `location_id` BIGINT(20) NOT NULL,
  `identifier_id` BIGINT(20) NOT NULL,
  `last_modified` TIMESTAMP(4) NOT NULL DEFAULT CURRENT_TIMESTAMP(4),
  `isFailover` TINYINT(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`identifier_id`, `location_id`),
  INDEX `FkLocation_idx` (`location_id` ASC) VISIBLE,
  CONSTRAINT `FkIdentifier`
    FOREIGN KEY (`identifier_id`)
    REFERENCES `nbnresolver`.`identifier` (`identifier_id`),
  CONSTRAINT `FkLocation`
    FOREIGN KEY (`location_id`)
    REFERENCES `nbnresolver`.`location` (`location_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `nbnresolver`.`identifier_registrant`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nbnresolver`.`identifier_registrant` (
  `identifier_id` BIGINT(20) NOT NULL,
  `registrant_id` INT(11) NOT NULL,
  PRIMARY KEY (`identifier_id`, `registrant_id`),
  INDEX `FK_tbl_registrant_idx` (`registrant_id` ASC) VISIBLE,
  CONSTRAINT `FK_tbl_identifier`
    FOREIGN KEY (`identifier_id`)
    REFERENCES `nbnresolver`.`identifier` (`identifier_id`),
  CONSTRAINT `FK_tbl_registrant`
    FOREIGN KEY (`registrant_id`)
    REFERENCES `nbnresolver`.`registrant` (`registrant_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `nbnresolver`.`location_registrant`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nbnresolver`.`location_registrant` (
  `location_id` BIGINT(20) NOT NULL,
  `registrant_id` INT(11) NOT NULL,
  PRIMARY KEY (`location_id`, `registrant_id`),
  INDEX `FK_Registrant_idx` (`registrant_id` ASC) VISIBLE,
  CONSTRAINT `FK_Location`
    FOREIGN KEY (`location_id`)
    REFERENCES `nbnresolver`.`location` (`location_id`),
  CONSTRAINT `FK_Registrant`
    FOREIGN KEY (`registrant_id`)
    REFERENCES `nbnresolver`.`registrant` (`registrant_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

USE `nbnresolver` ;

-- -----------------------------------------------------
-- procedure deleteNbnObject
-- -----------------------------------------------------

DELIMITER $$
USE `nbnresolver`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteNbnObject`(IN nbn_value VARCHAR(150))
BEGIN

START TRANSACTION;

SET FOREIGN_KEY_CHECKS=0;

DELETE I, IL, IR, L, LR
FROM identifier_location IL
INNER JOIN identifier I ON i.identifier_id = IL.identifier_id
INNER join identifier_registrant IR ON IR.identifier_id = I.identifier_id
INNER JOIN location_registrant LR ON LR.registrant_id = IR.registrant_id
INNER JOIN location L ON L.location_id = LR.location_id
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
CREATE DEFINER=`root`@`localhost` PROCEDURE `insertNbnObject`(IN nbn_value VARCHAR(150),IN nbn_location VARCHAR(150), IN registrant_id INT(11), IN failover BOOLEAN)
BEGIN

DECLARE identifier_id BIGINT(20);
DECLARE location_id BIGINT(20);

START TRANSACTION;

INSERT INTO identifier (identifier.identifier_value) VALUES (nbn_value);
SET identifier_id = LAST_INSERT_ID();
INSERT INTO location (location.location_url) VALUES (nbn_location);
SET location_id = LAST_INSERT_ID();
INSERT INTO identifier_registrant (identifier_registrant.identifier_id, identifier_registrant.registrant_id) VALUES (identifier_id, registrant_id );
INSERT INTO identifier_location (identifier_location.location_id, identifier_location.identifier_id, identifier_location.last_modified, identifier_location.isFailover) VALUES (location_id , identifier_id , NOW() , failover);
INSERT INTO location_registrant (location_registrant.location_id, location_registrant.registrant_id) VALUES (location_id, registrant_id );

COMMIT;

END$$

DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
