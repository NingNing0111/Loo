CREATE DATABASE IF NOT EXISTS `ldd-nat-cross`
CHARACTER SET utf8;

USE `ldd-nat-cross`;

DROP TABLE IF EXISTS `server_info`;
CREATE TABLE `server_info`(
    id VARCHAR(64) PRIMARY KEY COMMENT 'id',
    server_id VARCHAR(64) NOT NULL COMMENT '服务端ID',
    server_name VARCHAR(64) NOT NULL COMMENT '服务端注册的名称',
    os_name VARCHAR(64) COMMENT '服务端操作系统名称',
    os_arch VARCHAR(128) COMMENT '操作系统架构',
    os_version VARCHAR(64) COMMENT 'OS版本',
    register_time DATETIME COMMENT '注册时间'
);

DROP TABLE IF EXISTS `server_system_info`;
CREATE TABLE `server_system_info`(
    id VARCHAR(64) PRIMARY KEY COMMENT 'id',
    server_id VARCHAR(64) NOT NULL COMMENT '服务端ID',
    max_memory BIGINT COMMENT 'JVM能够使用的最大内存量',
    total_memory BIGINT COMMENT 'JVM当前已分配的内存总量',
    usable_memory BIGINT COMMENT '系统中的可用内存',
    free_memory BIGINT COMMENT '当前JVM已分配内存中未被使用的可用内存量',
    register_time DATETIME COMMENT '入库时间'
);

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    id BIGINT PRIMARY KEY  AUTO_INCREMENT COMMENT 'id',
    username varchar(64) NOT NULL COMMENT '用户名',
    password varchar(256) NOT NULL COMMENT '密码',
    role VARCHAR(32) NOT NULL DEFAULT 'user' COMMENT '角色'
);



