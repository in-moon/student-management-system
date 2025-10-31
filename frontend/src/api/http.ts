import axios from 'axios'

const http = axios.create({
  baseURL: '/api',
  timeout: 10000
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
    // 兼容某些环境对大小写敏感的处理
    // @ts-expect-error
    config.headers.authorization = `Bearer ${token}`
  }
  // 确保 DELETE 请求携带 JSON body 时设置 Content-Type
  if (config.method === 'delete' && config.data && !config.headers['Content-Type']) {
    config.headers['Content-Type'] = 'application/json'
  }
  return config
})

http.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err?.response?.status === 401) {
      // Token 失效或无效，清理并跳转登录
      localStorage.removeItem('token')
      if (location.pathname !== '/login') {
        location.href = '/login'
      }
    }
    return Promise.reject(err)
  }
)

export default http


