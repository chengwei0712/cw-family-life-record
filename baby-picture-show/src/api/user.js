import request from './request'

/**
 * 用户登录
 * @param {string} username 用户名
 * @param {string} password 密码
 */
export function login(username, password) {
  return request({
    url: '/user/login',
    method: 'post',
    data: {
      username,
      password
    }
  })
}

/**
 * 用户注册
 * @param {string} username 用户名
 * @param {string} password 密码
 * @param {string} email 邮箱
 */
export function register(username, password, email) {
  return request({
    url: '/user/register',
    method: 'post',
    data: {
      username,
      password,
      email
    }
  })
}

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

/**
 * 更新用户信息
 * @param {object} data 用户信息
 */
export function updateUserInfo(data) {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}

/**
 * 退出登录
 */
export function logout() {
  return request({
    url: '/user/logout',
    method: 'post'
  })
}