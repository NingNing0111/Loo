CREATE DATABASE IF NOT EXISTS `LOO_DB`
CHARACTER SET utf8;

USE `LOO_DB`;
SET GLOBAL time_zone = '+8:00';
SET time_zone = '+8:00';

DROP TABLE IF EXISTS `server_info`;
CREATE TABLE `server_info`(
    id VARCHAR(64) PRIMARY KEY COMMENT 'id',
    server_name VARCHAR(64) NOT NULL COMMENT '服务端注册的名称',
    os_name VARCHAR(64) COMMENT '服务端操作系统名称',
    os_arch VARCHAR(128) COMMENT '操作系统架构',
    os_version VARCHAR(64) COMMENT 'OS版本',
    register_time DATETIME COMMENT '注册时间',
    server_host VARCHAR(256) COMMENT '服务端地址',
    server_port INT COMMENT '服务端端口',
    is_live bit(1) NOT NULL DEFAULT b'0' COMMENT '是否在线',
    create_time DATETIME NOT NULL DEFAULT  CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除'
);

DROP TABLE IF EXISTS `server_system_info`;
CREATE TABLE `server_system_info`(
    id VARCHAR(64) PRIMARY KEY COMMENT 'id',
    server_id VARCHAR(64) NOT NULL COMMENT '服务端ID',
    jvm_max_memory BIGINT COMMENT 'JVM能够使用的最大内存量',
    jvm_total_memory BIGINT COMMENT 'JVM当前已分配的内存总量',
    jvm_usable_memory BIGINT COMMENT '系统中的可用内存',
    jvm_free_memory BIGINT COMMENT '当前JVM已分配内存中未被使用的可用内存量',
    cpu_usage DOUBLE COMMENT 'CPU使用率',
    system_load DOUBLE COMMENT '系统负载',
    cpu_cores DOUBLE COMMENT 'cpu核数',
    thread_count INT COMMENT '线程数',
    gc_count INT COMMENT 'GC总次数',
    gc_time INT COMMENT 'GC总耗时',
    disk_total BIGINT COMMENT '磁盘总容量',
    disk_free BIGINT COMMENT '磁盘剩余容量',
    register_time DATETIME COMMENT '入库时间',
    create_time DATETIME NOT NULL DEFAULT  CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除'
);

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    id BIGINT PRIMARY KEY  AUTO_INCREMENT COMMENT 'id',
    username varchar(64) NOT NULL COMMENT '用户名',
    password varchar(256) NOT NULL COMMENT '密码',
    role VARCHAR(32) NOT NULL DEFAULT 'userDO' COMMENT '角色',
    create_time DATETIME NOT NULL DEFAULT  CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除'
);


DROP TABLE IF EXISTS `server_client`;
CREATE TABLE `server_client` (
    id BIGINT PRIMARY KEY  AUTO_INCREMENT COMMENT 'id',
    server_id VARCHAR(255) NOT NULL COMMENT '服务端 ID',
    client_host VARCHAR(255) NOT NULL COMMENT '接入的客户端主机地址',
    client_port INT NOT NULL COMMENT '接入的客户端端口',
    license_key VARCHAR(255) COMMENT '授权码',
    is_live BOOLEAN DEFAULT FALSE COMMENT '是否存活 默认为 false',
    create_time DATETIME NOT NULL DEFAULT  CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除'
);

DROP TABLE IF EXISTS `visitor_config`;
CREATE TABLE `visitor_config` (
    id BIGINT PRIMARY KEY  AUTO_INCREMENT COMMENT 'id',
    type INTEGER NOT NULL COMMENT '配置类型',
    server_name VARCHAR(64) COMMENT '服务名称',
    black_list TEXT COMMENT '黑名单',
    white_list TEXT COMMENT '白名单',
    create_time DATETIME NOT NULL DEFAULT  CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除'
)


