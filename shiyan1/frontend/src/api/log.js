import request from '@/utils/request'

// 登录日志API
export function loginLogList(params) {
  return request({
    url: '/system/loginlog/list',
    method: 'get',
    params
  })
}

export function deleteLoginLog(logIds) {
  return request({
    url: `/system/loginlog/${logIds}`,
    method: 'delete'
  })
}

export function cleanLoginLog() {
  return request({
    url: '/system/loginlog/clean',
    method: 'delete'
  })
}

export function exportLoginLog() {
  return request({
    url: '/system/loginlog/export',
    method: 'get'
  })
}

// 操作日志API
export function operLogList(params) {
  return request({
    url: '/system/operlog/list',
    method: 'get',
    params
  })
}

export function deleteOperLog(operIds) {
  return request({
    url: `/system/operlog/${operIds}`,
    method: 'delete'
  })
}

export function cleanOperLog() {
  return request({
    url: '/system/operlog/clean',
    method: 'delete'
  })
}

export function exportOperLog() {
  return request({
    url: '/system/operlog/export',
    method: 'get'
  })
}

// 系统配置API
export function configList(params) {
  return request({
    url: '/system/config/list',
    method: 'get',
    params
  })
}

export function getConfig(configId) {
  return request({
    url: `/system/config/${configId}`,
    method: 'get'
  })
}

export function getConfigByKey(configKey) {
  return request({
    url: `/system/config/key/${configKey}`,
    method: 'get'
  })
}

export function addConfig(data) {
  return request({
    url: '/system/config',
    method: 'post',
    data
  })
}

export function updateConfig(data) {
  return request({
    url: '/system/config',
    method: 'put',
    data
  })
}

export function deleteConfig(configIds) {
  return request({
    url: `/system/config/${configIds}`,
    method: 'delete'
  })
}

export function refreshConfigCache() {
  return request({
    url: '/system/config/refreshCache',
    method: 'post'
  })
}