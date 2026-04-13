import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfo } from '@/api/user'
import { login as loginApi, register as registerApi, getUserInfo, logout as logoutApi } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>('')
  const userInfo = ref<UserInfo | null>(null)
  const isLoggedIn = computed(() => !!token.value && !!userInfo.value)

  // 初始化：从本地存储恢复状态
  function initFromStorage() {
    const savedToken = uni.getStorageSync('token')
    const savedUserInfo = uni.getStorageSync('userInfo')
    
    if (savedToken) {
      token.value = savedToken
    }
    if (savedUserInfo) {
      try {
        userInfo.value = JSON.parse(savedUserInfo)
      } catch (e) {
        console.error('解析用户信息失败', e)
      }
    }
  }

  // 登录
  async function login(username: string, password: string) {
    const res = await loginApi({ username, password })
    console.log('登录响应', res)
    
    if (res.code === 200 || res.success) {
      // 后端返回格式：{ id, name, email, token }
      const { id, name, email, token: newToken } = res.data
      
      // 组装成 userInfo
      const newUserInfo: UserInfo = {
        id: String(id),
        name,
        email
      }
      
      token.value = newToken
      userInfo.value = newUserInfo
      
      // 保存到本地存储
      uni.setStorageSync('token', newToken)
      uni.setStorageSync('userInfo', JSON.stringify(newUserInfo))
      
      console.log('登录成功', { token: newToken, userInfo: newUserInfo })
      return true
    }
    console.log('登录失败', res)
    return false
  }

  // 注册
  async function register(username: string, password: string, email: string) {
    const res = await registerApi({ username, password, email })
    return res.code === 200 || res.success
  }

  // 获取用户信息
  async function fetchUserInfo() {
    try {
      const res = await getUserInfo()
      if (res.code === 200 || res.success) {
        userInfo.value = res.data
        uni.setStorageSync('userInfo', JSON.stringify(res.data))
      }
    } catch (e) {
      console.error('获取用户信息失败', e)
    }
  }

  // 退出登录
  async function logout() {
    try {
      await logoutApi()
    } catch (e) {
      console.error('退出登录请求失败', e)
    } finally {
      // 清除状态和本地存储
      token.value = ''
      userInfo.value = null
      uni.removeStorageSync('token')
      uni.removeStorageSync('userInfo')
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    initFromStorage,
    login,
    register,
    fetchUserInfo,
    logout
  }
})
