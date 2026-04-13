<template>
  <view class="login-page">
    <!-- 顶部装饰 -->
    <view class="top-decoration">
      <view class="circle circle-1"></view>
      <view class="circle circle-2"></view>
      <view class="circle circle-3"></view>
    </view>

    <!-- Logo 和标题 -->
    <view class="header">
      <view class="logo">📸</view>
      <text class="title">家庭记忆</text>
      <text class="subtitle">记录每一个美好瞬间</text>
    </view>

    <!-- 登录/注册切换 -->
    <view class="tab-bar">
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 'login' }"
        @click="currentTab = 'login'"
      >
        登录
      </view>
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 'register' }"
        @click="currentTab = 'register'"
      >
        注册
      </view>
    </view>

    <!-- 表单区域 -->
    <view class="form-container">
      <!-- 登录表单 -->
      <view v-if="currentTab === 'login'" class="form">
        <view class="form-item">
          <text class="label">用户名</text>
          <input 
            class="input" 
            v-model="loginForm.username" 
            placeholder="请输入用户名"
            placeholder-class="placeholder"
          />
        </view>

        <view class="form-item">
          <text class="label">密码</text>
          <input 
            class="input" 
            v-model="loginForm.password" 
            :password="!showPassword"
            placeholder="请输入密码"
            placeholder-class="placeholder"
          />
          <text class="toggle-password" @click="showPassword = !showPassword">
            {{ showPassword ? '🙈' : '👁️' }}
          </text>
        </view>

        <button 
          class="submit-btn" 
          :class="{ loading: loading }"
          :disabled="loading"
          @click="handleLogin"
        >
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </view>

      <!-- 注册表单 -->
      <view v-if="currentTab === 'register'" class="form">
        <view class="form-item">
          <text class="label">用户名</text>
          <input 
            class="input" 
            v-model="registerForm.username" 
            placeholder="请输入用户名"
            placeholder-class="placeholder"
          />
        </view>

        <view class="form-item">
          <text class="label">邮箱</text>
          <input 
            class="input" 
            v-model="registerForm.email" 
            type="email"
            placeholder="请输入邮箱"
            placeholder-class="placeholder"
          />
        </view>

        <view class="form-item">
          <text class="label">密码</text>
          <input 
            class="input" 
            v-model="registerForm.password" 
            :password="!showRegisterPassword"
            placeholder="请输入密码（至少6位）"
            placeholder-class="placeholder"
          />
          <text class="toggle-password" @click="showRegisterPassword = !showRegisterPassword">
            {{ showRegisterPassword ? '🙈' : '👁️' }}
          </text>
        </view>

        <view class="form-item">
          <text class="label">确认密码</text>
          <input 
            class="input" 
            v-model="registerForm.confirmPassword" 
            :password="!showConfirmPassword"
            placeholder="请再次输入密码"
            placeholder-class="placeholder"
          />
          <text class="toggle-password" @click="showConfirmPassword = !showConfirmPassword">
            {{ showConfirmPassword ? '🙈' : '👁️' }}
          </text>
        </view>

        <button 
          class="submit-btn" 
          :class="{ loading: loading }"
          :disabled="loading"
          @click="handleRegister"
        >
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </view>
    </view>

    <!-- 底部提示 -->
    <view class="footer">
      <text v-if="currentTab === 'login'" class="footer-text">
        还没有账号？
        <text class="link" @click="currentTab = 'register'">立即注册</text>
      </text>
      <text v-if="currentTab === 'register'" class="footer-text">
        已有账号？
        <text class="link" @click="currentTab = 'login'">立即登录</text>
      </text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

// 当前 tab
const currentTab = ref<'login' | 'register'>('login')

// 密码显示控制
const showPassword = ref(false)
const showRegisterPassword = ref(false)
const showConfirmPassword = ref(false)

// 加载状态
const loading = ref(false)

// 登录表单
const loginForm = reactive({
  username: '',
  password: ''
})

// 注册表单
const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

// 初始化时检查是否已登录
onMounted(() => {
  userStore.initFromStorage()
  if (userStore.isLoggedIn) {
    // 已登录，跳转到首页
    uni.switchTab({
      url: '/pages/media/media'
    })
  }
})

// 登录
const handleLogin = async () => {
  if (!loginForm.username.trim()) {
    uni.showToast({ title: '请输入用户名', icon: 'none' })
    return
  }
  if (!loginForm.password.trim()) {
    uni.showToast({ title: '请输入密码', icon: 'none' })
    return
  }

  loading.value = true
  try {
    const success = await userStore.login(loginForm.username, loginForm.password)
    if (success) {
      uni.showToast({ title: '登录成功', icon: 'success' })
      setTimeout(() => {
        uni.switchTab({
          url: '/pages/media/media'
        })
      }, 1500)
    }
  } catch (e) {
    console.error('登录失败', e)
  } finally {
    loading.value = false
  }
}

// 注册
const handleRegister = async () => {
  if (!registerForm.username.trim()) {
    uni.showToast({ title: '请输入用户名', icon: 'none' })
    return
  }
  if (!registerForm.email.trim()) {
    uni.showToast({ title: '请输入邮箱', icon: 'none' })
    return
  }
  if (!registerForm.password.trim()) {
    uni.showToast({ title: '请输入密码', icon: 'none' })
    return
  }
  if (registerForm.password.length < 6) {
    uni.showToast({ title: '密码至少6位', icon: 'none' })
    return
  }
  if (registerForm.password !== registerForm.confirmPassword) {
    uni.showToast({ title: '两次密码不一致', icon: 'none' })
    return
  }

  loading.value = true
  try {
    const success = await userStore.register(
      registerForm.username,
      registerForm.password,
      registerForm.email
    )
    if (success) {
      uni.showToast({ title: '注册成功，请登录', icon: 'success' })
      currentTab.value = 'login'
      // 清空注册表单
      registerForm.username = ''
      registerForm.email = ''
      registerForm.password = ''
      registerForm.confirmPassword = ''
    }
  } catch (e) {
    console.error('注册失败', e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  padding: 40rpx;
  box-sizing: border-box;
}

/* 顶部装饰圆 */
.top-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 400rpx;
  overflow: hidden;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.circle-1 {
  width: 300rpx;
  height: 300rpx;
  top: -100rpx;
  right: -50rpx;
}

.circle-2 {
  width: 200rpx;
  height: 200rpx;
  top: 100rpx;
  left: -80rpx;
}

.circle-3 {
  width: 150rpx;
  height: 150rpx;
  top: 250rpx;
  right: 100rpx;
}

/* 头部 */
.header {
  position: relative;
  text-align: center;
  padding-top: 120rpx;
  padding-bottom: 80rpx;
}

.logo {
  font-size: 120rpx;
  margin-bottom: 20rpx;
}

.title {
  display: block;
  font-size: 48rpx;
  font-weight: bold;
  color: #ffffff;
  margin-bottom: 12rpx;
}

.subtitle {
  display: block;
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
}

/* Tab 切换 */
.tab-bar {
  position: relative;
  display: flex;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 16rpx;
  padding: 8rpx;
  margin-bottom: 40rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 20rpx 0;
  font-size: 32rpx;
  color: rgba(255, 255, 255, 0.7);
  border-radius: 12rpx;
  transition: all 0.3s;
}

.tab-item.active {
  background: #ffffff;
  color: #667eea;
  font-weight: 500;
}

/* 表单容器 */
.form-container {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 60rpx 40rpx;
  box-shadow: 0 10rpx 40rpx rgba(0, 0, 0, 0.1);
}

.form-item {
  position: relative;
  margin-bottom: 40rpx;
}

.label {
  display: block;
  font-size: 28rpx;
  color: #333;
  margin-bottom: 16rpx;
  font-weight: 500;
}

.input {
  width: 100%;
  height: 88rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 30rpx;
  color: #333;
  box-sizing: border-box;
}

.placeholder {
  color: #999;
}

.toggle-password {
  position: absolute;
  right: 24rpx;
  bottom: 24rpx;
  font-size: 40rpx;
}

/* 提交按钮 */
.submit-btn {
  width: 100%;
  height: 96rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  border: none;
  border-radius: 48rpx;
  font-size: 32rpx;
  font-weight: 500;
  margin-top: 20rpx;
  transition: all 0.3s;
}

.submit-btn:active {
  opacity: 0.8;
}

.submit-btn.loading {
  opacity: 0.6;
}

.submit-btn[disabled] {
  opacity: 0.6;
}

/* 底部 */
.footer {
  text-align: center;
  margin-top: 60rpx;
}

.footer-text {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
}

.link {
  color: #ffffff;
  font-weight: 500;
  text-decoration: underline;
}
</style>
