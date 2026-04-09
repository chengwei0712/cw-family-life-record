<template>
  <div class="dashboard">
    <!-- 欢迎区 -->
    <section class="welcome">
      <h1>欢迎回来，{{ userName }} 👋</h1>
      <p>今天是个记录美好时光的好日子</p>
    </section>
    
    <!-- 统计 - 支持点击跳转 -->
    <section class="stats">
      <div class="stat-card clickable" @click="$router.push('/media')">
        <div class="stat-icon" style="background:#e3f2fd;">📷</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.photoCount }}</div>
          <div class="stat-label">照片</div>
        </div>
      </div>
      <div class="stat-card clickable" @click="$router.push('/media')">
        <div class="stat-icon" style="background:#f3e5f5;">🎬</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.videoCount }}</div>
          <div class="stat-label">视频</div>
        </div>
      </div>
      <div class="stat-card clickable" @click="$router.push('/diary')">
        <div class="stat-icon" style="background:#e8f5e9;">📔</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.diaryCount }}</div>
          <div class="stat-label">日记</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:#fff3e0;">❤️</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.totalMemories }}</div>
          <div class="stat-label">回忆</div>
        </div>
      </div>
    </section>
    
    <!-- 快捷操作 -->
    <section class="section">
      <h2 class="section-title">快捷操作</h2>
      <div class="actions">
        <div class="action-card" @click="$router.push('/media')">
          <div class="action-icon">📷</div>
          <div class="action-text">
            <div class="action-title">上传照片</div>
            <div class="action-desc">记录精彩瞬间</div>
          </div>
          <div class="action-arrow">→</div>
        </div>
        <div class="action-card" @click="$router.push('/diary')">
          <div class="action-icon">✏️</div>
          <div class="action-text">
            <div class="action-title">写日记</div>
            <div class="action-desc">记录今天的心情</div>
          </div>
          <div class="action-arrow">→</div>
        </div>
        <div class="action-card" @click="$router.push('/media')">
          <div class="action-icon">🎬</div>
          <div class="action-text">
            <div class="action-title">上传视频</div>
            <div class="action-desc">保存珍贵影像</div>
          </div>
          <div class="action-arrow">→</div>
        </div>
      </div>
    </section>
    
    <!-- 最近动态 - 点击跳转并定位 -->
    <section class="section">
      <div class="section-header">
        <h2 class="section-title">最近动态</h2>
        <router-link to="/media" class="link-more">查看全部 →</router-link>
      </div>
      <div class="empty" v-if="recentMedia.length === 0">
        <div class="empty-icon">📝</div>
        <p>暂无动态，开始记录您的家庭时光吧</p>
      </div>
      <div v-else class="recent-grid">
        <div 
          v-for="(item, index) in recentMedia" 
          :key="item.id" 
          class="recent-item" 
          @click="goToMedia(item, index)"
        >
          <img v-if="isImage(item.fileType)" :src="item.filePath" :alt="item.fileName" />
          <div v-else class="video-placeholder">
            <span>▶</span>
          </div>
          <!-- 标记高亮 -->
          <div v-if="highlightId === item.id" class="highlight-badge">当前查看</div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getMediaList } from '@/api/media'
import { getTotalStats } from '@/api/stats'

const router = useRouter()
const route = useRoute()

const stats = ref({
  photoCount: 0,
  videoCount: 0,
  diaryCount: 0,
  totalMemories: 0
})

const recentMedia = ref([])
const highlightId = ref(null)

const userName = computed(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return user.name || '用户'
})

const isImage = (fileType) => {
  return fileType && fileType.startsWith('image/')
}

const loadStats = async () => {
  try {
    const res = await getTotalStats()
    if (res.code === 200 || res.success) {
      stats.value = res.data
    }
  } catch (e) {
    console.error('加载统计失败', e)
  }
}

const loadRecent = async () => {
  try {
    const res = await getMediaList({ page: 1, size: 4 })
    if (res.code === 200 || res.success) {
      recentMedia.value = res.data.list || []
    }
  } catch (e) {
    console.error('加载最近动态失败', e)
  }
}

// 点击最近动态跳转，并传递高亮ID
const goToMedia = (item, index) => {
  // 跳转到媒体页面，并传递要高亮的ID
  router.push({
    path: '/media',
    query: { highlight: item.id }
  })
}

onMounted(() => {
  loadStats()
  loadRecent()
  
  // 从路由获取高亮ID（保持字符串格式，避免大数字精度问题）
  if (route.query.highlight) {
    highlightId.value = route.query.highlight
  }
})
</script>

<style>
.dashboard {
  padding: 32px;
  max-width: 1200px;
  margin: 0 auto;
}

.welcome {
  margin-bottom: 32px;
}

.welcome h1 {
  font-size: 28px;
  color: #1a1a1a;
  margin-bottom: 8px;
}

.welcome p {
  color: #888;
  font-size: 16px;
}

.stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.stat-card.clickable {
  cursor: pointer;
  transition: all 0.2s;
}

.stat-card.clickable:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a1a;
}

.stat-label {
  font-size: 14px;
  color: #888;
}

.section {
  margin-bottom: 32px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 18px;
  color: #1a1a1a;
  margin: 0;
  font-weight: 600;
}

.link-more {
  font-size: 14px;
  color: #667eea;
  text-decoration: none;
}

.link-more:hover {
  text-decoration: underline;
}

.actions {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.action-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.action-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
}

.action-icon {
  font-size: 36px;
}

.action-text {
  flex: 1;
}

.action-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 4px;
}

.action-desc {
  font-size: 13px;
  color: #888;
}

.action-arrow {
  font-size: 20px;
  color: #ccc;
}

.empty {
  background: #fff;
  border-radius: 12px;
  padding: 60px;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty p {
  color: #888;
  font-size: 15px;
}

/* 最近动态网格 */
.recent-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.recent-item {
  aspect-ratio: 1;
  border-radius: 12px;
  overflow: hidden;
  background: #f5f5f5;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.recent-item:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}

.recent-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a1a2e, #2d2d44);
  color: rgba(255,255,255,0.8);
  font-size: 32px;
}

.highlight-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  background: #667eea;
  color: #fff;
  font-size: 11px;
  padding: 4px 8px;
  border-radius: 4px;
}

@media (max-width: 900px) {
  .stats { grid-template-columns: repeat(2, 1fr); }
  .actions { grid-template-columns: 1fr; }
  .recent-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 600px) {
  .stats { grid-template-columns: 1fr; }
  .dashboard { padding: 20px; }
  .recent-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>