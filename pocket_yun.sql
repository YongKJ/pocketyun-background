/*
 Navicat Premium Data Transfer

 Source Server         : 我的阿里云
 Source Server Type    : MySQL
 Source Server Version : 100038
 Source Host           : 47.106.102.217:3306
 Source Schema         : pocket_yun

 Target Server Type    : MySQL
 Target Server Version : 100038
 File Encoding         : 65001

 Date: 08/02/2021 17:54:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for py_paths
-- ----------------------------
DROP TABLE IF EXISTS `py_paths`;
CREATE TABLE `py_paths`  (
  `pathsUUID` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件相对路径UUID',
  `userUUID` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户UUID',
  `path` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件相对路径',
  `filename` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件名',
  `size` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件大小',
  `depth` int(3) NOT NULL COMMENT '路径深度',
  `addTime` datetime(0) NOT NULL COMMENT '文件添加时间',
  `modTime` datetime(0) NOT NULL COMMENT '文件修改时间',
  PRIMARY KEY (`pathsUUID`) USING BTREE,
  INDEX `py_foreign_paths_user`(`userUUID`) USING BTREE,
  CONSTRAINT `py_foreign_paths_user` FOREIGN KEY (`userUUID`) REFERENCES `py_user` (`userUUID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for py_user
-- ----------------------------
DROP TABLE IF EXISTS `py_user`;
CREATE TABLE `py_user`  (
  `userUUID` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户UUID',
  `userName` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `regSex` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '性别',
  `regAge` int(3) NOT NULL COMMENT '年龄',
  `regEmail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '电子邮箱',
  `regPhoto` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '头像',
  `regTime` datetime(0) NOT NULL COMMENT '注册时间',
  `loginTime` datetime(0) NULL DEFAULT NULL COMMENT '登录时间',
  `admin` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否为管理员',
  PRIMARY KEY (`userUUID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
