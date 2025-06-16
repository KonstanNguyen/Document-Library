-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: document_library_db
-- ------------------------------------------------------
-- Server version	8.0.41

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
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `account_id` bigint NOT NULL AUTO_INCREMENT,
  `password` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `UKgex1lmaqpg0ir5g1f5eftyaa1` (`username`),
  UNIQUE KEY `UKh6dr47em6vg85yuwt4e2roca4` (`user_id`),
  CONSTRAINT `FK7m8ru44m93ukyb61dfxw0apf6` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (1,'$2a$10$jLiClv66RTC/gpthYfbUj.tqh15pnxDYPrJSM8xQS9ZhZ6FW7UzPK','Long',1),(2,'$2a$10$q1IhqCwMyqHTTFqhsfTK5elxtf5q6W0m6sXtl2Yn2J66QuZebJSge','Khang',2),(3,'$2a$10$kCI7ncxry14JGe0og10Hrej/ibbKjvkdKguRXK7x.M5hI0ltRFLnG','Nguyen',3);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_role`
--

DROP TABLE IF EXISTS `account_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account_role` (
  `account_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  KEY `FKrs2s3m3039h0xt8d5yhwbuyam` (`role_id`),
  KEY `FK1f8y4iy71kb1arff79s71j0dh` (`account_id`),
  CONSTRAINT `FK1f8y4iy71kb1arff79s71j0dh` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`),
  CONSTRAINT `FKrs2s3m3039h0xt8d5yhwbuyam` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_role`
--

LOCK TABLES `account_role` WRITE;
/*!40000 ALTER TABLE `account_role` DISABLE KEYS */;
INSERT INTO `account_role` VALUES (1,1),(2,2),(3,2);
/*!40000 ALTER TABLE `account_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `category_id` bigint NOT NULL,
  `description` tinytext,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'Luận văn và báo cáo','Luận văn - Báo cáo'),(2,'Kinh tế tài chính','Kinh tế tài chính'),(3,'Ngoại ngữ','Ngoại ngữ'),(4,'Giáo dục và đào tạo','Giáo dục - Đào tạo'),(5,'Khoa học và công nghệ','Khoa học - Công nghệ'),(6,'Y tế sức khỏe','Y tế - Sức khỏe');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `document`
--

DROP TABLE IF EXISTS `document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `document` (
  `document_id` bigint NOT NULL AUTO_INCREMENT,
  `content` longtext,
  `create_at` datetime(6) NOT NULL,
  `description` tinytext,
  `status` smallint NOT NULL,
  `thumbnail` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `update_at` datetime(6) NOT NULL,
  `views` int DEFAULT NULL,
  `author_id` bigint NOT NULL,
  `category_id` bigint NOT NULL,
  PRIMARY KEY (`document_id`),
  KEY `FKegc4plit7eewymjwtrr02r89o` (`author_id`),
  KEY `FK1vwugdy4y8ivgpikjcuojibc0` (`category_id`),
  CONSTRAINT `FK1vwugdy4y8ivgpikjcuojibc0` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`),
  CONSTRAINT `FKegc4plit7eewymjwtrr02r89o` FOREIGN KEY (`author_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `document`
--

LOCK TABLES `document` WRITE;
/*!40000 ALTER TABLE `document` DISABLE KEYS */;
INSERT INTO `document` VALUES (5,'uploads/Trading-in-the-Zone.-Ban-Tieng-Viet.pdf','2025-06-01 16:02:35.134991','',1,'uploads/Trading-in-the-Zone.-Ban-Tieng-Viet.pdf.png','Trading in the zone','2025-06-01 16:02:35.134991',0,1,2),(6,'uploads/XLA.pdf','2025-06-01 16:06:22.867392','',1,'uploads/XLA.pdf.png','Giáo trình xử lý ảnh','2025-06-01 16:18:48.110273',1,1,4),(7,'uploads/Hoi_uc_cua_mot_thien_tai_dau_tu-_Livermore.pdf','2025-06-01 16:06:51.924637','',1,'uploads/Hoi_uc_cua_mot_thien_tai_dau_tu-_Livermore.pdf.png','Hồi ức của một thiên tài đầu tư','2025-06-01 18:50:38.317455',6,1,2),(8,'uploads/baocaoKNTLVB.pdf','2025-06-01 17:40:22.083470','',1,'uploads/baocaoKNTLVB.pdf.png','Luận đề kỹ năng tạo lập văn bản','2025-06-01 18:24:06.410639',1,3,1),(9,'uploads/baocaoKNTLVB.pdf','2025-06-01 18:04:09.051036','',1,'uploads/baocaoKNTLVB.pdf.png','Luận văn kỹ năng mềm','2025-06-01 18:30:35.513473',1,3,1),(10,'uploads/baocaoKNTLVB.pdf','2025-06-01 18:13:00.182180','',1,'uploads/baocaoKNTLVB.pdf.png','Luận văn kỹ năng 1','2025-06-01 19:14:35.964550',1,3,1),(11,'uploads/baocaoKNTLVB.pdf','2025-06-01 18:16:58.028708','',1,'uploads/baocaoKNTLVB.pdf.png','Luận văn kỹ năng mềm m','2025-06-01 18:40:36.970987',1,3,1),(12,'uploads/Bai_giang_Cac_he_thong_phan_tan.pdf','2025-06-01 18:19:10.381958','',0,'uploads/Bai_giang_Cac_he_thong_phan_tan.pdf.png','Đề ôn TOEIC','2025-06-01 18:19:10.381958',0,2,3);
/*!40000 ALTER TABLE `document` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `history_download`
--

DROP TABLE IF EXISTS `history_download`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `history_download` (
  `date` datetime(6) NOT NULL,
  `account_id` bigint NOT NULL,
  `document_id` bigint NOT NULL,
  PRIMARY KEY (`account_id`,`document_id`),
  KEY `FKt0y7crv9miv98afbtho8exmrp` (`document_id`),
  CONSTRAINT `FKmuif17rwww34679qol7ax2irx` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`),
  CONSTRAINT `FKt0y7crv9miv98afbtho8exmrp` FOREIGN KEY (`document_id`) REFERENCES `document` (`document_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `history_download`
--

LOCK TABLES `history_download` WRITE;
/*!40000 ALTER TABLE `history_download` DISABLE KEYS */;
INSERT INTO `history_download` VALUES ('2025-06-01 19:14:39.352044',1,10),('2025-06-01 18:50:41.617653',3,7);
/*!40000 ALTER TABLE `history_download` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rating`
--

DROP TABLE IF EXISTS `rating`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rating` (
  `rate` smallint NOT NULL,
  `account_id` bigint NOT NULL,
  `document_id` bigint NOT NULL,
  PRIMARY KEY (`account_id`,`document_id`),
  KEY `FK4aj1i620ksrvngbexxcnk7xbs` (`document_id`),
  CONSTRAINT `FK4aj1i620ksrvngbexxcnk7xbs` FOREIGN KEY (`document_id`) REFERENCES `document` (`document_id`),
  CONSTRAINT `FKbmk8g2rph271ns01tundsayfk` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`),
  CONSTRAINT `rating_chk_1` CHECK (((`rate` >= 1) and (`rate` <= 5)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rating`
--

LOCK TABLES `rating` WRITE;
/*!40000 ALTER TABLE `rating` DISABLE KEYS */;
INSERT INTO `rating` VALUES (5,2,7);
/*!40000 ALTER TABLE `rating` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT,
  `description` tinytext,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'admin','admin'),(2,'user','user'),(3,'guest user','guest');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `address` tinytext,
  `date_of_birth` date NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `gender` bit(1) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,NULL,'2003-01-01','nguyenphilong@gmail.com',_binary '\0','Long',NULL),(2,NULL,'2001-01-01','nguyenkhang@gmail.com',_binary '\0','Khang',NULL),(3,NULL,'2002-01-01','nguyen@gmail.com',_binary '\0','Nguyen',NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-01 19:31:58