import { post, get, put } from '@/utils/request'

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  email: string
}

export interface UserInfo {
  id: string
  name: string
  email?: string
  phone?: string
  avatar?: string
}

export interface LoginResponse {
  token: string
  userInfo: UserInfo
}

/**
 * 用户登录
 */
export function login(data: LoginRequest) {
  return post<LoginResponse>('/user/login', data, false)
}

/**
 * 用户注册
 */
export function register(data: RegisterRequest) {
  return post('/user/register', data, false)
}

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
  return get<UserInfo>('/user/info')
}

/**
 * 更新用户信息
 */
export function updateUserInfo(data: Partial<UserInfo>) {
  return put('/user/update', data)
}

/**
 * 退出登录
 */
export function logout() {
  return post('/user/logout')
}
