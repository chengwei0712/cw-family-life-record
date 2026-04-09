<template>
  <div class="app">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <span class="logo">🏠</span>
        <span class="brand">家庭记忆</span>
      </div>
      
      <nav class="nav">
        <router-link to="/dashboard" class="nav-item">
          <span class="icon">🏠</span>
          <span>首页</span>
        </router-link>
        <router-link to="/media" class="nav-item">
          <span class="icon">📷</span>
          <span>照片视频</span>
        </router-link>
        <router-link to="/diary" class="nav-item">
          <span class="icon">📔</span>
          <span>日记本</span>
        </router-link>
        <router-link to="/profile" class="nav-item">
          <span class="icon">👤</span>
          <span>个人中心</span>
        </router-link>
      </nav>
      
      <div class="sidebar-footer">
        <div class="user-info" @click="showLogout = !showLogout">
          <div class="avatar">{{ userInitial }}</div>
          <span class="username">{{ userName }}</span>
        </div>
        <button v-if="showLogout" class="logout-btn" @click="logout">退出登录</button>
      </div>
    </aside>
    
    <!-- 主内容 -->
    <main class="main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const showLogout = ref(false)

const userName = computed(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return user.name || '用户'
})

const userInitial = computed(() => userName.value.charAt(0).toUpperCase())

const logout = () => {
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('user')
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<style>
.app {
  display: flex;
  min-height: 100vh;
  width: 100%;
}

.sidebar {
  width: 240px;
  min-height: 100vh;
  background: #1a1a2e;
  color: #fff;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.sidebar-header {
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.logo {
  font-size: 28px;
}

.brand {
  font-size: 20px;
  font-weight: 600;
}

.nav {
  flex: 1;
  padding: 16px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  color: rgba(255,255,255,0.7);
  text-decoration: none;
  border-radius: 10px;
  transition: all 0.2s;
}

.nav-item:hover {
  background: rgba(255,255,255,0.1);
  color: #fff;
}

.nav-item.router-link-active {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
}

.nav-item .icon {
  font-size: 20px;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(255,255,255,0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  border-radius: 10px;
  cursor: pointer;
}

.user-info:hover {
  background: rgba(255,255,255,0.1);
}

.avatar {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

.username {
  font-size: 14px;
}

.logout-btn {
  width: 100%;
  margin-top: 12px;
  padding: 10px;
  background: rgba(255,255,255,0.1);
  border: none;
  border-radius: 8px;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
}

.main {
  flex: 1;
  min-height: 100vh;
  background: #f5f6fa;
  overflow-y: auto;
}
</style>