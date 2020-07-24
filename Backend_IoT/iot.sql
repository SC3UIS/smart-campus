-- MySQL dump 10.13  Distrib 8.0.13, for Win64 (x86_64)
--
-- Host: localhost    Database: iot
-- ------------------------------------------------------
-- Server version	8.0.13

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `app_user`
--

DROP TABLE IF EXISTS `app_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `app_user` (
  `username` varchar(45) NOT NULL,
  `password` varchar(100) NOT NULL,
  `email` varchar(180) NOT NULL,
  `id_user` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(90) NOT NULL,
  `is_admin` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `username_UNIQUE` (`username`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  KEY `admin_INDEX` (`is_admin`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `app_user`
--

LOCK TABLES `app_user` WRITE;
/*!40000 ALTER TABLE `app_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `app_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `application`
--

DROP TABLE IF EXISTS `application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `application` (
  `id_application` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(180) NOT NULL,
  `description` varchar(300) NOT NULL,
  `id_user` bigint(20) NOT NULL,
  PRIMARY KEY (`id_application`),
  KEY `fk_application_app_user_idx` (`id_user`),
  CONSTRAINT `fk_application_app_user` FOREIGN KEY (`id_user`) REFERENCES `app_user` (`id_user`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `application`
--

LOCK TABLES `application` WRITE;
/*!40000 ALTER TABLE `application` DISABLE KEYS */;
/*!40000 ALTER TABLE `application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `application_per_gateway`
--

DROP TABLE IF EXISTS `application_per_gateway`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `application_per_gateway` (
  `id_application` bigint(20) NOT NULL,
  `id_gateway` bigint(20) NOT NULL,
  PRIMARY KEY (`id_application`,`id_gateway`),
  KEY `fk_Aplicacion_has_Gateway_Aplicacion1_idx` (`id_application`),
  KEY `fk_gateway` (`id_gateway`),
  CONSTRAINT `fk_application` FOREIGN KEY (`id_application`) REFERENCES `application` (`id_application`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_gateway` FOREIGN KEY (`id_gateway`) REFERENCES `gateway` (`id_gateway`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `application_per_gateway`
--

LOCK TABLES `application_per_gateway` WRITE;
/*!40000 ALTER TABLE `application_per_gateway` DISABLE KEYS */;
/*!40000 ALTER TABLE `application_per_gateway` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `device`
--

DROP TABLE IF EXISTS `device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `device` (
  `id_device` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `id_gateway` bigint(20) NOT NULL,
  `device_type` varchar(10) NOT NULL,
  `description` varchar(300) NOT NULL,
  PRIMARY KEY (`id_device`),
  KEY `fk_device_gateway` (`id_gateway`),
  KEY `type_INDEX` (`device_type`),
  CONSTRAINT `fk_device_gateway` FOREIGN KEY (`id_gateway`) REFERENCES `gateway` (`id_gateway`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device`
--

LOCK TABLES `device` WRITE;
/*!40000 ALTER TABLE `device` DISABLE KEYS */;
/*!40000 ALTER TABLE `device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `device_property`
--

DROP TABLE IF EXISTS `device_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `device_property` (
  `property_type` varchar(15) NOT NULL,
  `id_device` bigint(20) NOT NULL,
  `name` varchar(45) NOT NULL,
  `value` varchar(300) NOT NULL,
  PRIMARY KEY (`name`,`id_device`,`property_type`),
  KEY `fk_device_property_device1_idx` (`id_device`),
  CONSTRAINT `fk_device_property_device` FOREIGN KEY (`id_device`) REFERENCES `device` (`id_device`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device_property`
--

LOCK TABLES `device_property` WRITE;
/*!40000 ALTER TABLE `device_property` DISABLE KEYS */;
/*!40000 ALTER TABLE `device_property` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gateway`
--

DROP TABLE IF EXISTS `gateway`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `gateway` (
  `id_gateway` bigint(20) NOT NULL AUTO_INCREMENT,
  `ip_gateway` varchar(300) NOT NULL,
  `description` varchar(300) NOT NULL,
  `name` varchar(45) NOT NULL,
  `is_alive` tinyint(1) NOT NULL DEFAULT '0',
  `id_user` bigint(20) NOT NULL,
  PRIMARY KEY (`id_gateway`),
  UNIQUE KEY `ip_gateway_UNIQUE` (`ip_gateway`),
  KEY `fk_gateway_app_user1_idx` (`id_user`),
  CONSTRAINT `fk_gateway_app_user1` FOREIGN KEY (`id_user`) REFERENCES `app_user` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gateway`
--

LOCK TABLES `gateway` WRITE;
/*!40000 ALTER TABLE `gateway` DISABLE KEYS */;
/*!40000 ALTER TABLE `gateway` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gateway_property`
--

DROP TABLE IF EXISTS `gateway_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `gateway_property` (
  `value` varchar(300) NOT NULL,
  `id_gateway` bigint(20) NOT NULL,
  `property_type` varchar(15) NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`name`,`property_type`,`id_gateway`),
  KEY `fk_Propiedad_reportada_Gateway1_idx` (`id_gateway`),
  KEY `type_INDEX` (`property_type`),
  CONSTRAINT `fk_gateway_property_gateway` FOREIGN KEY (`id_gateway`) REFERENCES `gateway` (`id_gateway`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gateway_property`
--

LOCK TABLES `gateway_property` WRITE;
/*!40000 ALTER TABLE `gateway_property` DISABLE KEYS */;
/*!40000 ALTER TABLE `gateway_property` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `notification` (
  `id_notification` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_user` bigint(20) NOT NULL,
  `id_gateway` bigint(20) NOT NULL,
  `id_process` bigint(20) DEFAULT NULL,
  `sent_date` timestamp NOT NULL,
  `message` varchar(1000) NOT NULL,
  `alive` tinyint(1) NOT NULL,
  `was_read` tinyint(1) NOT NULL DEFAULT '0',
  `hidden` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id_notification`),
  KEY `fk_notification_user_idx` (`id_user`),
  KEY `fk_notification_process_idx` (`id_process`),
  KEY `fk_notification_gateway_idx` (`id_gateway`),
  KEY `alive_index` (`alive`) /*!80000 INVISIBLE */,
  KEY `hidden_index` (`hidden`),
  CONSTRAINT `fk_notification_gateway` FOREIGN KEY (`id_gateway`) REFERENCES `gateway` (`id_gateway`) ON DELETE CASCADE,
  CONSTRAINT `fk_notification_process` FOREIGN KEY (`id_process`) REFERENCES `process` (`id_process`) ON DELETE CASCADE,
  CONSTRAINT `fk_notification_user` FOREIGN KEY (`id_user`) REFERENCES `app_user` (`id_user`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `process`
--

DROP TABLE IF EXISTS `process`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `process` (
  `id_process` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(300) NOT NULL,
  `name` varchar(45) NOT NULL,
  `id_gateway` bigint(20) NOT NULL,
  `is_alive` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_process`),
  KEY `fk_process_gateway1_idx` (`id_gateway`),
  CONSTRAINT `fk_process_gateway` FOREIGN KEY (`id_gateway`) REFERENCES `gateway` (`id_gateway`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `process`
--

LOCK TABLES `process` WRITE;
/*!40000 ALTER TABLE `process` DISABLE KEYS */;
/*!40000 ALTER TABLE `process` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `process_property`
--

DROP TABLE IF EXISTS `process_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `process_property` (
  `value` varchar(300) NOT NULL,
  `name_property_type` varchar(45) NOT NULL,
  `id_process` bigint(20) NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`name_property_type`,`id_process`,`name`),
  KEY `fk_process_property_property_type1_idx` (`name_property_type`),
  KEY `fk_process_property_process1_idx` (`id_process`),
  CONSTRAINT `fk_process_property_process` FOREIGN KEY (`id_process`) REFERENCES `process` (`id_process`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `process_property`
--

LOCK TABLES `process_property` WRITE;
/*!40000 ALTER TABLE `process_property` DISABLE KEYS */;
/*!40000 ALTER TABLE `process_property` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-04-07 18:39:59
