CREATE DATABASE IF NOT EXISTS example_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE example_db;

CREATE TABLE IF NOT EXISTS sys_user (
    user_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    dept_id BIGINT DEFAULT NULL COMMENT '部门ID',
    user_name VARCHAR(30) NOT NULL COMMENT '用户账号',
    nick_name VARCHAR(30) NOT NULL COMMENT '用户昵称',
    email VARCHAR(50) DEFAULT '' COMMENT '邮箱',
    phonenumber VARCHAR(11) DEFAULT '' COMMENT '手机号码',
    sex CHAR(1) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
    avatar VARCHAR(100) DEFAULT '' COMMENT '头像路径',
    password VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    status CHAR(1) DEFAULT '1' COMMENT '状态（0禁用 1启用）',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    login_ip VARCHAR(50) DEFAULT '' COMMENT '最后登录IP',
    login_date DATETIME DEFAULT NULL COMMENT '最后登录时间',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT '' COMMENT '备注',
    PRIMARY KEY (user_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE IF NOT EXISTS sys_role (
    role_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(30) NOT NULL COMMENT '角色名称',
    role_key VARCHAR(100) NOT NULL COMMENT '角色权限字符串',
    role_sort INT DEFAULT 0 COMMENT '显示顺序',
    status CHAR(1) DEFAULT '1' COMMENT '角色状态（0禁用 1启用）',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT '' COMMENT '备注',
    PRIMARY KEY (role_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

CREATE TABLE IF NOT EXISTS sys_menu (
    menu_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    order_num INT DEFAULT 0 COMMENT '显示顺序',
    path VARCHAR(200) DEFAULT '' COMMENT '路由地址',
    component VARCHAR(255) DEFAULT '' COMMENT '组件路径',
    query VARCHAR(255) DEFAULT '' COMMENT '路由参数',
    is_frame CHAR(1) DEFAULT '1' COMMENT '是否为外链（0是 1否）',
    is_cache CHAR(1) DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
    menu_type CHAR(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
    visible CHAR(1) DEFAULT '1' COMMENT '显示状态（0隐藏 1显示）',
    status CHAR(1) DEFAULT '1' COMMENT '菜单状态（0禁用 1启用）',
    perms VARCHAR(100) DEFAULT '' COMMENT '权限标识',
    icon VARCHAR(100) DEFAULT '' COMMENT '菜单图标',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT '' COMMENT '备注',
    PRIMARY KEY (menu_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

CREATE TABLE IF NOT EXISTS sys_dept (
    dept_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父部门ID',
    ancestors VARCHAR(500) DEFAULT '' COMMENT '祖级列表',
    dept_name VARCHAR(30) NOT NULL COMMENT '部门名称',
    order_num INT DEFAULT 0 COMMENT '显示顺序',
    leader VARCHAR(20) DEFAULT '' COMMENT '负责人',
    phone VARCHAR(11) DEFAULT '' COMMENT '联系电话',
    email VARCHAR(50) DEFAULT '' COMMENT '邮箱',
    status CHAR(1) DEFAULT '1' COMMENT '部门状态（0禁用 1启用）',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (dept_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

CREATE TABLE IF NOT EXISTS sys_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

INSERT INTO sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time) VALUES
(1, 0, '0', '系统管理部', 1, '张三', '13800138001', 'zhangsan@example.com', '1', '0', 'admin', NOW(), 'admin', NOW()),
(2, 1, '0,1', '研发组', 1, '李四', '13800138002', 'lisi@example.com', '1', '0', 'admin', NOW(), 'admin', NOW()),
(3, 1, '0,1', '测试组', 2, '王五', '13800138003', 'wangwu@example.com', '1', '0', 'admin', NOW(), 'admin', NOW()),
(4, 1, '0,1', '运维组', 3, '赵六', '13800138004', 'zhaoliu@example.com', '1', '0', 'admin', NOW(), 'admin', NOW());

INSERT INTO sys_role (role_id, role_name, role_key, role_sort, status, del_flag, create_by, create_time, update_by, update_time, remark) VALUES
(1, '超级管理员', 'admin', 1, '1', '0', 'admin', NOW(), 'admin', NOW(), '拥有所有权限'),
(2, '普通用户', 'user', 2, '1', '0', 'admin', NOW(), 'admin', NOW(), '拥有基本权限');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
(1, '系统管理', 0, 1, '/system', '', '', '1', '0', 'M', '1', '1', '', 'Setting', 'admin', NOW(), 'admin', NOW(), '系统管理目录'),
(2, '用户管理', 1, 1, '/system/user', 'system/User', '', '1', '0', 'C', '1', '1', 'system:user:list', 'User', 'admin', NOW(), 'admin', NOW(), '用户管理菜单'),
(3, '角色管理', 1, 2, '/system/role', 'system/Role', '', '1', '0', 'C', '1', '1', 'system:role:list', 'UserFilled', 'admin', NOW(), 'admin', NOW(), '角色管理菜单'),
(4, '菜单管理', 1, 3, '/system/menu', 'system/Menu', '', '1', '0', 'C', '1', '1', 'system:menu:list', 'Menu', 'admin', NOW(), 'admin', NOW(), '菜单管理菜单'),
(5, '部门管理', 1, 4, '/system/dept', 'system/Dept', '', '1', '0', 'C', '1', '1', 'system:dept:list', 'OfficeBuilding', 'admin', NOW(), 'admin', NOW(), '部门管理菜单');

INSERT INTO sys_user (user_id, dept_id, user_name, nick_name, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark) VALUES
(1, 2, 'admin', '系统管理员', 'admin@example.com', '13800138000', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTh.ye8WDNGr2SSy5pZ6vFwFvFq', '1', '0', '127.0.0.1', NOW(), 'admin', NOW(), 'admin', NOW(), '系统管理员'),
(2, 2, 'user', '普通用户', 'user@example.com', '13800138005', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTh.ye8WDNGr2SSy5pZ6vFwFvFq', '1', '0', '127.0.0.1', NOW(), 'admin', NOW(), 'admin', NOW(), '普通用户');

INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2);