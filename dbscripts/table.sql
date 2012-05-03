-- ----------------------------
--  Table structure for `TEMPLATE`
-- ----------------------------
DROP TABLE IF EXISTS `TEMPLATE`;
CREATE TABLE `TEMPLATE` (
  `id` varchar(100) NOT NULL DEFAULT '',
  `source` longtext NOT NULL,
  `last_modify` datetime NOT NULL,
  `create_time` datetime NOT NULL,
  `type` varchar(10) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `typeIdx` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;