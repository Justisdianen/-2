-- =============================================
-- 若依管理系统 - 数据库索引优化脚本
-- 文件路径: backend/sql/init_index.sql
-- 适用版本: MySQL 8.0+
-- 作者: 若依团队
-- 创建时间: 2026-06-26
-- =============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- sys_user 用户表索引优化
-- =============================================
-- 用户名校验索引（用户名唯一查询）
CREATE INDEX idx_sys_user_user_name ON sys_user(user_name);

-- 昵称索引（模糊查询）
CREATE INDEX idx_sys_user_nick_name ON sys_user(nick_name);

-- 部门ID索引（按部门查询用户）
CREATE INDEX idx_sys_user_dept_id ON sys_user(dept_id);

-- 状态索引（按状态筛选）
CREATE INDEX idx_sys_user_status ON sys_user(status);

-- 创建时间索引（排序查询）
CREATE INDEX idx_sys_user_create_time ON sys_user(create_time);

-- =============================================
-- sys_role 角色表索引优化
-- =============================================
-- 角色名称索引（角色名唯一校验）
CREATE INDEX idx_sys_role_role_name ON sys_role(role_name);

-- 角色权限标识索引（角色key唯一校验）
CREATE INDEX idx_sys_role_role_key ON sys_role(role_key);

-- 状态索引（筛选启用/禁用角色）
CREATE INDEX idx_sys_role_status ON sys_role(status);

-- 排序号索引（角色排序查询）
CREATE INDEX idx_sys_role_role_sort ON sys_role(role_sort);

-- =============================================
-- sys_menu 菜单表索引优化
-- =============================================
-- 父菜单ID索引（树形结构查询）
CREATE INDEX idx_sys_menu_parent_id ON sys_menu(parent_id);

-- 菜单名称索引（模糊查询）
CREATE INDEX idx_sys_menu_menu_name ON sys_menu(menu_name);

-- 菜单类型索引（按类型筛选）
CREATE INDEX idx_sys_menu_menu_type ON sys_menu(menu_type);

-- 状态索引（筛选启用/禁用菜单）
CREATE INDEX idx_sys_menu_status ON sys_menu(status);

-- 排序号索引（菜单排序）
CREATE INDEX idx_sys_menu_order_num ON sys_menu(order_num);

-- =============================================
-- sys_dept 部门表索引优化
-- =============================================
-- 父部门ID索引（树形结构查询）
CREATE INDEX idx_sys_dept_parent_id ON sys_dept(parent_id);

-- 部门名称索引（模糊查询）
CREATE INDEX idx_sys_dept_dept_name ON sys_dept(dept_name);

-- 状态索引（筛选启用/禁用部门）
CREATE INDEX idx_sys_dept_status ON sys_dept(status);

-- 排序号索引（部门排序）
CREATE INDEX idx_sys_dept_order_num ON sys_dept(order_num);

-- =============================================
-- sys_user_role 用户角色关联表索引优化
-- =============================================
-- 用户ID索引（查询用户角色）
CREATE INDEX idx_sys_user_role_user_id ON sys_user_role(user_id);

-- 角色ID索引（查询角色用户）
CREATE INDEX idx_sys_user_role_role_id ON sys_user_role(role_id);

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- 索引创建完成提示
-- =============================================
SELECT '索引创建完成' AS result;