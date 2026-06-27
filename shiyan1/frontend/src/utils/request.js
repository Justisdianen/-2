/**
 * 请求工具类 - 带重复请求取消功能（仅对写操作）
 * 文件路径: frontend/src/utils/request.js
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: import.meta.env.VITE_APP_API_BASE_URL,
  timeout: 10000
})

let pendingRequests = new Map()

const generateKey = (config) => {
  return `${config.method}_${config.url}_${JSON.stringify(config.params || {})}_${JSON.stringify(config.data || {})}`
}

const isWriteOperation = (method) => {
  return ['post', 'put', 'delete', 'patch'].includes((method || 'get').toLowerCase())
}

service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    console.log('Request URL:', config.url)
    console.log('Token from localStorage:', token ? 'exists (' + token.length + ' chars)' : 'null')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
      console.log('Authorization header set:', 'Bearer ' + token.substring(0, 20) + '...')
    }

    if (isWriteOperation(config.method)) {
      const requestKey = generateKey(config)
      if (pendingRequests.has(requestKey)) {
        pendingRequests.get(requestKey).cancel()
      }

      const source = axios.CancelToken.source()
      config.cancelToken = source.token
      pendingRequests.set(requestKey, source)
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  (response) => {
    if (isWriteOperation(response.config.method)) {
      const requestKey = generateKey(response.config)
      pendingRequests.delete(requestKey)
    }

    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res
  },
  (error) => {
    if (axios.isCancel(error)) {
      return Promise.reject(new Error('请求已取消'))
    }

    const config = error.config
    if (config && isWriteOperation(config.method)) {
      const requestKey = generateKey(config)
      pendingRequests.delete(requestKey)
    }

    // 处理401未授权
    if (error.response && error.response.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      window.location.href = '/login'
      return Promise.reject(new Error('登录已过期'))
    }

    ElMessage.error(error.message || '网络异常')
    return Promise.reject(error)
  }
)

export default service