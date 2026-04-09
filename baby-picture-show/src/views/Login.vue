<template>
  <div class="login-page">
    <div class="login-card">
      <div class="card-header">
        <div class="logo">🏠</div>
        <h1>家庭记忆</h1>
        <p class="subtitle">记录生活的每一个温馨时刻</p>
      </div>
      
      <div class="tabs">
        <button :class="['tab', { active: isLogin }]" @click="isLogin = true">登录</button>
        <button :class="['tab', { active: !isLogin }]" @click="isLogin = false">注册</button>
      </div>
      
      <!-- 登录 -->
      <form v-if="isLogin" @submit.prevent="handleLogin" class="form">
        <div class="field">
          <label>用户名</label>
          <input v-model="loginForm.username" type="text" placeholder="请输入用户名" required />
        </div>
        <div class="field">
          <label>密码</label>
          <input v-model="loginForm.password" type="password" placeholder="请输入密码" required />
        </div>
        <button type="submit" class="btn-primary" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
      
      <!-- 注册 -->
      <form v-else @submit.prevent="handleRegister" class="form">
        <div class="field">
          <label>用户名</label>
          <input v-model="registerForm.username" type="text" placeholder="请输入用户名" required />
        </div>
        <div class="field">
          <label>邮箱</label>
          <input v-model="registerForm.email" type="email" placeholder="请输入邮箱" required />
        </div>
        <div class="field">
          <label>密码</label>
          <input v-model="registerForm.password" type="password" placeholder="至少6位密码" required minlength="6" />
        </div>
        <div class="field">
          <label>确认密码</label>
          <input v-model="registerForm.confirmPassword" type="password" placeholder="再次输入密码" required />
        </div>
        <button type="submit" class="btn-primary" :disabled="loading">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>
      
      <div v-if="msg.text" :class="['msg', msg.type]">{{ msg.text }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { login, register } from '@/api/user'

const router = useRouter()
const isLogin = ref(true)
const loading = ref(false)
const msg = reactive({ text: '', type: 'success' })

const loginForm = reactive({ username: '', password: '' })
const registerForm = reactive({ username: '', email: '', password: '', confirmPassword: '' })

const showMsg = (text, type = 'success') => {
  msg.text = text
  msg.type = type
  setTimeout(() => msg.text = '', 3000)
}

const handleLogin = async () => {
  loading.value = true
  try {
    const res = await login(loginForm.username, loginForm.password)
    if (res.code === 200 || res.success) {
      localStorage.setItem('isLoggedIn', 'true')
      localStorage.setItem('user', JSON.stringify(res.data || { name: loginForm.username }))
      router.push('/dashboard')
    } else {
      showMsg(res.msg || '登录失败', 'error')
    }
  } catch (e) {
    showMsg(e.response?.data?.msg || '登录失败', 'error')
  }
  loading.value = false
}

const handleRegister = async () => {
  if (registerForm.password !== registerForm.confirmPassword) {
    showMsg('两次密码不一致', 'error')
    return
  }
  loading.value = true
  try {
    const res = await register(registerForm.username, registerForm.password, registerForm.email)
    if (res.code === 200 || res.success) {
      showMsg('注册成功，请登录')
      isLogin.value = true
      loginForm.username = registerForm.username
    } else {
      showMsg(res.msg || '注册失败', 'error')
    }
  } catch (e) {
    showMsg(e.response?.data?.msg || '注册失败', 'error')
  }
  loading.value = false
}
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
html, body, #app { height: 100%; width: 100%; }

.login-page {
  min-height: 100vh;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 16px;
  padding: 40px 36px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.2);
}

.card-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo {
  font-size: 48px;
  margin-bottom: 12px;
}

.card-header h1 {
  font-size: 28px;
  color: #1a1a1a;
  margin-bottom: 8px;
}

.subtitle {
  color: #888;
  font-size: 15px;
}

.tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 28px;
}

.tab {
  flex: 1;
  padding: 12px;
  border: none;
  background: #f5f5f5;
  border-radius: 8px;
  font-size: 15px;
  color: #888;
  cursor: pointer;
  transition: all 0.2s;
}

.tab.active {
  background: #667eea;
  color: #fff;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.field label {
  display: block;
  font-size: 14px;
  color: #555;
  margin-bottom: 8px;
  font-weight: 500;
}

.field input {
  width: 100%;
  padding: 14px 16px;
  border: 1px solid #ddd;
  border-radius: 10px;
  font-size: 15px;
  transition: all 0.2s;
}

.field input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102,126,234,0.15);
}

.btn-primary {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 8px;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.msg {
  margin-top: 16px;
  padding: 12px;
  border-radius: 8px;
  text-align: center;
  font-size: 14px;
}
.msg.success { background: #e8f5e9; color: #2e7d32; }
.msg.error { background: #ffebee; color: #c62828; }
</style>