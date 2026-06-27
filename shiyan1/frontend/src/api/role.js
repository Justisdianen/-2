/**
 * 角色管理API接口
 * 文件路径: frontend/src/api/role.js
 */
import request from '@/utils/request'

// 查询角色列表
export function getRoleList(params) {
  return request({
    url: '/system/role/list',
    method: 'get',
    params
  })
}

// 分页查询角色列表
export function getRolePage(params) {
  return request({
    url: '/system/role/page',
    method: 'get',
    params
  })
}

// 查询角色详情
export function getRole(roleId) {
  return request({
    url: '/system/role/' + roleId,
    method: 'get'
  })
}

// 新增角色
export function addRole(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data
  })
}

// 修改角色
export function updateRole(data) {
  return request({
    url: '/system/role',
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(roleIds) {
  return request({
    url: '/system/role/' + roleIds,
    method: 'delete'
  })
}

// 修改角色状态
export function changeRoleStatus(data) {
  return request({
    url: '/system/role/changeStatus',
    method: 'put',
    data
  })
}

// 查询角色选项列表（用于下拉选择）
export function getRoleOption() {
  return request({
    url: '/system/role/optionselect',
    method: 'get'
  })
}

// 查询角色菜单权限
export function getRoleMenu(roleId) {
  return request({
    url: '/system/menu/userMenu/' + roleId,
    method: 'get'
  })
}

// 分配角色菜单权限
export function saveRoleMenu(data) {
  return request({
    url: '/system/userRole/batch',
    method: 'post',
    params: { userId: data.userId },
    data: data.roleIds
  })
}