-- ============================================
-- 若依管理系统 - 数据库初始化脚本
-- 生成时间：2026-06-26
-- 基于需求文档：系统需求.md
-- ============================================

-- ----------------------------
-- 1. 部门表 (sys_dept)
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
    `dept_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门编号',
    `parent_id` BIGINT DEFAULT 0 COMMENT '上级部门编号（顶级部门为0）',
    `dept_name` VARCHAR(100) NOT NULL COMMENT '部门名称',
    `leader` VARCHAR(50) DEFAULT '' COMMENT '部门负责人',
    `phone` VARCHAR(20) DEFAULT '' COMMENT '部门负责人联系电话',
    `order_num` INT DEFAULT 0 COMMENT '排序号',
    `status` CHAR(1) DEFAULT '1' COMMENT '状态（0-禁用，1-启用）',
    `del_flag` CHAR(1) DEFAULT '0' COMMENT '逻辑删除（0-正常，2-删除）',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `ancestors` VARCHAR(500) DEFAULT '' COMMENT '祖级列表',
    PRIMARY KEY (`dept_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_dept_name` (`dept_name`),
    KEY `idx_status` (`status`),
    KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ----------------------------
-- 2. 用户表 (sys_user)
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `user_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户编号',
    `dept_id` BIGINT DEFAULT NULL COMMENT '所属部门编号',
    `user_name` VARCHAR(30) NOT NULL COMMENT '用户名称（登录账号）',
    `nick_name` VARCHAR(30) NOT NULL COMMENT '用户昵称（显示名称）',
    `email` VARCHAR(50) DEFAULT '' COMMENT '邮箱地址',
    `phonenumber` VARCHAR(20) DEFAULT '' COMMENT '手机号码',
    `sex` CHAR(1) DEFAULT '0' COMMENT '性别（0-男，1-女，2-未知）',
    `avatar` VARCHAR(100) DEFAULT '' COMMENT '头像路径',
    `password` VARCHAR(100) DEFAULT '' COMMENT '密码（BCrypt加密）',
    `status` CHAR(1) DEFAULT '1' COMMENT '状态（0-禁用，1-启用）',
    `del_flag` CHAR(1) DEFAULT '0' COMMENT '逻辑删除（0-正常，2-删除）',
    `login_ip` VARCHAR(50) DEFAULT '' COMMENT '最后登录IP',
    `login_date` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_user_name` (`user_name`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_nick_name` (`nick_name`),
    KEY `idx_status` (`status`),
    KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 3. 角色表 (sys_role)
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `role_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色编号',
    `role_name` VARCHAR(30) NOT NULL COMMENT '角色名称',
    `role_key` VARCHAR(100) NOT NULL COMMENT '角色权限标识',
    `role_sort` INT DEFAULT 0 COMMENT '排序号',
    `data_scope` CHAR(1) DEFAULT '1' COMMENT '数据范围（1-全部数据，2-本部门及以下，3-本部门，4-仅本人，5-自定义）',
    `status` CHAR(1) DEFAULT '1' COMMENT '状态（0-禁用，1-启用）',
    `del_flag` CHAR(1) DEFAULT '0' COMMENT '逻辑删除（0-正常，2-删除）',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT '' COMMENT '角色描述',
    PRIMARY KEY (`role_id`),
    UNIQUE KEY `uk_role_name` (`role_name`),
    UNIQUE KEY `uk_role_key` (`role_key`),
    KEY `idx_status` (`status`),
    KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ----------------------------
-- 4. 用户角色关联表 (sys_user_role)
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `user_id` BIGINT NOT NULL COMMENT '用户编号',
    `role_id` BIGINT NOT NULL COMMENT '角色编号',
    PRIMARY KEY (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ----------------------------
-- 5. 菜单表 (sys_menu)
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
    `menu_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜单编号',
    `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '上级菜单编号（顶级菜单为0）',
    `order_num` INT DEFAULT 0 COMMENT '排序号',
    `path` VARCHAR(200) DEFAULT '' COMMENT '路由地址',
    `component` VARCHAR(255) DEFAULT '' COMMENT '组件路径',
    `query` VARCHAR(255) DEFAULT '' COMMENT '路由参数',
    `is_frame` INT DEFAULT 1 COMMENT '是否为外链（0-是，1-否）',
    `is_cache` INT DEFAULT 0 COMMENT '是否缓存（0-否，1-是）',
    `menu_type` CHAR(1) DEFAULT '' COMMENT '菜单类型（1-目录，2-菜单，3-按钮）',
    `visible` CHAR(1) DEFAULT '0' COMMENT '是否显示（0-隐藏，1-显示）',
    `status` CHAR(1) DEFAULT '1' COMMENT '状态（0-禁用，1-启用）',
    `perms` VARCHAR(100) DEFAULT '' COMMENT '权限标识',
    `icon` VARCHAR(100) DEFAULT '#' COMMENT '菜单图标',
    `del_flag` CHAR(1) DEFAULT '0' COMMENT '逻辑删除（0-正常，2-删除）',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT '' COMMENT '菜单描述',
    `route_name` VARCHAR(100) DEFAULT '' COMMENT '路由名称',
    `always_show` INT DEFAULT 0 COMMENT '是否始终显示（0-否，1-是）',
    PRIMARY KEY (`menu_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_menu_name` (`menu_name`),
    KEY `idx_menu_type` (`menu_type`),
    KEY `idx_visible` (`visible`),
    KEY `idx_status` (`status`),
    KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- ----------------------------
-- 6. 角色菜单关联表 (sys_role_menu)
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
    `role_id` BIGINT NOT NULL COMMENT '角色编号',
    `menu_id` BIGINT NOT NULL COMMENT '菜单编号',
    PRIMARY KEY (`role_id`, `menu_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- ----------------------------
-- 7. 岗位表 (sys_post)
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post` (
    `post_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '岗位编号',
    `post_code` VARCHAR(50) NOT NULL COMMENT '岗位编码',
    `post_name` VARCHAR(100) NOT NULL COMMENT '岗位名称',
    `post_sort` INT DEFAULT 0 COMMENT '排序号',
    `status` CHAR(1) DEFAULT '1' COMMENT '状态（0-禁用，1-启用）',
    `del_flag` CHAR(1) DEFAULT '0' COMMENT '逻辑删除（0-正常，2-删除）',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT '' COMMENT '岗位描述',
    PRIMARY KEY (`post_id`),
    UNIQUE KEY `uk_post_code` (`post_code`),
    UNIQUE KEY `uk_post_name` (`post_name`),
    KEY `idx_status` (`status`),
    KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位表';

-- ----------------------------
-- 8. 用户岗位关联表 (sys_user_post)
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post` (
    `user_id` BIGINT NOT NULL COMMENT '用户编号',
    `post_id` BIGINT NOT NULL COMMENT '岗位编号',
    PRIMARY KEY (`user_id`, `post_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户岗位关联表';

-- ----------------------------
-- 9. 角色部门关联表 (sys_role_dept)
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept` (
    `role_id` BIGINT NOT NULL COMMENT '角色编号',
    `dept_id` BIGINT NOT NULL COMMENT '部门编号',
    PRIMARY KEY (`role_id`, `dept_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色部门关联表（自定义数据范围）';

-- ----------------------------
-- 初始化数据：部门表
-- ----------------------------
INSERT INTO `sys_dept` (`dept_id`, `parent_id`, `dept_name`, `leader`, `phone`, `order_num`, `status`, `ancestors`) VALUES
(1, 0, '若依科技', '张三', '13800138000', 1, '1', '0'),
(2, 1, '技术部', '李四', '13800138001', 1, '1', '0,1'),
(3, 1, '产品部', '王五', '13800138002', 2, '1', '0,1'),
(4, 1, '运营部', '赵六', '13800138003', 3, '1', '0,1'),
(5, 2, '前端开发组', '孙七', '13800138004', 1, '1', '0,1,2'),
(6, 2, '后端开发组', '周八', '13800138005', 2, '1', '0,1,2');

-- ----------------------------
-- 初始化数据：角色表
-- ----------------------------
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_key`, `role_sort`, `data_scope`, `status`, `remark`) VALUES
(1, '超级管理员', 'admin', 1, '1', '1', '拥有系统所有权限'),
(2, '普通操作员', 'user', 2, '3', '1', '拥有基础操作权限');

-- ----------------------------
-- 初始化数据：用户表
-- ----------------------------
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `email`, `phonenumber`, `sex`, `password`, `status`) VALUES
(1, 2, 'admin', '超级管理员', 'admin@ruoyi.com', '13800138000', '0', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '1'),
(2, 5, 'user', '普通用户', 'user@ruoyi.com', '13800138006', '0', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '1');

-- ----------------------------
-- 初始化数据：用户角色关联表
-- ----------------------------
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1),
(2, 2);

-- ----------------------------
-- 初始化数据：菜单表（系统管理模块）
-- ----------------------------
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `menu_type`, `visible`, `status`, `perms`, `icon`, `route_name`) VALUES
(1, '系统管理', 0, 1, 'system', '', '1', '1', '1', '', '#icon-system', 'System'),
(2, '用户管理', 1, 1, 'user', 'system/user/index', '2', '1', '1', 'system:user:list', '#icon-user', 'User'),
(3, '新增用户', 2, 1, '', '', '3', '0', '1', 'system:user:add', '', ''),
(4, '编辑用户', 2, 2, '', '', '3', '0', '1', 'system:user:edit', '', ''),
(5, '删除用户', 2, 3, '', '', '3', '0', '1', 'system:user:remove', '', ''),
(6, '角色管理', 1, 2, 'role', 'system/role/index', '2', '1', '1', 'system:role:list', '#icon-peoples', 'Role'),
(7, '新增角色', 6, 1, '', '', '3', '0', '1', 'system:role:add', '', ''),
(8, '编辑角色', 6, 2, '', '', '3', '0', '1', 'system:role:edit', '', ''),
(9, '删除角色', 6, 3, '', '', '3', '0', '1', 'system:role:remove', '', ''),
(10, '菜单管理', 1, 3, 'menu', 'system/menu/index', '2', '1', '1', 'system:menu:list', '#icon-tree-table', 'Menu'),
(11, '新增菜单', 10, 1, '', '', '3', '0', '1', 'system:menu:add', '', ''),
(12, '编辑菜单', 10, 2, '', '', '3', '0', '1', 'system:menu:edit', '', ''),
(13, '删除菜单', 10, 3, '', '', '3', '0', '1', 'system:menu:remove', '', ''),
(14, '部门管理', 1, 4, 'dept', 'system/dept/index', '2', '1', '1', 'system:dept:list', '#icon-tree', 'Dept'),
(15, '新增部门', 14, 1, '', '', '3', '0', '1', 'system:dept:add', '', ''),
(16, '编辑部门', 14, 2, '', '', '3', '0', '1', 'system:dept:edit', '', ''),
(17, '删除部门', 14, 3, '', '', '3', '0', '1', 'system:dept:remove', '', ''),
(18, '岗位管理', 1, 5, 'post', 'system/post/index', '2', '1', '1', 'system:post:list', '#icon-post', 'Post'),
(19, '字典管理', 1, 6, 'dict', 'system/dict/index', '2', '1', '1', 'system:dict:list', '#icon-dict', 'Dict'),
(20, '参数设置', 1, 7, 'config', 'system/config/index', '2', '1', '1', 'system:config:list', '#icon-edit', 'Config'),
(21, '通知公告', 1, 8, 'notice', 'system/notice/index', '2', '1', '1', 'system:notice:list', '#icon-message', 'Notice');

-- ----------------------------
-- 初始化数据：角色菜单关联表（超级管理员拥有所有权限）
-- ----------------------------
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
(1, 6), (1, 7), (1, 8), (1, 9), (1, 10),
(1, 11), (1, 12), (1, 13), (1, 14), (1, 15),
(1, 16), (1, 17), (1, 18), (1, 19), (1, 20), (1, 21);

-- ----------------------------
-- 初始化数据：角色菜单关联表（普通操作员拥有部分权限）
-- ----------------------------
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(2, 1), (2, 2), (2, 14), (2, 21);

-- ----------------------------
-- 初始化数据：岗位表
-- ----------------------------
INSERT INTO `sys_post` (`post_id`, `post_code`, `post_name`, `post_sort`, `status`) VALUES
(1, 'DEV', '开发工程师', 1, '1'),
(2, 'TEST', '测试工程师', 2, '1'),
(3, 'PM', '产品经理', 3, '1'),
(4, 'OP', '运营专员', 4, '1');

-- ----------------------------
-- 初始化数据：用户岗位关联表
-- ----------------------------
INSERT INTO `sys_user_post` (`user_id`, `post_id`) VALUES
(1, 1),
(2, 1);

-- ============================================
-- 建表脚本结束
-- ============================================