/*
 Navicat Premium Data Transfer

 Source Server         : 188（测试库）
 Source Server Type    : MySQL
 Source Server Version : 50718
 Source Host           : 192.168.168.188:3306
 Source Schema         : edu_crm_dev

 Target Server Type    : MySQL
 Target Server Version : 50718
 File Encoding         : 65001

 Date: 03/04/2018 09:47:55
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for job_entity
-- ----------------------------
DROP TABLE IF EXISTS `job_entity`;
CREATE TABLE `job_entity`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务分组，任务名称+组名称应该是唯一的',
  `job_status` char(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务状态',
  `job_data_map_string` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '任务data',
  `trigger_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '触发器名称',
  `trigger_group` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '触发器分组',
  `trigger_time_zone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '触发器时区',
  `is_concurrent_disallowed` char(2) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '任务是否取消并发 （默认是有并发的）',
  `cron_expression` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务运行时间表达式',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '任务描述',
  `spring_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'bean id',
  `job_class` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '包名+类名',
  `method_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '任务调用的方法名',
  `start_time` datetime(0) DEFAULT NULL COMMENT '启动时间',
  `previous_time` datetime(0) DEFAULT NULL COMMENT '前一次运行时间',
  `next_time` datetime(0) DEFAULT NULL COMMENT '下次运行时间',
  `job_exec_count` int(20) DEFAULT 0 COMMENT 'job 运行次数',
  `job_exception_count` int(20) DEFAULT 0 COMMENT 'job 运行异常次数',
  `create_dt` datetime(0) DEFAULT NULL,
  `is_del` tinyint(2) DEFAULT 0 COMMENT '1:已删除，0:未删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
