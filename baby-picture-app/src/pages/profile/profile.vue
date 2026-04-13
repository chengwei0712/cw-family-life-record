<template>
  <view class="profile-page">
    <!-- 用户信息卡片 -->
    <view class="profile-card">
      <view class="avatar-section">
        <view class="avatar">
          <text class="avatar-text">{{ userInitial }}</text>
        </view>
        <view class="user-info">
          <text class="user-name">{{ userStore.userInfo?.name || '未登录' }}</text>
          <text class="user-email">{{ userStore.userInfo?.email || '未设置邮箱' }}</text>
        </view>
      </view>
    </view>

    <!-- 统计卡片 -->
    <view class="stats-card">
      <view class="stat-item">
        <text class="stat-number">{{ mediaStats.photoCount + mediaStats.videoCount }}</text>
        <text class="stat-label">照片视频</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <text class="stat-number">{{ diaryStats.diaryCount }}</text>
        <text class="stat-label">日记</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <text class="stat-number">{{ loginDays }}</text>
        <text class="stat-label">记录天数</text>
      </view>
    </view>

    <!-- 菜单列表 -->
    <view class="menu-list">
      <view class="menu-section">
        <view class="menu-item" @click="navigateTo('/pages/media/media')">
          <text class="menu-icon">📷</text>
          <text class="menu-title">我的照片</text>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="navigateTo('/pages/diary/diary')">
          <text class="menu-icon">📔</text>
          <text class="menu-title">我的日记</text>
          <text class="menu-arrow">›</text>
        </view>
      </view>

      <view class="menu-section">
        <view class="menu-item" @click="showAbout">
          <text class="menu-icon">ℹ️</text>
          <text class="menu-title">关于</text>
          <text class="menu-arrow">›</text>
        </view>
      </view>

      <view class="menu-section">
        <view class="menu-item logout-item" @click="handleLogout">
          <text class="menu-icon">🚪</text>
          <text class="menu-title">退出登录</text>
        </view>
      </view>
    </view>

    <!-- 版本信息 -->
    <view class="version-info">
      <text>家庭记忆 v1.0.0</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getMediaStats } from '@/api/media'
import { getDiaryStats } from '@/api/diary'

const userStore = useUserStore()

// 统计数据
const mediaStats = ref({ photoCount: 0, videoCount: 0, total: 0 })
const diaryStats = ref({ diaryCount: 0 })
const loginDays = ref(1)

// 用户首字母
const userInitial = computed(() => {
  const name = userStore.userInfo?.name
  if (!name) return '?'
  return name.charAt(0).toUpperCase()
})

// 初始化
onMounted(() => {
  userStore.initFromStorage()
  if (!userStore.isLoggedIn) {
    uni.reLaunch({
      url: '/pages/login/login'
    })
    return
  }
  loadStats()
})

// 加载统计数据
const loadStats = async () => {
  try {
    const [mediaRes, diaryRes] = await Promise.all([
      getMediaStats(),
      getDiaryStats()
    ])
    
    if (mediaRes.code === 200 || mediaRes.success) {
      mediaStats.value = mediaRes.data
    }
    
    if (diaryRes.code === 200 || diaryRes.success) {
      diaryStats.value = diaryRes.data
    }
  } catch (e) {
    console.error('加载统计数据失败', e)
  }
}

// 页面跳转
const navigateTo = (url: string) => {
  uni.switchTab({ url })
}

// 显示关于
const showAbout = () => {
  uni.showModal({
    title: '关于家庭记忆',
    content: '家庭记忆 v1.0.0\n\n记录每一个美好瞬间，珍藏家庭的珍贵回忆。',
    showCancel: false
  })
}

// 退出登录
const handleLogout = () => {
  uni.showModal({
    title: '确认退出',
    content: '确定要退出登录吗？',
    success: async (res) => {
      if (res.confirm) {
        await userStore.logout()
        uni.reLaunch({
          url: '/pages/login/login'
        })
      }
    }
  })
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 40rpx;
}

/* 用户卡片 */
.profile-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60rpx 40rpx 40rpx;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 4rpx solid rgba(255, 255, 255, 0.5);
}

.avatar-text {
  font-size: 48rpx;
  font-weight: bold;
  color: #ffffff;
}

.user-info {
  flex: 1;
}

.user-name {
  display: block;
  font-size: 36rpx;
  font-weight: 500;
  color: #ffffff;
  margin-bottom: 8rpx;
}

.user-email {
  display: block;
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
}

/* 统计卡片 */
.stats-card {
  margin: -30rpx 30rpx 30rpx;
  background: #ffffff;
  border-radius: 20rpx;
  padding: 40rpx;
  display: flex;
  align-items: center;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.08);
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 48rpx;
  font-weight: bold;
  color: #667eea;
  line-height: 1;
  margin-bottom: 12rpx;
}

.stat-label {
  display: block;
  font-size: 24rpx;
  color: #999;
}

.stat-divider {
  width: 1rpx;
  height: 60rpx;
  background: #e5e5e5;
}

/* 菜单列表 */
.menu-list {
  padding: 0 20rpx;
}

.menu-section {
  background: #ffffff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f5f5f5;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-icon {
  font-size: 36rpx;
  margin-right: 20rpx;
}

.menu-title {
  flex: 1;
  font-size: 30rpx;
  color: #333;
}

.menu-arrow {
  font-size: 40rpx;
  color: #ccc;
  font-weight: 300;
}

.logout-item {
  justify-content: center;
}

.logout-item .menu-title {
  flex: none;
  color: #ff4757;
  text-align: center;
}

.logout-item .menu-icon {
  margin-right: 12rpx;
}

/* 版本信息 */
.version-info {
  text-align: center;
  padding: 40rpx;
  color: #ccc;
  font-size: 24rpx;
}
</style>
