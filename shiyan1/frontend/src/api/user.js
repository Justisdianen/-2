import request from '@/utils/request'

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/info',
    method: 'get'
  })
}

// 更新用户基本信息
export function updateUserInfo(data) {
  return request({
    url: '/system/user/profile',
    method: 'put',
    data
  })
}

// 修改密码
export function updateUserPwd(data) {
  return request({
    url: '/system/user/profile/updatePwd',
    method: 'put',
    params: {
      oldPassword: data.oldPassword,
      newPassword: data.newPassword
    }
  })
}

// 上传头像
export function uploadAvatar(data) {
  return request({
    url: '/system/user/profile/avatar',
    method: 'post',
    data
  })
}

// 用户管理相关API
export function getUserPage(params) {
  return request({
    url: '/system/user/page',
    method: 'get',
    params
  })
}

export function userList(params) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params
  })
}

export function getUser(userId) {
  return request({
    url: `/system/user/${userId}`,
    method: 'get'
  })
}

export function addUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data
  })
}

export function updateUser(data) {
  return request({
    url: '/system/user',
    method: 'put',
    data
  })
}

export function deleteUser(userId) {
  return request({
    url: `/system/user/${userId}`,
    method: 'delete'
  })
}

export function resetUserPwd(userId, password) {
  return request({
    url: '/system/user/resetPwd',
    method: 'put',
    data: { userId, password }
  })
}

export function changeUserStatus(userId, status) {
  return request({
    url: '/system/user/changeStatus',
    method: 'put',
    data: { userId, status }
  })
}

export function exportUser(params) {
  return request({
    url: '/system/user/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export function importUser(data) {
  return request({
    url: '/system/user/import',
    method: 'post',
    data
  })
}

export function importUserTemplate() {
  return request({
    url: '/system/user/importTemplate',
    method: 'get',
    responseType: 'blob'
  })
}