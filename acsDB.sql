-- MySQL dump 10.13  Distrib 5.7.33, for Linux (x86_64)
--
-- Host: localhost    Database: acsDB
-- ------------------------------------------------------
-- Server version	5.7.33-0ubuntu0.18.04.1

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
-- Table structure for table `cpe_response_log`
--

DROP TABLE IF EXISTS `cpe_response_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cpe_response_log` (
  `id` bigint(20) NOT NULL,
  `method` varchar(255) DEFAULT NULL,
  `payload` text,
  `serial_num` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cpe_response_log`
--

LOCK TABLES `cpe_response_log` WRITE;
/*!40000 ALTER TABLE `cpe_response_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `cpe_response_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `device`
--

DROP TABLE IF EXISTS `device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `device` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `activated` bit(1) DEFAULT NULL,
  `date_created` varchar(255) DEFAULT NULL,
  `date_modified` varchar(255) DEFAULT NULL,
  `device_name` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `mac_address` varchar(255) DEFAULT NULL,
  `model` varchar(255) DEFAULT NULL,
  `parent` varchar(255) DEFAULT NULL,
  `serial_number` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT 'offline',
  `date_offline` varchar(255) DEFAULT NULL,
  `device_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device`
--

LOCK TABLES `device` WRITE;
/*!40000 ALTER TABLE `device` DISABLE KEYS */;
/*!40000 ALTER TABLE `device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `devices`
--

DROP TABLE IF EXISTS `devices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `devices` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `serial_num` varchar(255) DEFAULT NULL,
  `model` varchar(255) DEFAULT NULL,
  `manufacturer` varchar(255) DEFAULT NULL,
  `oui` varchar(255) DEFAULT NULL,
  `hardware_ver` varchar(255) DEFAULT NULL,
  `root_fs_ver` varchar(255) DEFAULT NULL,
  `firmware_ver` varchar(255) DEFAULT NULL,
  `ap_mode` varchar(255) DEFAULT NULL,
  `mac_address` varchar(255) DEFAULT NULL,
  `os_type` varchar(255) DEFAULT NULL,
  `host_name` varchar(255) DEFAULT NULL,
  `max_users` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `last_reboot` varchar(255) DEFAULT NULL,
  `last_boot` varchar(255) DEFAULT NULL,
  `root_data_model` varchar(255) DEFAULT NULL,
  `web_auth` varchar(255) DEFAULT NULL,
  `group_path` varchar(255) DEFAULT NULL,
  `udp_con_req_url` varchar(255) DEFAULT NULL,
  `con_req_url` varchar(255) DEFAULT NULL,
  `cpu_usage` varchar(255) DEFAULT NULL,
  `cwmp_cycle_end` bit(1) DEFAULT NULL,
  `device_alias` varchar(255) DEFAULT NULL,
  `management_ip` varchar(255) DEFAULT NULL,
  `memory_usage` varchar(255) DEFAULT NULL,
  `public_ip` varchar(255) DEFAULT NULL,
  `software_ver` varchar(255) DEFAULT NULL,
  `ssids` varchar(255) DEFAULT NULL,
  `second_wan_mac` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `devices`
--

LOCK TABLES `devices` WRITE;
/*!40000 ALTER TABLE `devices` DISABLE KEYS */;
/*!40000 ALTER TABLE `devices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_command`
--

DROP TABLE IF EXISTS `group_command`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_command` (
  `id` bigint(20) NOT NULL,
  `command` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `model` varchar(255) DEFAULT NULL,
  `parent` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_command`
--

LOCK TABLES `group_command` WRITE;
/*!40000 ALTER TABLE `group_command` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_command` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_ssid`
--

DROP TABLE IF EXISTS `group_ssid`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_ssid` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `auth` bit(1) DEFAULT NULL,
  `downlink` int(11) DEFAULT NULL,
  `encryption_mode` varchar(255) DEFAULT NULL,
  `forward_mode` varchar(255) DEFAULT NULL,
  `gateway_id` varchar(255) DEFAULT NULL,
  `limitless` bit(1) DEFAULT NULL,
  `parent` varchar(255) DEFAULT NULL,
  `portal_ip` varchar(255) DEFAULT NULL,
  `portal_url` varchar(255) DEFAULT NULL,
  `seamless` bit(1) DEFAULT NULL,
  `ssid` varchar(255) DEFAULT NULL,
  `uplink` int(11) DEFAULT NULL,
  `vlan_id` int(11) DEFAULT NULL,
  `wlan_id` int(11) DEFAULT NULL,
  `passphrase` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_ssid`
--

LOCK TABLES `group_ssid` WRITE;
/*!40000 ALTER TABLE `group_ssid` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_ssid` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `groups`
--

DROP TABLE IF EXISTS `groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groups` (
  `id` bigint(20) NOT NULL,
  `child` varchar(255) DEFAULT NULL,
  `date_created` varchar(255) DEFAULT NULL,
  `date_modified` varchar(255) DEFAULT NULL,
  `parent` varchar(255) DEFAULT NULL,
  `site` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `group_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `groups`
--

LOCK TABLES `groups` WRITE;
/*!40000 ALTER TABLE `groups` DISABLE KEYS */;
/*!40000 ALTER TABLE `groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hibernate_sequence`
--

DROP TABLE IF EXISTS `hibernate_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hibernate_sequence`
--

LOCK TABLES `hibernate_sequence` WRITE;
/*!40000 ALTER TABLE `hibernate_sequence` DISABLE KEYS */;
/*!40000 ALTER TABLE `hibernate_sequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `httprequestlog`
--

DROP TABLE IF EXISTS `httprequestlog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `httprequestlog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cookie` varchar(255) DEFAULT NULL,
  `last_request` datetime(6) DEFAULT NULL,
  `serial_num` varchar(255) DEFAULT NULL,
  `device_status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `httprequestlog`
--

LOCK TABLES `httprequestlog` WRITE;
/*!40000 ALTER TABLE `httprequestlog` DISABLE KEYS */;
/*!40000 ALTER TABLE `httprequestlog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `taskhandler`
--

DROP TABLE IF EXISTS `taskhandler`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `taskhandler` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `method` varchar(255) DEFAULT NULL,
  `optional` varchar(255) DEFAULT NULL,
  `parameters` text,
  `serial_num` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2837 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `taskhandler`
--

LOCK TABLES `taskhandler` WRITE;
/*!40000 ALTER TABLE `taskhandler` DISABLE KEYS */;
/*!40000 ALTER TABLE `taskhandler` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `webcli_response_log`
--

DROP TABLE IF EXISTS `webcli_response_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `webcli_response_log` (
  `id` bigint(20) NOT NULL,
  `command_output` blob,
  `command_used` blob,
  `device_sn` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `webcli_response_log`
--

LOCK TABLES `webcli_response_log` WRITE;
/*!40000 ALTER TABLE `webcli_response_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `webcli_response_log` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-03-17 10:46:21
