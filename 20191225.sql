/*
 Navicat Premium Data Transfer

 Source Server         : lj
 Source Server Type    : MySQL
 Source Server Version : 50562
 Source Host           : localhost:3306
 Source Schema         : 20191225

 Target Server Type    : MySQL
 Target Server Version : 50562
 File Encoding         : 65001

 Date: 26/12/2019 14:46:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for s_active
-- ----------------------------
DROP TABLE IF EXISTS `s_active`;
CREATE TABLE `s_active`  (
  `pid` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `type` int(11) NULL DEFAULT 0 COMMENT '分类',
  `date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '日期  yyyy-MM-dd HH:mm:ss',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'IP',
  `other` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参与人',
  `info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '其他信息',
  PRIMARY KEY (`pid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '活动记录表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for s_user
-- ----------------------------
DROP TABLE IF EXISTS `s_user`;
CREATE TABLE `s_user`  (
  `pid` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户名',
  `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '密码   md5',
  `sso` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'sso标识',
  `system` tinyint(4) NULL DEFAULT 0 COMMENT '系统账号  0--否 1--是',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态  0：禁用，1：启用',
  PRIMARY KEY (`pid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of s_user
-- ----------------------------
INSERT INTO `s_user` VALUES (1, 'admin', '21232f297a57a5a743894a0e4a801fc3', '', 1, 1);

SET FOREIGN_KEY_CHECKS = 1;
