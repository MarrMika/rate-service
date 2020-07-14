SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema rate_db
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `rates` DEFAULT CHARACTER SET utf8 ;
USE `rates` ;


-- -----------------------------------------------------
-- Table `rate_db`.`currencies`
-- -----------------------------------------------------
CREATE TABLE rates.currencies (
    id VARCHAR(32) NOT NULL,
    isoCode VARCHAR(3) NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX isoCode_UNIQUE (`isoCode` ASC),
    UNIQUE INDEX name_UNIQUE (`name` ASC)
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `rate_db`.`exchangeRates`
-- -----------------------------------------------------
CREATE TABLE rates.exchangeRates (
    id VARCHAR(32) NOT NULL,
    currencyId VARCHAR(32) NOT NULL,
    exchangeRate FLOAT NOT NULL,
    rateDate DATE NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;