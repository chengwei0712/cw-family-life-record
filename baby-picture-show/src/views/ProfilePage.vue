<template>
  <div class="page">
    <header class="page-header">
      <h1>👤 个人中心</h1>
    </header>
    
    <div class="profile-card">
      <div class="profile-header">
        <div class="avatar">{{ userInitial }}</div>
        <div class="profile-info">
          <h2>{{ userName }}</h2>
          <p>{{ userEmail || '未设置邮箱' }}</p>
        </div>
      </div>
      
      <div class="profile-section">
        <h3>账户信息</h3>
        <div class="info-row">
          <span class="label">用户名</span>
          <span class="value">{{ userName }}</span>
        </div>
        <div class="info-row">
          <span class="label">邮箱</span>
          <span class="value">{{ userEmail || '未设置' }}</span>
        </div>
      </div>
      
      <button class="logout-btn" @click="logout">退出登录</button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const userName = computed(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return user.name || '用户'
})

const userEmail = computed(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return user.email || ''
})

const userInitial = computed(() => userName.value.charAt(0).toUpperCase())

const logout = () => {
  if (confirm('确定要退出登录吗？')) {
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('user')
    localStorage.removeItem('token')
    router.push('/login')
  }
}
</script>

<style>
.page {
  padding: 32px;
  min-height: 100%;
  max-width: 600px;
}

.page-header h1 {
  font-size: 28px;
  color: #1a1a1a;
  margin-bottom: 32px;
}

.profile-card {
  background: #fff;
  border-radius: 16px;
  padding: 32px;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding-bottom: 24px;
  border-bottom: 1px solid #eee;
  margin-bottom: 24px;
}

.avatar {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: #fff;
  font-weight: 600;
}

.profile-info h2 {
  font-size: 24px;
  color: #1a1a1a;
  margin-bottom: 4px;
}

.profile-info p {
  color: #888;
  font-size: 14px;
}

.profile-section {
  margin-bottom: 24px;
}

.profile-section h3 {
  font-size: 16px;
  color: #1a1a1a;
  margin-bottom: 16px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 14px 0;
  border-bottom: 1px solid #f5f5f5;
}

.info-row .label {
  color: #888;
  font-size: 14px;
}

.info-row .value {
  color: #1a1a1a;
  font-size: 14px;
  font-weight: 500;
}

.logout-btn {
  width: 100%;
  padding: 14px;
  background: #ffebee;
  color: #c62828;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
}

.logout-btn:hover {
  background: #ffcdd2;
}
</style>