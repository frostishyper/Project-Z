-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: blackshores_bank
-- ------------------------------------------------------
-- Server version	8.0.43

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
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `accounts` (
  `Account_ID` int NOT NULL AUTO_INCREMENT,
  `Account_Number` varchar(15) NOT NULL,
  `Account_Email` varchar(255) NOT NULL,
  `Account_FirstName` varchar(100) NOT NULL,
  `Account_LastName` varchar(100) NOT NULL,
  `Account_Username` varchar(50) NOT NULL,
  `Account_Pin` char(64) NOT NULL,
  `Created_At` datetime DEFAULT CURRENT_TIMESTAMP,
  `Updated_At` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Is_Active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`Account_ID`),
  UNIQUE KEY `Account_Number` (`Account_Number`),
  UNIQUE KEY `Account_Email` (`Account_Email`),
  UNIQUE KEY `Account_Username` (`Account_Username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `listing_inclusions`
--

DROP TABLE IF EXISTS `listing_inclusions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `listing_inclusions` (
  `Inclusion_ID` int NOT NULL AUTO_INCREMENT,
  `Listing_ID` int NOT NULL,
  `Inclusion_Name` varchar(255) NOT NULL,
  `Quantity` int NOT NULL,
  PRIMARY KEY (`Inclusion_ID`),
  KEY `fk_inclusion_listing` (`Listing_ID`),
  CONSTRAINT `fk_inclusion_listing` FOREIGN KEY (`Listing_ID`) REFERENCES `listings` (`Listing_ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `listings`
--

DROP TABLE IF EXISTS `listings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `listings` (
  `Listing_ID` int NOT NULL AUTO_INCREMENT,
  `Merchant_Name` varchar(100) NOT NULL,
  `Listing_Name` varchar(255) NOT NULL,
  `Listing_Price` decimal(15,2) DEFAULT '0.00',
  `Listing_Icon_Name` varchar(100) DEFAULT NULL,
  `Created_At` datetime DEFAULT CURRENT_TIMESTAMP,
  `Updated_At` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Listing_ID`),
  UNIQUE KEY `Listing_Name` (`Listing_Name`),
  KEY `fk_listing_merchant` (`Merchant_Name`),
  CONSTRAINT `fk_listing_merchant` FOREIGN KEY (`Merchant_Name`) REFERENCES `merchants` (`Merchant_Name`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `merchants`
--

DROP TABLE IF EXISTS `merchants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchants` (
  `Merchant_ID` int NOT NULL AUTO_INCREMENT,
  `Merchant_Name` varchar(100) NOT NULL,
  `Created_At` datetime DEFAULT CURRENT_TIMESTAMP,
  `Updated_At` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Merchant_ID`),
  UNIQUE KEY `Merchant_Name` (`Merchant_Name`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactions` (
  `Transaction_ID` int NOT NULL AUTO_INCREMENT,
  `Reference_ID` varchar(13) NOT NULL,
  `Sender_Type` enum('USER','MERCHANT') NOT NULL,
  `Recipient_Type` enum('USER','MERCHANT') NOT NULL,
  `Sender_Number` varchar(15) DEFAULT NULL,
  `Recipient_Number` varchar(15) DEFAULT NULL,
  `Merchant_Name` varchar(100) DEFAULT NULL,
  `Amount` decimal(15,2) NOT NULL,
  `Fee` decimal(15,2) DEFAULT '0.00',
  `Note` varchar(50) DEFAULT NULL,
  `Created_At` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`Transaction_ID`),
  UNIQUE KEY `Reference_ID` (`Reference_ID`),
  KEY `fk_sender` (`Sender_Number`),
  KEY `fk_recipient` (`Recipient_Number`),
  KEY `fk_merchant` (`Merchant_Name`),
  CONSTRAINT `fk_merchant` FOREIGN KEY (`Merchant_Name`) REFERENCES `merchants` (`Merchant_Name`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_recipient` FOREIGN KEY (`Recipient_Number`) REFERENCES `accounts` (`Account_Number`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_sender` FOREIGN KEY (`Sender_Number`) REFERENCES `accounts` (`Account_Number`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `wallet`
--

DROP TABLE IF EXISTS `wallet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wallet` (
  `Wallet_ID` int NOT NULL AUTO_INCREMENT,
  `Account_Number` varchar(15) NOT NULL,
  `Wallet_Balance` decimal(15,2) DEFAULT '0.00',
  `Created_At` datetime DEFAULT CURRENT_TIMESTAMP,
  `Updated_At` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Wallet_ID`),
  KEY `fk_wallet_account` (`Account_Number`),
  CONSTRAINT `fk_wallet_account` FOREIGN KEY (`Account_Number`) REFERENCES `accounts` (`Account_Number`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-11 15:21:25
