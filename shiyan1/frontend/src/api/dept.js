/**
 * 部门管理API接口
 * 文件路径: frontend/src/api/dept.js
 */
import request from '@/utils/request'

// 查询部门列表
export function getDeptList(params) {
  return request({
    url: '/system/dept/list',
    method: 'get',
    params
  })
}

// 查询部门树形结构
export function getDeptTree(params) {
  return request({
    url: '/system/dept/tree',
    method: 'get',
    params
  })
}

// 查询部门详情
export function getDept(deptId) {
  return request({
    url: '/system/dept/' + deptId,
    method: 'get'
  })
}

// 新增部门
export function addDept(data) {
  return request({
    url: '/system/dept',
    method: 'post',
    data
  })
}

// 修改部门
export function updateDept(data) {
  return request({
    url: '/system/dept',
    method: 'put',
    data
  })
}

// 删除部门
export function deleteDept(deptIds) {
  return request({
    url: '/system/dept/' + deptIds,
    method: 'delete'
  })
}

// 查询部门下拉树结构（用于用户选择所属部门）
export function getDeptTreeSelect() {
  return request({
    url: '/system/dept/treeSelect',
    method: 'get'
  })
}