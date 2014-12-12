-- MySQL dump 10.13  Distrib 5.5.40, for debian-linux-gnu (x86_64)
--
-- Host: 127.0.0.1    Database: appdb
-- ------------------------------------------------------
-- Server version	5.5.37-0ubuntu0.13.10.1

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
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `UK_ofx66keruapi6vyqpv6f2or37` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (2,'ADMIN'),(1,'USER');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sleep`
--

DROP TABLE IF EXISTS `sleep`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sleep` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `minutes_awake_in_bed` int(11) NOT NULL,
  `minutes_awake_not_in_bed` int(11) NOT NULL,
  `minutes_napping` int(11) NOT NULL,
  `minutes_total` int(11) NOT NULL,
  `time_out_of_bed` datetime DEFAULT NULL,
  `user` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_rfypjk2yj79499hdnoekw3v8w` (`user`),
  CONSTRAINT `FK_rfypjk2yj79499hdnoekw3v8w` FOREIGN KEY (`user`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sleep`
--

LOCK TABLES `sleep` WRITE;
/*!40000 ALTER TABLE `sleep` DISABLE KEYS */;
INSERT INTO `sleep` VALUES (1,25,20,0,480,'2014-07-02 05:30:00',10),(2,25,20,0,480,'2014-07-03 05:30:00',10);
/*!40000 ALTER TABLE `sleep` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role` (
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FK_it77eq964jhfqtu54081ebtio` (`role_id`),
  KEY `FK_apcc8lxk2xnug8377fatvbn04` (`user_id`),
  CONSTRAINT `FK_apcc8lxk2xnug8377fatvbn04` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK_it77eq964jhfqtu54081ebtio` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (10,1),(11,1),(15,1),(16,1),(17,1),(18,1),(19,1),(20,1),(21,1),(22,1),(23,1),(24,1),(25,1),(26,1),(27,1),(28,1),(29,1),(30,1),(31,1),(32,1),(33,1),(34,1),(35,1),(36,1),(37,1),(38,1),(39,1),(40,1),(41,1),(42,1),(43,1),(44,1),(45,1),(46,1),(47,1),(48,1),(49,1),(50,1),(51,1),(52,1),(53,1),(54,1),(55,1),(56,1),(57,1),(58,1),(59,1),(60,1),(61,1),(62,1),(63,1),(64,1),(65,1),(66,1),(67,1),(68,1),(69,1),(70,1),(71,1),(72,1),(73,1),(74,1),(75,1),(76,1),(77,1),(78,1),(79,1),(80,1),(81,1),(82,1),(83,1),(84,1),(85,1),(86,1),(87,1),(88,1),(89,1),(90,1),(91,1),(92,1),(93,1),(94,1),(95,1),(96,1),(97,1),(98,1),(99,1),(100,1),(101,1),(102,1),(103,1),(104,1),(105,1),(106,1),(107,1),(108,1),(109,1),(110,1),(111,1),(112,1),(113,1),(114,1),(11,2);
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `display_name` varchar(255) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  `password` varchar(255) NOT NULL,
  `registration` datetime DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (10,'user',1,'$2a$10$4xVhHB2ZcBsxe81RByGLV.WAjOQ6isMVEPm9JhqdtWzeQuKgQgJCy','2014-04-17 19:38:24','user@app.com'),(11,'admin',1,'$2a$10$ZgEuGnoIOoR.TUgIuCz2iuYZz4Umb4Xs6JwPYN7ITbatu/I.W0442','2014-04-17 19:38:39','admin@app.com'),(15,'user0',1,'$2a$10$/cJsEjwcFGsCJy9.R/9rwuPQxNBsKPH6T9MMSVvJqady4o9HUK8iq','2014-12-11 16:11:42','user0@app.com'),(16,'user1',1,'$2a$10$J8K1PJbbGsMxOweHAiUwzucCxu4VcOADYgVQjAJx.6kKM7Uv5oTWK','2014-12-11 16:11:43','user1@app.com'),(17,'user2',1,'$2a$10$dbzrY2b5UxMFqu3lPboTDOAJ/4ZWyEK.t0TDyju7oYKai2oFr9CVe','2014-12-11 16:11:43','user2@app.com'),(18,'user3',1,'$2a$10$erkWJPR4K50OMZr.zkcLru/m7P7QQhzb214dWCv0/vWJ2Y4W1xXtS','2014-12-11 16:11:43','user3@app.com'),(19,'user4',1,'$2a$10$vdFKrHcG9/Hb9dql/dhrsuHrpWl3udTmC/ynLQ8KL4ZVHRJ/vWIwC','2014-12-11 16:11:43','user4@app.com'),(20,'user5',1,'$2a$10$hWRhYuIqrIEgGGcgb7rulOvlFwwZZ.h14Sr6Nj44KFzOSeaI8BQ5O','2014-12-11 16:11:43','user5@app.com'),(21,'user6',1,'$2a$10$mMWRvuX32dINM/3qGp1BG.VANo9F.h.kfZmRHeg6QJNPejcm29Kum','2014-12-11 16:11:43','user6@app.com'),(22,'user7',1,'$2a$10$2OiaBRsZL8U1sfgFQsxBfO4RGM.x.GaHAVe.nOY3Omfwdd6.Fy2Na','2014-12-11 16:11:43','user7@app.com'),(23,'user8',1,'$2a$10$kC.UpabYUDSXOdp9HtNY6ey2M0AFvAwWdLu/WP7D.aBjEEAoD5/bO','2014-12-11 16:11:43','user8@app.com'),(24,'user9',1,'$2a$10$RwrN3NiKmCNkKffHo3dm3e2A6GtOZzZ.LI.mRkbu0h0kNXXx2SfHq','2014-12-11 16:11:43','user9@app.com'),(25,'user10',1,'$2a$10$dC0pavQEugUroVrGeEyAn.1nF3aq6GhWEJV791wz9KmimQgCN8GEe','2014-12-11 16:11:43','user10@app.com'),(26,'user11',1,'$2a$10$yj6r5k2xvKTxwTV70Xp6s.kaXZk42IbpPf3/HJNgZ3yKToEtGbmGa','2014-12-11 16:11:43','user11@app.com'),(27,'user12',1,'$2a$10$xq5qDk/9K0frlQ1i4qxgFO0md2Jd7ezyDBGJDmDOt7yaxO.kgIvJG','2014-12-11 16:11:44','user12@app.com'),(28,'user13',1,'$2a$10$cmPqzy.ngfJjIsRzmCIjvOOGiP09WxjTo1kjsvJyXZbuNxtLDjvmC','2014-12-11 16:11:44','user13@app.com'),(29,'user14',1,'$2a$10$YiJw/nC/qfbxAm1VpNmUM.c1YPcir94ezSyS2qugDUefVoPCAI0p6','2014-12-11 16:11:44','user14@app.com'),(30,'user15',1,'$2a$10$M14/asxrK1lnNjdA4QT4XedQrqzWucOVlXil33B7YHj01Sc.hqlLq','2014-12-11 16:11:44','user15@app.com'),(31,'user16',1,'$2a$10$HoxMEob8zj2hzUXYik.yAu7A9rHYIdfUqI26RYl7wchgUHTofE/SW','2014-12-11 16:11:44','user16@app.com'),(32,'user17',1,'$2a$10$dzg/.WCoFAaDQMDhaAQnPuwPk.ifbfZV/z8a51/7iWKALIb9FE/Nq','2014-12-11 16:11:44','user17@app.com'),(33,'user18',1,'$2a$10$aspQeFkxTBRixqWfC6pjhO0d9uae1qCKtZfPogHRWLKqnnQpxaWDq','2014-12-11 16:11:44','user18@app.com'),(34,'user19',1,'$2a$10$wm2cNA4hJbtSIt7iTwf6sOjHkLyCkyCnomTvf291i54l1qfFPA/Ka','2014-12-11 16:11:44','user19@app.com'),(35,'user20',1,'$2a$10$OqgVBBxlxSJEuSq/b7n7D.MHzKfJS4oKJIIeCoD23QUiJOnMcoh2y','2014-12-11 16:11:44','user20@app.com'),(36,'user21',1,'$2a$10$9F9xM4E5GaqZk4Wj4b7/Y.Ab3Rk/Wv34qNONEjDcApjR3y6/5QlO.','2014-12-11 16:11:44','user21@app.com'),(37,'user22',1,'$2a$10$BRMJciBd8klGVtg5isITOOo0Cw1EBk/1PRuyJCtO2REMGwZJ679ra','2014-12-11 16:11:44','user22@app.com'),(38,'user23',1,'$2a$10$ogfv/YcjqS1pccX.pGrzL.oUcdiYTP3aOqp6tQ3yrqdrBaZAkMbfK','2014-12-11 16:11:45','user23@app.com'),(39,'user24',1,'$2a$10$5APLE0e8aUP6d4Nmnt6xte831LUsm2DJIXzZBec2Qd3YMGDdBD/kW','2014-12-11 16:11:45','user24@app.com'),(40,'user25',1,'$2a$10$bPwIylwdg8CHIWgiBr6O3OKkI2U8qtze3ButTL2GIC5G98ZNrR1lC','2014-12-11 16:11:45','user25@app.com'),(41,'user26',1,'$2a$10$6111KfBKahTFZepK19eEbOthoFRzYy8wPYcCkbz0EQh.rDI75xmJO','2014-12-11 16:11:45','user26@app.com'),(42,'user27',1,'$2a$10$Yrua/zsDmc5WC4mtG0hGFuDpGE2lADDGwM.ILqm21jIrgbmJd5P2.','2014-12-11 16:11:45','user27@app.com'),(43,'user28',1,'$2a$10$mKn8VCg03B4MS58MojizUOkkSXIyBkTrYqnX41wZHJ0XlBSE/ZI1a','2014-12-11 16:11:45','user28@app.com'),(44,'user29',1,'$2a$10$UmANFSXegFrbS3zo6zHdV.zubJVjO/tZwotc6gUCRUwgxAKz9T23O','2014-12-11 16:11:45','user29@app.com'),(45,'user30',1,'$2a$10$cIqeS8xjVWMA0w739xOlVOFD0.RgXGfwH7siwqMwVAkcp2uyWZv.a','2014-12-11 16:11:45','user30@app.com'),(46,'user31',1,'$2a$10$Z8AmH3.icddqBYw/Xw9W9.TjTchXpzJdUBp/H.sM/PkPJ5.sDSHI6','2014-12-11 16:11:45','user31@app.com'),(47,'user32',1,'$2a$10$DMLmH3.xTgIkIdWNSiAksuvIGu41zH0Mb1OMQwKdJsRC1HPKlBxfO','2014-12-11 16:11:45','user32@app.com'),(48,'user33',1,'$2a$10$XCrjfAhHCnOCmEEdBH8J/.41N8qEVgmDJ5FkDwoFwvYm.Ri5hf0/q','2014-12-11 16:11:45','user33@app.com'),(49,'user34',1,'$2a$10$r5805WboGTrvyUcYh3JdSOWgPW/evSOB7Ga41e7yJQi6H0hmMKjYm','2014-12-11 16:11:46','user34@app.com'),(50,'user35',1,'$2a$10$9ysMxY6zn1dT5jsHuECAbOGyix/MltRzCNc/mSJzc4keOKgW6z8sq','2014-12-11 16:11:46','user35@app.com'),(51,'user36',1,'$2a$10$rk/rU6hYIFWaGzBu1JVWNOiRfhvVnqLKjuuFZD7MD4OBPr3zSeY2i','2014-12-11 16:11:46','user36@app.com'),(52,'user37',1,'$2a$10$ywB0nFlLd2uiUKg67Y3ZH.wQd6S/CIz1B5zSOlpWptVOoR/TKbH2e','2014-12-11 16:11:46','user37@app.com'),(53,'user38',1,'$2a$10$TmwqvHW9Zywxgdjn/sVcoOuHMekC0861oWRKtq2j4whAQxg42VFO.','2014-12-11 16:11:46','user38@app.com'),(54,'user39',1,'$2a$10$J/ledEq86HHGSurfV7nTr.yDX/5.Fi/BNucGycEOMFzVBNTRzN7s6','2014-12-11 16:11:46','user39@app.com'),(55,'user40',1,'$2a$10$iECT0TIkbeJ/.rg8ZeK/0up7WK7uGW7jEDGuDQhAY6.t0WqVkKfUC','2014-12-11 16:11:46','user40@app.com'),(56,'user41',1,'$2a$10$4lsTz/G5j84C6Y2VNcjcr.JG2ISJXR/qDEWT9NLFXCuCCNIqWARU6','2014-12-11 16:11:46','user41@app.com'),(57,'user42',1,'$2a$10$TjkLjlcgnMjoAxxl2RV/I.uk3mdj7ftFUbTi4BrHrdwYqgggW5vyG','2014-12-11 16:11:46','user42@app.com'),(58,'user43',1,'$2a$10$4txo74zmmtAplwntsgkRBeC4UL3eyv4lpa4BjgD8YJ8x5fY1KBwRS','2014-12-11 16:11:46','user43@app.com'),(59,'user44',1,'$2a$10$SlpdBc2pLqFc3O1Q2Rb1hu8gXyPwNNAkp45tihm2nV766cGT6F94G','2014-12-11 16:11:46','user44@app.com'),(60,'user45',1,'$2a$10$/hSgACqICN.ADJYlKOjNwueJjjnBtH52aFQO5XSjw/.hbf6usjjbW','2014-12-11 16:11:47','user45@app.com'),(61,'user46',1,'$2a$10$J6Ap72PH9iEOkvBFKU4eVuaMLsjq7A4zZg/yIZJis85CXkk07ljli','2014-12-11 16:11:47','user46@app.com'),(62,'user47',1,'$2a$10$JMtF6LkCxS2CRHJ8./5JOuE4oDyjuDw2qJCw2qZCZF281QbZyWqWm','2014-12-11 16:11:47','user47@app.com'),(63,'user48',1,'$2a$10$M7LVQirvkJP9N.sIjCWZzuNxdPS20EFg7p2Liht3U2k/XP6VyAFU2','2014-12-11 16:11:47','user48@app.com'),(64,'user49',1,'$2a$10$1qPAUSVaECz6u.zvrQhSN.mPSbKZxInXx.QN3a10ns0LFI7684GAq','2014-12-11 16:11:47','user49@app.com'),(65,'user50',1,'$2a$10$Di2IxieL1ZZDj603YCyn5u8ZF/ZK.ncAwRkFc0G/dog7P.eLif.wu','2014-12-11 16:11:47','user50@app.com'),(66,'user51',1,'$2a$10$yUBaeq.ai8QIUxDGbR3xLuCt6unZ0vSWBFtkPnfIUpZVDE5ntHb1i','2014-12-11 16:11:47','user51@app.com'),(67,'user52',1,'$2a$10$WEHjtedsJIbO1oEo2/SLtOdoqMJHzJUiQcfXTlG7iYOZXgzns4twa','2014-12-11 16:11:47','user52@app.com'),(68,'user53',1,'$2a$10$5/tk/5nCk5bYhlNKX31Bb.x8IShnDZAfjWGtkiqJRfVEYk4sRRDAa','2014-12-11 16:11:47','user53@app.com'),(69,'user54',1,'$2a$10$qkGWj.Rjq9xd8QtKKhTYNua8SXCT.BPNQi2Uc6jA1m/clYM2VVVti','2014-12-11 16:11:47','user54@app.com'),(70,'user55',1,'$2a$10$2ZRoGPkrJdLcXRBq6uYzpOe7E4QBxxs9NbeflBZ5VNW2TrrqoP4DC','2014-12-11 16:11:47','user55@app.com'),(71,'user56',1,'$2a$10$0Yxt76RFfz.c.cOqHjXwiOfePc2mj5xgatqwTlHrpemKwk0IspAJO','2014-12-11 16:11:47','user56@app.com'),(72,'user57',1,'$2a$10$uZ40V6/iHG4czaT78zflXu7X8PREDNQHTCqVZMfBju9Vnzj6xdFM2','2014-12-11 16:11:48','user57@app.com'),(73,'user58',1,'$2a$10$6tIb9zd2nEWXmLqFev/HJOji82Bjrtp643VplUMfBXLFTVo6jIhbS','2014-12-11 16:11:48','user58@app.com'),(74,'user59',1,'$2a$10$Bh0s6M.stlvVnozs8nca9.qOb6.gUyrDX2i7JvjwAdN0sTsR8E22e','2014-12-11 16:11:48','user59@app.com'),(75,'user60',1,'$2a$10$3QsiK1ImUfr5W0A3fwluYeoZQHv24n0kHqh6R/F2f3wcRjZr/n4Ty','2014-12-11 16:11:48','user60@app.com'),(76,'user61',1,'$2a$10$orb702vmwUqUUccDVIx68..gNqphv58ItmKINJe1QDNBLVWVMicvO','2014-12-11 16:11:48','user61@app.com'),(77,'user62',1,'$2a$10$rHj4lTgwD/cmTwxkPVLRf..lD7Pugcwp9aPDNp8U3IRHeVZZASfoq','2014-12-11 16:11:48','user62@app.com'),(78,'user63',1,'$2a$10$gQF/vwZFW.reAMP7kwxRyeOQCs8HAjbG4aqi0fb8xb2QYRR3AeVda','2014-12-11 16:11:48','user63@app.com'),(79,'user64',1,'$2a$10$F6QlOsZNRdwQEXAo44PZhOrxDpagmSQ9x0YrPYhStM.vtsREyuyGu','2014-12-11 16:11:48','user64@app.com'),(80,'user65',1,'$2a$10$6uKyfJ4OYs11XFSoMcw1KOK2sgVMtkW3VMEYgRpXzHheakUlqiR2O','2014-12-11 16:11:48','user65@app.com'),(81,'user66',1,'$2a$10$b4APVRf1ktxhhLBJO4DM5uK.zhNY.nQiloKp6EF41AoOqqH6I9kE6','2014-12-11 16:11:48','user66@app.com'),(82,'user67',1,'$2a$10$.hXv7hLvqWtC0NDU1USOuOf3OIfA145RqyMTVoe8TIpz.CSRp1Os6','2014-12-11 16:11:48','user67@app.com'),(83,'user68',1,'$2a$10$MeoSuhzwL.uxhf5e2uyS7.zxtttWAgzMHzsmB2bFRgUPHHHKy.aK6','2014-12-11 16:11:49','user68@app.com'),(84,'user69',1,'$2a$10$MGfRQKXLTk09qX39hesR8uYKNKLq4YQrsS8JmaOGeA0Fsj3X7Acza','2014-12-11 16:11:49','user69@app.com'),(85,'user70',1,'$2a$10$e2faUWqJbOmcwQ2DPDvGBu1R.SW6GABtfZXTUYGBM9s9GETc1EqOi','2014-12-11 16:11:49','user70@app.com'),(86,'user71',1,'$2a$10$JREymJUWLSi8gjdxJOMDX.rRLUmEKusR2c8B7vuuZjB3lZM9Tyg.K','2014-12-11 16:11:49','user71@app.com'),(87,'user72',1,'$2a$10$GtPywGeLKOWBX2Ie1.Hv9OVjmmYmRq0qz6LzEDhXICb8xraDD1cEm','2014-12-11 16:11:49','user72@app.com'),(88,'user73',1,'$2a$10$jl2ePVGy31uZwtp70DfgyuQCcfxz0DaDlFAjPWSiySpymf4IPw4lG','2014-12-11 16:11:49','user73@app.com'),(89,'user74',1,'$2a$10$SmeGgJulkVyET8zQNclh/OyHPmAfR26NA6.Ezfnc0dOHIfGvcvpZq','2014-12-11 16:11:49','user74@app.com'),(90,'user75',1,'$2a$10$XgG7gsh8fc04wtAWuTB8quTjWWuLwQ.2H2.m5XF.aJnmn3xAF1GRe','2014-12-11 16:11:49','user75@app.com'),(91,'user76',1,'$2a$10$WAOIyGVr3Ir.aknxNTHW9e/yFS4ZWKh6iuIZFOnOPQ8KCUIFSrA.2','2014-12-11 16:11:49','user76@app.com'),(92,'user77',1,'$2a$10$bvzWEcK6hNxBq5TnulckzeO6m8yswMHaTmIOeMvAhQGgTjMHf6g7.','2014-12-11 16:11:49','user77@app.com'),(93,'user78',1,'$2a$10$gVL5J5trzY5X6ARTvvn9C.LnDMPqTVmZJ7kSu8hHz.1r8rJKTYSp2','2014-12-11 16:11:49','user78@app.com'),(94,'user79',1,'$2a$10$6ZOKvdKyDPOcuCNb9.VKAuUunOm5DL5Voe7oU1Q4jSsdp0Fh6r9FW','2014-12-11 16:11:50','user79@app.com'),(95,'user80',1,'$2a$10$GAMtN..iclT5BGMFXRABdecYf5ix4FSeVjN8pdElXGwdWA7ixnkU.','2014-12-11 16:11:50','user80@app.com'),(96,'user81',1,'$2a$10$CC6iQMCshijdOJwweesA7.rPMenf35CaRDzAfQu3rdoVZZK4mfzP2','2014-12-11 16:11:50','user81@app.com'),(97,'user82',1,'$2a$10$EJk/MDIoFMTcjauoWPCPku86sO/mv72rV1Q6iyn5fhNEM295zP4o6','2014-12-11 16:11:50','user82@app.com'),(98,'user83',1,'$2a$10$VO6Gm5xQWkofTjDFzvbMYuKV22e89xRTg8kWqOPeJPm3zbSuK5pyu','2014-12-11 16:11:50','user83@app.com'),(99,'user84',1,'$2a$10$9Iz.gxM2q27txnPgWUaLTOY8AjA2F3Vjm.E41IcVZjWhXtmAun4vG','2014-12-11 16:11:50','user84@app.com'),(100,'user85',1,'$2a$10$lSsOhEYQ6s5cl4PKUFiFj.cuhn1efXefmALrp3QARvPWBqe8P4h0i','2014-12-11 16:11:50','user85@app.com'),(101,'user86',1,'$2a$10$RzXU8a2DUXy.YEomJS6eL.q7dZzGTkHk3nA/2TWULY4qnm2Y3i55q','2014-12-11 16:11:50','user86@app.com'),(102,'user87',1,'$2a$10$zOzXPhYGowaMLPJKkG2gR.ct2mbYAipBl/zwTAMRfCofLS2vCtuhK','2014-12-11 16:11:50','user87@app.com'),(103,'user88',1,'$2a$10$l0qhoz23MpJCjbiDqermEOWEBxSjQLYO1isHtKSo9rJhEiEpWldKe','2014-12-11 16:11:50','user88@app.com'),(104,'user89',1,'$2a$10$s/7a0AWmR.l2oxq3oJQJ8.LdVziYmyGfttKewV4uyxd8.vLpBhjZq','2014-12-11 16:11:50','user89@app.com'),(105,'user90',1,'$2a$10$bUcHlWdZtpMzY67O60gMruioAmvAAzjpw4jJeRC2339EnNgTZy70K','2014-12-11 16:11:51','user90@app.com'),(106,'user91',1,'$2a$10$0gX0IHdUe7D2DxI7dH/Aeudno05BUvnYklwwDYMmaga.P16FrvCZ2','2014-12-11 16:11:51','user91@app.com'),(107,'user92',1,'$2a$10$dQJMW7VNWXGTrb.q7rjYK.agadGzuE.jGiciTwjAPAOqh5UlskcPG','2014-12-11 16:11:51','user92@app.com'),(108,'user93',1,'$2a$10$CWvYskRlclABmVJBwlE4nuOyC5W8.0lI8rqgVMWsWvVzLIHUVn452','2014-12-11 16:11:51','user93@app.com'),(109,'user94',1,'$2a$10$UzmKTaaqMdqDWyvwsrntruDfMcA4a2yKrjL1Rt2z5bK3WV/nVlPCa','2014-12-11 16:11:51','user94@app.com'),(110,'user95',1,'$2a$10$oJcVqz3RZ9J2mnqsTARzreHrMc8ZuZmvcWjrhPYPTBlgb5qDYMdxe','2014-12-11 16:11:51','user95@app.com'),(111,'user96',1,'$2a$10$AdEt2fc85sG/D28i5nf.LuCNpOCVXPJGeS0zrlHpStdAytCOvtXcy','2014-12-11 16:11:51','user96@app.com'),(112,'user97',1,'$2a$10$HtOuhHXLztKHB/ouZx7EDe96ISIpI8ChAV9zm9kxnxhguopDC9HCu','2014-12-11 16:11:51','user97@app.com'),(113,'user98',1,'$2a$10$gurRYCBvvfdMkqSw5bLDyOfkVD56QPd477USjDQz3o7Y7T01TyQTK','2014-12-11 16:11:51','user98@app.com'),(114,'user99',1,'$2a$10$IDwwlEsiFZjL7lhscl7VROr3agn/r0us3oYZMTiwkBqFNHFa4fhhe','2014-12-11 16:11:51','user99@app.com');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-12-11 16:16:36
