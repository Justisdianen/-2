/**
 * 菜单管理API接口
 * 文件路径: frontend/src/api/menu.js
 */
import request from '@/utils/request'

// 查询菜单列表
export function getMenuList(params) {
  return request({
    url: '/system/menu/list',
    method: 'get',
    params
  })
}

// 查询菜单树形结构
export function getMenuTree(params) {
  return request({
    url: '/system/menu/tree',
    method: 'get',
    params
  })
}

// 查询菜单详情
export function getMenu(menuId) {
  return request({
    url: '/system/menu/' + menuId,
    method: 'get'
  })
}

// 新增菜单
export function addMenu(data) {
  return request({
    url: '/system/menu',
    method: 'post',
    data
  })
}

// 修改菜单
export function updateMenu(data) {
  return request({
    url: '/system/menu',
    method: 'put',
    data
  })
}

// 删除菜单
export function deleteMenu(menuIds) {
  return request({
    url: '/system/menu/' + menuIds,
    method: 'delete'
  })
}

// 查询用户菜单列表（用于权限控制）
export function getUserMenu(userId) {
  return request({
    url: '/system/menu/userMenu/' + userId,
    method: 'get'
  })
}