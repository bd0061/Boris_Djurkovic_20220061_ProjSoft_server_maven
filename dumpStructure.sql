/*
SQLyog Community v13.3.0 (64 bit)
MySQL - 10.4.27-MariaDB-log : Database - projektovanjesoftvera_seminarski_test2
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`projektovanjesoftvera_seminarski_test` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;

USE `projektovanjesoftvera_seminarski_test`;

/*Table structure for table `dozvola` */

DROP TABLE IF EXISTS `dozvola`;

CREATE TABLE `dozvola` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `kategorija` char(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

/*Table structure for table `iznajmljivanje` */

DROP TABLE IF EXISTS `iznajmljivanje`;

CREATE TABLE `iznajmljivanje` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `datumSklapanja` date NOT NULL,
  `ukupanIznos` double NOT NULL,
  `idZaposleni` int(11) DEFAULT NULL,
  `idVozac` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_iznajmljivanje_idZaposleni` (`idZaposleni`),
  KEY `idx_iznajmljivanje_idVozac` (`idVozac`),
  KEY `datumsklapanja` (`datumSklapanja`),
  CONSTRAINT `iznajmljivanje_ibfk_1` FOREIGN KEY (`idZaposleni`) REFERENCES `zaposleni` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `iznajmljivanje_ibfk_2` FOREIGN KEY (`idVozac`) REFERENCES `vozac` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2038 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

/*Table structure for table `stavkaiznajmljivanja` */

DROP TABLE IF EXISTS `stavkaiznajmljivanja`;

CREATE TABLE `stavkaiznajmljivanja` (
  `idIznajmljivanje` int(11) NOT NULL,
  `rb` int(11) NOT NULL,
  `datumPocetka` date NOT NULL,
  `datumZavrsetka` date NOT NULL,
  `iznos` double NOT NULL,
  `idVozilo` int(11) DEFAULT NULL,
  PRIMARY KEY (`idIznajmljivanje`,`rb`),
  KEY `idx_stavkaiznajmljivanja_idIznajmljivanje` (`idIznajmljivanje`),
  KEY `idx_stavkaiznajmljivanja_idVozilo` (`idVozilo`),
  CONSTRAINT `stavkaiznajmljivanja_ibfk_1` FOREIGN KEY (`idIznajmljivanje`) REFERENCES `iznajmljivanje` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `stavkaiznajmljivanja_ibfk_2` FOREIGN KEY (`idVozilo`) REFERENCES `vozilo` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

/*Table structure for table `termindezurstva` */

DROP TABLE IF EXISTS `termindezurstva`;

CREATE TABLE `termindezurstva` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `napomena` varchar(100) DEFAULT 'Nema napomene',
  `tipTermina` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

/*Table structure for table `vozac` */

DROP TABLE IF EXISTS `vozac`;

CREATE TABLE `vozac` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ime` varchar(100) NOT NULL,
  `prezime` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `idDozvola` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_email` (`email`),
  KEY `idx_vozac_idDozvola` (`idDozvola`),
  CONSTRAINT `vozac_ibfk_1` FOREIGN KEY (`idDozvola`) REFERENCES `dozvola` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=202 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

/*Table structure for table `vozilo` */

DROP TABLE IF EXISTS `vozilo`;

CREATE TABLE `vozilo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `klasa` varchar(50) NOT NULL,
  `proizvodjac` varchar(100) NOT NULL,
  `kupovnaCena` double NOT NULL,
  `godiste` int(11) NOT NULL,
  `imeModela` varchar(100) NOT NULL,
  `kategorija` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_imevozila` (`proizvodjac`,`imeModela`)
) ENGINE=InnoDB AUTO_INCREMENT=2001 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

/*Table structure for table `zaposleni` */

DROP TABLE IF EXISTS `zaposleni`;

CREATE TABLE `zaposleni` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ime` varchar(100) NOT NULL,
  `prezime` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `sifra` varchar(255) NOT NULL,
  `salt` varchar(255) NOT NULL,
  `admin` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=201 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

/*Table structure for table `zapter` */

DROP TABLE IF EXISTS `zapter`;

CREATE TABLE `zapter` (
  `datum` date NOT NULL,
  `idZaposleni` int(11) NOT NULL,
  `idTerminDezurstva` int(11) NOT NULL,
  `vanredan` tinyint(1) NOT NULL,
  `brojSati` int(11) NOT NULL,
  `fiksniBonus` int(11) NOT NULL,
  PRIMARY KEY (`datum`,`idZaposleni`,`idTerminDezurstva`),
  KEY `idx_zapter_idZaposleni` (`idZaposleni`),
  KEY `idx_zapter_idTerminDezurstva` (`idTerminDezurstva`),
  CONSTRAINT `zapter_ibfk_1` FOREIGN KEY (`idZaposleni`) REFERENCES `zaposleni` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `zapter_ibfk_2` FOREIGN KEY (`idTerminDezurstva`) REFERENCES `termindezurstva` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
