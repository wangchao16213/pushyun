-- phpMyAdmin SQL Dump
-- version 4.0.10.16
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Nov 26, 2018 at 08:29 AM
-- Server version: 5.1.73
-- PHP Version: 5.3.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `pushyun`
--

-- --------------------------------------------------------

--
-- Table structure for table `base_config`
--

CREATE TABLE IF NOT EXISTS `base_config` (
  `id` varchar(40) NOT NULL COMMENT 'id',
  `code` varchar(500) DEFAULT NULL COMMENT '',
  `name` varchar(500) DEFAULT NULL COMMENT '',
  `value` varchar(500) DEFAULT NULL COMMENT 'ֵ',
  `remark` varchar(500) DEFAULT NULL COMMENT '',
  `state` varchar(2) DEFAULT NULL COMMENT '',
  `createtime` datetime DEFAULT NULL COMMENT '',
  `updatetime` datetime DEFAULT NULL COMMENT '',
  `createuserid` varchar(40) DEFAULT NULL COMMENT '',
  `updateuserid` varchar(40) DEFAULT NULL COMMENT '',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='';

-- --------------------------------------------------------

--
-- Table structure for table `business_channel`
--

CREATE TABLE IF NOT EXISTS `business_channel` (
  `id` varchar(40) COLLATE utf8_unicode_ci NOT NULL COMMENT 'id',
  `code` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '渠道编码',
  `name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '渠道名称',
  `sign` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '识别标志',
  `version` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '版本',
  `sendermac` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '下发通道MAC',
  `sendername` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '下发通道名称',
  `sniffernames` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '镜像名称(逗号隔开)',
  `routermac` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '网关MAC',
  `serveraddress` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '服务器地址',
  `threadnum` int(11) DEFAULT NULL COMMENT '运行线程',
  `hostthreadnum` int(11) DEFAULT NULL,
  `nohostthreadnum` int(11) DEFAULT NULL,
  `onlinestate` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '在线状态',
  `traffic` float DEFAULT '0',
  `matchrulenum` bigint(20) DEFAULT '0',
  `totalnum` bigint(20) DEFAULT '0',
  `fileaddress` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `pushserveraddress` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '推送服务器地址',
  `dnsserveraddress` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `remark` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `state` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '状态',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',
  `createuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `updateuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='渠道表';

-- --------------------------------------------------------

--
-- Table structure for table `business_channel_cmd`
--

CREATE TABLE IF NOT EXISTS `business_channel_cmd` (
  `id` varchar(40) COLLATE utf8_unicode_ci NOT NULL COMMENT 'id',
  `business_channel_id` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '渠道表_id',
  `cmd` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '命令',
  `type` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `remark` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `state` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '状态',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',
  `createuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `updateuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_5` (`business_channel_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='渠道命令表';

-- --------------------------------------------------------

--
-- Table structure for table `business_channel_daemon`
--

CREATE TABLE IF NOT EXISTS `business_channel_daemon` (
  `id` varchar(40) COLLATE utf8_unicode_ci NOT NULL COMMENT 'id',
  `business_channel_id` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '渠道表_id',
  `exec` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '程序名',
  `unzip` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '是否解压',
  `url` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '链接',
  `seq` int(11) DEFAULT NULL COMMENT '序列',
  `remark` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `state` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '状态',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',
  `createuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `updateuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_6` (`business_channel_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='渠道程序表';

-- --------------------------------------------------------

--
-- Table structure for table `business_dns`
--

CREATE TABLE IF NOT EXISTS `business_dns` (
  `id` varchar(40) COLLATE utf8_unicode_ci NOT NULL COMMENT 'id',
  `business_channel_id` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '渠道表_id',
  `host` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '域名',
  `exact` varchar(500) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '精确匹配',
  `fuzzy` varchar(500) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '模糊匹配',
  `pushrate` int(11) DEFAULT NULL COMMENT '推送频率',
  `urlfilter` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'url过滤条件',
  `ratekey` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '频率关键词',
  `num` int(11) DEFAULT '0',
  `content` text COLLATE utf8_unicode_ci COMMENT '推送内容',
  `remark` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `state` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '状态',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',
  `createuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `updateuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_1` (`business_channel_id`),
  KEY `index_name` (`business_channel_id`),
  KEY `index_name_1` (`business_channel_id`,`state`),
  KEY `index_channel` (`business_channel_id`),
  KEY `index_state` (`state`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='规则表';

-- --------------------------------------------------------

--
-- Table structure for table `business_pushserver`
--

CREATE TABLE IF NOT EXISTS `business_pushserver` (
  `id` varchar(40) COLLATE utf8_unicode_ci NOT NULL COMMENT 'id',
  `code` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '编码',
  `name` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '名称',
  `sign` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '识别标志',
  `version` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '版本',
  `sendermac` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '下发通道MAC',
  `sendername` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '下发通道名称',
  `routermac` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '网关MAC',
  `serveraddress` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '服务器地址',
  `remark` varchar(5000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `state` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '状态',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',
  `createuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `updateuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='推送服务器';

-- --------------------------------------------------------

--
-- Table structure for table `business_rule`
--

CREATE TABLE IF NOT EXISTS `business_rule` (
  `id` varchar(40) COLLATE utf8_unicode_ci NOT NULL COMMENT 'id',
  `business_channel_id` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '渠道表_id',
  `host` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '域名',
  `exact` varchar(500) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '精确匹配',
  `fuzzy` varchar(500) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '模糊匹配',
  `pushrate` int(11) DEFAULT NULL COMMENT '推送频率',
  `urlfilter` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'url过滤条件',
  `ratekey` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '频率关键词',
  `num` int(11) DEFAULT '0',
  `remark` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `state` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '状态',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',
  `createuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `updateuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_1` (`business_channel_id`),
  KEY `index_name` (`business_channel_id`),
  KEY `index_name_1` (`business_channel_id`,`state`),
  KEY `index_channel` (`business_channel_id`),
  KEY `index_state` (`state`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='规则表';

-- --------------------------------------------------------

--
-- Table structure for table `business_rule_detail`
--

CREATE TABLE IF NOT EXISTS `business_rule_detail` (
  `id` varchar(40) COLLATE utf8_unicode_ci NOT NULL COMMENT 'id',
  `business_rule_id` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '规则表_id',
  `ratekey` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `pushrate` int(11) DEFAULT NULL,
  `content` text COLLATE utf8_unicode_ci COMMENT '推送的内容',
  `type` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '推送类型',
  `num` int(11) DEFAULT '0',
  `remark` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `state` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '状态',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',
  `createuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `updateuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_2` (`business_rule_id`),
  KEY `index_business_rule` (`business_rule_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='规则明细表';

-- --------------------------------------------------------

--
-- Table structure for table `business_task`
--

CREATE TABLE IF NOT EXISTS `business_task` (
  `id` varchar(40) COLLATE utf8_unicode_ci NOT NULL COMMENT 'id',
  `business_channel_id` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '渠道ID',
  `code` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '任务名称',
  `type` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '任务类型',
  `tasktime` datetime DEFAULT NULL COMMENT '任务时间',
  `uploadfile` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '上传文件',
  `downloadfile` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '下载文件',
  `content` varchar(10000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `remark` varchar(5000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `state` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '状态',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',
  `createuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `updateuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='任务表';

-- --------------------------------------------------------

--
-- Table structure for table `business_whitelist`
--

CREATE TABLE IF NOT EXISTS `business_whitelist` (
  `id` varchar(40) COLLATE utf8_unicode_ci NOT NULL COMMENT 'id',
  `business_channel_id` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '渠道表_id',
  `account` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '账号',
  `ip` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'ip',
  `remark` varchar(5000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `state` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '状态',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',
  `createuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `updateuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_11` (`business_channel_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='白名单';

-- --------------------------------------------------------

--
-- Table structure for table `data_channel_log`
--

CREATE TABLE IF NOT EXISTS `data_channel_log` (
  `id` varchar(40) COLLATE utf8_unicode_ci NOT NULL COMMENT 'id',
  `business_channel_id` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '渠道表_id',
  `sdate` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '年月日',
  `ip` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'ip',
  `replyrecord` text COLLATE utf8_unicode_ci COMMENT '回复信息',
  `remark` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `state` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '状态',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',
  `createuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `updateuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_3` (`business_channel_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='渠道请求日志表';

-- --------------------------------------------------------

--
-- Table structure for table `data_statistics`
--

CREATE TABLE IF NOT EXISTS `data_statistics` (
  `id` varchar(40) COLLATE utf8_unicode_ci NOT NULL COMMENT 'id',
  `business_channel_id` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '渠道表_id',
  `sdate` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '统计年月日',
  `business_rule_id` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '规则ID',
  `exact` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '精确匹配',
  `fuzzy` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '模糊匹配',
  `count` int(11) NOT NULL DEFAULT '0' COMMENT '数量',
  `remark` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `state` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '状态',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',
  `createuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `updateuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_4` (`business_channel_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='统计表';

-- --------------------------------------------------------

--
-- Table structure for table `log_rule_statistics`
--

CREATE TABLE IF NOT EXISTS `log_rule_statistics` (
  `id` varchar(40) COLLATE utf8_unicode_ci NOT NULL COMMENT 'id',
  `business_channel_id` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '渠道表_id',
  `business_rule_detail_id` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '规则明_id',
  `business_rule_id` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '规则表_id',
  `sdate` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '统计年月日',
  `num` int(11) DEFAULT NULL COMMENT '数量',
  `remark` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `state` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '状态',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',
  `createuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `updateuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_10` (`business_channel_id`),
  KEY `FK_Relationship_8` (`business_rule_detail_id`),
  KEY `FK_Relationship_9` (`business_rule_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='规则统计日志表';

-- --------------------------------------------------------

--
-- Table structure for table `log_sniffer_statistics`
--

CREATE TABLE IF NOT EXISTS `log_sniffer_statistics` (
  `id` varchar(40) COLLATE utf8_unicode_ci NOT NULL COMMENT 'id',
  `business_channel_id` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '渠道表_id',
  `sdate` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '统计年月日',
  `sniffername` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '网口',
  `traffic` float DEFAULT NULL COMMENT '流量',
  `matchrulenum` bigint(20) DEFAULT NULL COMMENT '匹配规则数量',
  `totalnum` bigint(20) DEFAULT NULL COMMENT '总处理数量',
  `reporttime` datetime DEFAULT NULL COMMENT '上报时间',
  `remark` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `state` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '状态',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',
  `createuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `updateuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_7` (`business_channel_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='网口统计日志表';

-- --------------------------------------------------------

--
-- Table structure for table `manage_menu`
--

CREATE TABLE IF NOT EXISTS `manage_menu` (
  `id` varchar(40) COLLATE utf8_unicode_ci NOT NULL COMMENT 'id',
  `code` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '编码',
  `name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '名称',
  `url` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '链接',
  `seq` int(11) DEFAULT NULL COMMENT '序号',
  `parentid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '上级id',
  `remark` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `state` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '状态',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',
  `createuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `updateuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='菜单表';

-- --------------------------------------------------------

--
-- Table structure for table `manage_user`
--

CREATE TABLE IF NOT EXISTS `manage_user` (
  `id` varchar(40) COLLATE utf8_unicode_ci NOT NULL COMMENT 'id',
  `username` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '用户名',
  `passwd` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '密码',
  `email` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `nowloginip` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '本次登录ip',
  `lastloginip` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '上次登录ip',
  `logintimes` int(11) DEFAULT NULL COMMENT '登录次数',
  `lastlogintime` datetime DEFAULT NULL COMMENT '上次登录时间',
  `remark` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `state` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '状态',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',
  `createuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `updateuserid` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='用户表';

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
