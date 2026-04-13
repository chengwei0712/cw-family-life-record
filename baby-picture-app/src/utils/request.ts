// API 基础配置
// 开发环境：通过 Vite 代理转发到后端
// 生产环境：直接连接后端
const BASE_URL = import.meta.env.DEV ? '/api' : 'http://localhost:9003'

// 确保拼接 URL 不会出现双斜杠
const joinUrl = (base: string, path: string) => {
  if (base.endsWith('/') && path.startsWith('/')) {
    return base + path.slice(1)
  }
  if (!base.endsWith('/') && !path.startsWith('/')) {
    return base + '/' + path
  }
  return base + path
}

interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: any
  header?: any
  needAuth?: boolean
}

interface ResponseData<T = any> {
  code: number
  msg: string
  data: T
  success?: boolean
}

// 请求拦截器
function requestInterceptor(options: RequestOptions) {
  const header = {
    'Content-Type': 'application/json',
    ...options.header
  }

  // 如果需要认证，添加 token
  if (options.needAuth !== false) {
    const token = uni.getStorageSync('token')
    if (token) {
      header['Authorization'] = `Bearer ${token}`
    }
  }

  return {
    ...options,
    header
  }
}

// 响应拦截器
function responseInterceptor<T>(res: UniApp.RequestSuccessCallbackResult): ResponseData<T> {
  const data = res.data as ResponseData<T>

  console.log('API响应:', res.statusCode, data)

  // 检查 HTTP 状态码
  if (res.statusCode >= 200 && res.statusCode < 300) {
    // 检查业务状态码
    if (data.code === 200 || data.success) {
      return data
    } else if (data.code === 401) {
      // 未授权，清除登录状态
      uni.removeStorageSync('token')
      uni.removeStorageSync('userInfo')
      uni.showToast({
        title: '登录已过期，请重新登录',
        icon: 'none'
      })
      // 跳转到登录页
      uni.reLaunch({
        url: '/pages/login/login'
      })
      return Promise.reject(new Error(data.msg || '未授权'))
    } else {
      uni.showToast({
        title: data.msg || '请求失败',
        icon: 'none'
      })
      return Promise.reject(new Error(data.msg || '请求失败'))
    }
  } else {
    uni.showToast({
      title: '网络错误',
      icon: 'none'
    })
    return Promise.reject(new Error('网络错误'))
  }
}

// 通用请求方法
export function request<T = any>(options: RequestOptions): Promise<ResponseData<T>> {
  return new Promise((resolve, reject) => {
    const { url, method = 'GET', data, header, needAuth = true } = requestInterceptor(options)

    // UniApp: GET 请求参数放在 query，其他请求放在 data
    const requestOptions: any = {
      method,
      header
    }
    
    if (method === 'GET') {
      requestOptions.query = data
    } else {
      requestOptions.data = data
    }

    const fullUrl = joinUrl(BASE_URL, url)
    console.log('发起请求:', method, fullUrl, data)

    uni.request({
      url: fullUrl,
      ...requestOptions,
      success: (res) => {
        try {
          const result = responseInterceptor<T>(res)
          resolve(result)
        } catch (err) {
          reject(err)
        }
      },
      fail: (err) => {
        console.error('请求失败:', err)
        uni.showToast({
          title: '网络连接失败',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

// 快捷方法
export function get<T = any>(url: string, data?: any, needAuth = true) {
  return request<T>({ url, method: 'GET', data, needAuth })
}

export function post<T = any>(url: string, data?: any, needAuth = true) {
  return request<T>({ url, method: 'POST', data, needAuth })
}

export function put<T = any>(url: string, data?: any, needAuth = true) {
  return request<T>({ url, method: 'PUT', data, needAuth })
}

export function del<T = any>(url: string, data?: any, needAuth = true) {
  return request<T>({ url, method: 'DELETE', data, needAuth })
}
