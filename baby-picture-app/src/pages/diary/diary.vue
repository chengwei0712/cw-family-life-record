<template>
  <view class="diary-page">
    <!-- 顶部操作栏 -->
    <view class="page-header">
      <view class="header-title">
        <text class="title">📔 日记本</text>
        <text class="subtitle">记录家庭的每一天</text>
      </view>
      <button class="write-btn" @click="openWriteModal">
        <text class="write-icon">✏️</text>
        <text>写日记</text>
      </button>
    </view>

    <!-- 日记列表 -->
    <view v-if="!loading && diaryList.length > 0" class="diary-list">
      <view 
        v-for="diary in diaryList" 
        :key="diary.id"
        class="diary-card"
        @click="viewDiary(diary)"
      >
        <view class="diary-date">
          <text class="date-day">{{ formatDay(diary.createTime) }}</text>
          <text class="date-month">{{ formatMonth(diary.createTime) }}</text>
        </view>
        <view class="diary-content">
          <text class="diary-title">{{ diary.title || '无标题' }}</text>
          <text class="diary-text">{{ truncate(diary.content, 100) }}</text>
          
          <!-- 日记配图 -->
          <view v-if="diary.mediaList && diary.mediaList.length > 0" class="diary-media">
            <view 
              v-for="media in diary.mediaList.slice(0, 3)" 
              :key="media.id" 
              class="media-thumb-small"
              @click.stop
            >
              <image 
                v-if="isImage(media.fileType)" 
                :src="media.filePath" 
                mode="aspectFill"
                class="thumb-image-small"
              />
              <view v-else class="video-icon-small">▶</view>
            </view>
            <text v-if="diary.mediaList.length > 3" class="more-count">
              +{{ diary.mediaList.length - 3 }}
            </text>
          </view>
          
          <view class="diary-meta">
            <text v-if="diary.mood" class="meta-item">
              {{ getMoodEmoji(diary.mood) }} {{ diary.mood }}
            </text>
            <text v-if="diary.weather" class="meta-item">
              {{ getWeatherEmoji(diary.weather) }} {{ diary.weather }}
            </text>
          </view>
        </view>
        <view class="diary-actions" @click.stop>
          <button class="action-btn edit-btn" @click="editDiary(diary)">编辑</button>
          <button class="action-btn delete-btn" @click="confirmDelete(diary)">删除</button>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="!loading && diaryList.length === 0" class="empty-state">
      <view class="empty-icon">📔</view>
      <text class="empty-title">还没有日记</text>
      <text class="empty-desc">点击上方按钮开始记录</text>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-state">
      <text>加载中...</text>
    </view>

    <!-- 加载更多 -->
    <view v-if="!loading && hasMore && diaryList.length > 0" class="load-more" @click="loadMore">
      <text>加载更多</text>
    </view>

    <!-- 写日记弹窗 -->
    <view v-if="showWriteModal" class="modal-overlay" @click.self="closeWriteModal">
      <view class="modal-content">
        <view class="modal-header">
          <text class="modal-title">{{ editingDiary ? '编辑日记' : '写日记' }}</text>
          <button class="close-btn" @click="closeWriteModal">✕</button>
        </view>

        <scroll-view class="modal-body" scroll-y>
          <!-- 标题 -->
          <view class="form-item">
            <text class="form-label">标题</text>
            <input 
              class="form-input" 
              v-model="form.title" 
              type="text" 
              placeholder="给日记起个标题"
              maxlength="100"
            />
          </view>

          <!-- 心情和天气 -->
          <view class="form-row">
            <view class="form-item half">
              <text class="form-label">心情</text>
              <scroll-view class="emoji-scroll" scroll-x>
                <view class="emoji-selector">
                  <view 
                    v-for="mood in moods" 
                    :key="mood.value"
                    class="emoji-item"
                    :class="{ active: form.mood === mood.value }"
                    @click="form.mood = form.mood === mood.value ? '' : mood.value"
                  >
                    <text class="emoji">{{ mood.emoji }}</text>
                  </view>
                </view>
              </scroll-view>
            </view>
            <view class="form-item half">
              <text class="form-label">天气</text>
              <scroll-view class="emoji-scroll" scroll-x>
                <view class="emoji-selector">
                  <view 
                    v-for="weather in weathers" 
                    :key="weather.value"
                    class="emoji-item"
                    :class="{ active: form.weather === weather.value }"
                    @click="form.weather = form.weather === weather.value ? '' : weather.value"
                  >
                    <text class="emoji">{{ weather.emoji }}</text>
                  </view>
                </view>
              </scroll-view>
            </view>
          </view>

          <!-- 内容 -->
          <view class="form-item">
            <text class="form-label">内容</text>
            <textarea 
              class="form-textarea" 
              v-model="form.content" 
              placeholder="记录今天发生的事情..."
              :maxlength="5000"
              :auto-height="true"
              :show-confirm-bar="false"
            />
          </view>

          <!-- 配图 -->
          <view class="form-item">
            <text class="form-label">配图 (可选)</text>
            <view class="media-grid-small">
              <view 
                v-for="media in selectedMedia" 
                :key="media.id"
                class="media-item-small"
              >
                <image 
                  v-if="isImage(media.fileType)" 
                  :src="media.filePath" 
                  mode="aspectFill"
                  class="media-image-small"
                />
                <view v-else class="media-video-small">
                  <text class="play-icon-small">▶</text>
                </view>
                <button class="remove-media-btn" @click="removeMedia(media)">✕</button>
              </view>
              <view class="add-media-btn" @click="openMediaPicker">
                <text class="add-icon">+</text>
                <text class="add-text">添加</text>
              </view>
            </view>
          </view>
        </scroll-view>

        <view class="modal-footer">
          <button class="btn-cancel" @click="closeWriteModal">取消</button>
          <button class="btn-primary" :disabled="saving" @click="saveDiary">
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </view>
      </view>
    </view>

    <!-- 查看日记详情弹窗 -->
    <view v-if="showDetailModal" class="modal-overlay" @click.self="closeDetailModal">
      <view class="modal-content detail-modal">
        <view class="modal-header">
          <text class="modal-title">{{ detailDiary?.title || '无标题' }}</text>
          <button class="close-btn" @click="closeDetailModal">✕</button>
        </view>

        <scroll-view class="modal-body" scroll-y>
          <!-- 日期和元信息 -->
          <view class="detail-header">
            <text class="detail-date">{{ formatFullDate(detailDiary?.createTime) }}</text>
            <view class="detail-meta">
              <text v-if="detailDiary?.mood" class="meta-item">
                {{ getMoodEmoji(detailDiary.mood) }} {{ detailDiary.mood }}
              </text>
              <text v-if="detailDiary?.weather" class="meta-item">
                {{ getWeatherEmoji(detailDiary.weather) }} {{ detailDiary.weather }}
              </text>
            </view>
          </view>

          <!-- 内容 -->
          <text class="detail-content">{{ detailDiary?.content }}</text>

          <!-- 配图 -->
          <view v-if="detailDiary?.mediaList && detailDiary.mediaList.length > 0" class="detail-media">
            <text class="media-label">配图</text>
            <view class="media-grid-detail">
              <view 
                v-for="media in detailDiary.mediaList" 
                :key="media.id"
                class="media-item-detail"
                @click="previewDetailMedia(media)"
              >
                <image 
                  v-if="isImage(media.fileType)" 
                  :src="media.filePath" 
                  mode="aspectFill"
                  class="media-image-detail"
                />
                <view v-else class="media-video-detail">
                  <text class="play-icon-detail">▶</text>
                </view>
              </view>
            </view>
          </view>
        </scroll-view>

        <view class="modal-footer">
          <button class="btn-cancel" @click="closeDetailModal">关闭</button>
          <button class="btn-primary" @click="editFromDetail">编辑</button>
        </view>
      </view>
    </view>

    <!-- 媒体选择弹窗 -->
    <view v-if="showMediaPicker" class="modal-overlay" @click.self="closeMediaPicker">
      <view class="modal-content media-picker-modal">
        <view class="modal-header">
          <text class="modal-title">选择配图</text>
          <button class="close-btn" @click="closeMediaPicker">✕</button>
        </view>

        <scroll-view class="modal-body" scroll-y>
          <view class="media-picker-grid">
            <view 
              v-for="media in availableMedia" 
              :key="media.id"
              class="picker-media-item"
              :class="{ selected: isMediaSelected(media) }"
              @click="toggleMediaSelection(media)"
            >
              <image 
                v-if="isImage(media.fileType)" 
                :src="media.filePath" 
                mode="aspectFill"
                class="picker-image"
              />
              <view v-else class="picker-video">
                <text class="picker-play">▶</text>
              </view>
              <view v-if="isMediaSelected(media)" class="selected-check">✓</view>
            </view>
          </view>
          
          <view v-if="availableMedia.length === 0" class="empty-media">
            <text>暂无可选媒体，请先上传照片或视频</text>
          </view>
        </scroll-view>

        <view class="modal-footer">
          <button class="btn-cancel" @click="closeMediaPicker">取消</button>
          <button class="btn-primary" @click="confirmMediaSelection">确定 ({{ tempSelectedMedia.length }})</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onReachBottom } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import {
  getDiaryList,
  createDiary,
  updateDiary,
  deleteDiary,
  MOOD_OPTIONS,
  WEATHER_OPTIONS,
  type DiaryItem
} from '@/api/diary'
import { getMediaList, type MediaItem } from '@/api/media'

const userStore = useUserStore()

// 状态
const diaryList = ref<DiaryItem[]>([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

// 写日记弹窗
const showWriteModal = ref(false)
const editingDiary = ref<DiaryItem | null>(null)
const saving = ref(false)
const form = ref({
  title: '',
  content: '',
  mood: '',
  weather: '',
  mediaIds: [] as string[]
})
const selectedMedia = ref<MediaItem[]>([])

// 详情弹窗
const showDetailModal = ref(false)
const detailDiary = ref<DiaryItem | null>(null)

// 媒体选择器
const showMediaPicker = ref(false)
const availableMedia = ref<MediaItem[]>([])
const tempSelectedMedia = ref<MediaItem[]>([])

// 选项
const moods = MOOD_OPTIONS
const weathers = WEATHER_OPTIONS

// 计算属性
const hasMore = computed(() => diaryList.value.length < total.value)

// 初始化
onMounted(() => {
  userStore.initFromStorage()
  if (!userStore.isLoggedIn) {
    uni.reLaunch({
      url: '/pages/login/login'
    })
    return
  }
  loadData(true)
})

// 加载日记列表
const loadData = async (refresh = false) => {
  if (loading.value) return
  
  if (refresh) {
    page.value = 1
    diaryList.value = []
  }
  
  loading.value = true
  try {
    const res = await getDiaryList({
      page: page.value,
      size: pageSize.value
    })
    
    if (res.code === 200 || res.success) {
      if (refresh) {
        diaryList.value = res.data.list
      } else {
        diaryList.value = [...diaryList.value, ...res.data.list]
      }
      total.value = res.data.total
    }
  } catch (e) {
    console.error('加载失败', e)
  } finally {
    loading.value = false
  }
}

// 加载更多
const loadMore = () => {
  if (!hasMore.value || loading.value) return
  page.value++
  loadData(false)
}

// 触底加载更多
onReachBottom(() => {
  loadMore()
})

// 格式化日期
const formatDay = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.getDate().toString().padStart(2, '0')
}

const formatMonth = (dateStr: string) => {
  const date = new Date(dateStr)
  const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  return months[date.getMonth()]
}

const formatFullDate = (dateStr?: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
}

// 截断文本
const truncate = (text: string, maxLength: number) => {
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.slice(0, maxLength) + '...'
}

// 获取心情emoji
const getMoodEmoji = (mood: string) => {
  const item = moods.find(m => m.value === mood)
  return item ? item.emoji : '😊'
}

// 获取天气emoji
const getWeatherEmoji = (weather: string) => {
  const item = weathers.find(w => w.value === weather)
  return item ? item.emoji : '☀️'
}

// 判断是否是图片
const isImage = (fileType: string) => {
  return fileType && fileType.startsWith('image/')
}

// 打开写日记弹窗
const openWriteModal = () => {
  editingDiary.value = null
  form.value = {
    title: '',
    content: '',
    mood: '',
    weather: '',
    mediaIds: []
  }
  selectedMedia.value = []
  showWriteModal.value = true
}

// 编辑日记
const editDiary = (diary: DiaryItem) => {
  editingDiary.value = diary
  form.value = {
    title: diary.title || '',
    content: diary.content || '',
    mood: diary.mood || '',
    weather: diary.weather || '',
    mediaIds: diary.mediaList?.map(m => m.id) || []
  }
  selectedMedia.value = diary.mediaList ? [...diary.mediaList] : []
  showWriteModal.value = true
}

// 从详情页编辑
const editFromDetail = () => {
  if (detailDiary.value) {
    closeDetailModal()
    editDiary(detailDiary.value)
  }
}

// 关闭写日记弹窗
const closeWriteModal = () => {
  showWriteModal.value = false
  editingDiary.value = null
}

// 保存日记
const saveDiary = async () => {
  if (!form.value.content.trim()) {
    uni.showToast({ title: '请输入日记内容', icon: 'none' })
    return
  }

  saving.value = true
  try {
    if (editingDiary.value) {
      // 更新
      await updateDiary(editingDiary.value.id, {
        ...form.value,
        mediaIds: selectedMedia.value.map(m => m.id)
      })
      uni.showToast({ title: '更新成功', icon: 'success' })
    } else {
      // 创建
      await createDiary({
        ...form.value,
        mediaIds: selectedMedia.value.map(m => m.id)
      })
      uni.showToast({ title: '创建成功', icon: 'success' })
    }
    
    closeWriteModal()
    loadData(true)
  } catch (e) {
    console.error('保存失败', e)
  } finally {
    saving.value = false
  }
}

// 查看日记详情
const viewDiary = (diary: DiaryItem) => {
  detailDiary.value = diary
  showDetailModal.value = true
}

// 关闭详情弹窗
const closeDetailModal = () => {
  showDetailModal.value = false
  detailDiary.value = null
}

// 确认删除
const confirmDelete = (diary: DiaryItem) => {
  uni.showModal({
    title: '确认删除',
    content: '删除后无法恢复，确定要删除这篇日记吗？',
    confirmColor: '#ff4757',
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteDiary(diary.id)
          uni.showToast({ title: '删除成功', icon: 'success' })
          loadData(true)
        } catch (e) {
          console.error('删除失败', e)
        }
      }
    }
  })
}

// 打开媒体选择器
const openMediaPicker = async () => {
  try {
    const res = await getMediaList({ page: 1, size: 50 })
    if (res.code === 200 || res.success) {
      availableMedia.value = res.data.list
    }
  } catch (e) {
    console.error('加载媒体失败', e)
  }
  
  tempSelectedMedia.value = [...selectedMedia.value]
  showMediaPicker.value = true
}

// 关闭媒体选择器
const closeMediaPicker = () => {
  showMediaPicker.value = false
  tempSelectedMedia.value = []
}

// 判断媒体是否已选中
const isMediaSelected = (media: MediaItem) => {
  return tempSelectedMedia.value.some(m => m.id === media.id)
}

// 切换媒体选中状态
const toggleMediaSelection = (media: MediaItem) => {
  const index = tempSelectedMedia.value.findIndex(m => m.id === media.id)
  if (index > -1) {
    tempSelectedMedia.value.splice(index, 1)
  } else {
    tempSelectedMedia.value.push(media)
  }
}

// 确认媒体选择
const confirmMediaSelection = () => {
  selectedMedia.value = [...tempSelectedMedia.value]
  closeMediaPicker()
}

// 移除已选媒体
const removeMedia = (media: MediaItem) => {
  const index = selectedMedia.value.findIndex(m => m.id === media.id)
  if (index > -1) {
    selectedMedia.value.splice(index, 1)
  }
}

// 预览详情页的媒体
const previewDetailMedia = (media: MediaItem) => {
  if (isImage(media.fileType)) {
    uni.previewImage({
      urls: [media.filePath],
      current: 0
    })
  }
}
</script>

<style scoped>
.diary-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 40rpx;
}

/* 顶部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #ffffff;
  padding: 30rpx;
  position: sticky;
  top: 0;
  z-index: 10;
}

.header-title {
  flex: 1;
}

.title {
  display: block;
  font-size: 40rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 8rpx;
}

.subtitle {
  display: block;
  font-size: 26rpx;
  color: #999;
}

.write-btn {
  padding: 16rpx 32rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  border: none;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 28rpx;
}

.write-btn::after {
  border: none;
}

.write-icon {
  font-size: 32rpx;
}

/* 日记列表 */
.diary-list {
  padding: 20rpx;
}

.diary-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  display: flex;
  gap: 20rpx;
}

.diary-date {
  width: 100rpx;
  flex-shrink: 0;
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12rpx;
  padding: 16rpx 0;
}

.date-day {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  color: #ffffff;
  line-height: 1;
}

.date-month {
  display: block;
  font-size: 20rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 8rpx;
}

.diary-content {
  flex: 1;
  min-width: 0;
}

.diary-title {
  display: block;
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 12rpx;
}

.diary-text {
  display: block;
  font-size: 26rpx;
  color: #666;
  line-height: 1.6;
  margin-bottom: 16rpx;
}

.diary-media {
  display: flex;
  gap: 8rpx;
  margin-bottom: 16rpx;
}

.media-thumb-small {
  width: 100rpx;
  height: 100rpx;
  border-radius: 8rpx;
  overflow: hidden;
  background: #f5f5f5;
  position: relative;
}

.thumb-image-small {
  width: 100%;
  height: 100%;
}

.video-icon-small {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a1a2e, #2d2d44);
  font-size: 32rpx;
  color: rgba(255, 255, 255, 0.8);
}

.more-count {
  width: 100rpx;
  height: 100rpx;
  border-radius: 8rpx;
  background: #e5e5e5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  color: #666;
}

.diary-meta {
  display: flex;
  gap: 16rpx;
}

.meta-item {
  font-size: 24rpx;
  color: #999;
}

.diary-actions {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.action-btn {
  padding: 8rpx 16rpx;
  background: #f5f5f5;
  border: none;
  border-radius: 8rpx;
  font-size: 24rpx;
  color: #666;
}

.action-btn::after {
  border: none;
}

.edit-btn {
  background: #f0f3ff;
  color: #667eea;
}

.delete-btn {
  background: #fff0f0;
  color: #ff4757;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 160rpx 60rpx;
}

.empty-icon {
  font-size: 160rpx;
  margin-bottom: 40rpx;
}

.empty-title {
  display: block;
  font-size: 32rpx;
  color: #333;
  margin-bottom: 16rpx;
  font-weight: 500;
}

.empty-desc {
  display: block;
  font-size: 28rpx;
  color: #999;
}

/* 加载状态 */
.loading-state,
.load-more {
  text-align: center;
  padding: 40rpx;
  font-size: 28rpx;
  color: #999;
}

.load-more {
  color: #667eea;
}

/* 弹窗 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.modal-content {
  width: 100%;
  max-height: 90vh;
  background: #ffffff;
  border-radius: 32rpx 32rpx 0 0;
  display: flex;
  flex-direction: column;
}

.detail-modal,
.media-picker-modal {
  max-height: 85vh;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #e5e5e5;
  flex-shrink: 0;
}

.modal-title {
  font-size: 32rpx;
  font-weight: 500;
  color: #333;
}

.close-btn {
  width: 60rpx;
  height: 60rpx;
  background: #f5f5f5;
  border: none;
  border-radius: 50%;
  font-size: 32rpx;
  color: #666;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn::after {
  border: none;
}

.modal-body {
  flex: 1;
  padding: 30rpx;
  overflow-y: auto;
}

.modal-footer {
  display: flex;
  gap: 20rpx;
  padding: 30rpx;
  border-top: 1rpx solid #e5e5e5;
  flex-shrink: 0;
}

.btn-cancel {
  flex: 1;
  height: 88rpx;
  background: #f5f5f5;
  color: #666;
  border: none;
  border-radius: 16rpx;
  font-size: 30rpx;
}

.btn-cancel::after {
  border: none;
}

.btn-primary {
  flex: 1;
  height: 88rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  border: none;
  border-radius: 16rpx;
  font-size: 30rpx;
}

.btn-primary::after {
  border: none;
}

.btn-primary[disabled] {
  opacity: 0.6;
}

/* 表单 */
.form-item {
  margin-bottom: 32rpx;
}

.form-row {
  display: flex;
  gap: 20rpx;
}

.form-row .form-item {
  flex: 1;
}

.form-label {
  display: block;
  font-size: 28rpx;
  color: #333;
  margin-bottom: 16rpx;
  font-weight: 500;
}

.form-input {
  width: 100%;
  height: 88rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  color: #333;
  box-sizing: border-box;
}

.form-textarea {
  width: 100%;
  min-height: 300rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  padding: 20rpx 24rpx;
  font-size: 28rpx;
  color: #333;
  line-height: 1.6;
  box-sizing: border-box;
}

/* Emoji 选择器 */
.emoji-scroll {
  width: 100%;
}

.emoji-selector {
  display: flex;
  gap: 12rpx;
  padding: 4rpx 0;
}

.emoji-item {
  width: 80rpx;
  height: 80rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border: 3rpx solid transparent;
}

.emoji-item.active {
  background: #f0f3ff;
  border-color: #667eea;
}

.emoji {
  font-size: 40rpx;
}

/* 媒体网格 */
.media-grid-small {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.media-item-small {
  width: 160rpx;
  height: 160rpx;
  border-radius: 12rpx;
  overflow: hidden;
  position: relative;
  background: #f5f5f5;
}

.media-image-small {
  width: 100%;
  height: 100%;
}

.media-video-small {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a1a2e, #2d2d44);
}

.play-icon-small {
  font-size: 48rpx;
  color: rgba(255, 255, 255, 0.8);
}

.remove-media-btn {
  position: absolute;
  top: 8rpx;
  right: 8rpx;
  width: 44rpx;
  height: 44rpx;
  background: rgba(0, 0, 0, 0.6);
  border: none;
  border-radius: 50%;
  color: #ffffff;
  font-size: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.remove-media-btn::after {
  border: none;
}

.add-media-btn {
  width: 160rpx;
  height: 160rpx;
  border: 2rpx dashed #ddd;
  border-radius: 12rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  background: #fafafa;
}

.add-icon {
  font-size: 48rpx;
  color: #999;
}

.add-text {
  font-size: 24rpx;
  color: #999;
}

/* 详情页 */
.detail-header {
  margin-bottom: 32rpx;
  padding-bottom: 24rpx;
  border-bottom: 1rpx solid #e5e5e5;
}

.detail-date {
  display: block;
  font-size: 28rpx;
  color: #666;
  margin-bottom: 16rpx;
}

.detail-meta {
  display: flex;
  gap: 16rpx;
}

.detail-content {
  display: block;
  font-size: 30rpx;
  color: #333;
  line-height: 1.8;
  white-space: pre-wrap;
}

.detail-media {
  margin-top: 40rpx;
}

.media-label {
  display: block;
  font-size: 28rpx;
  color: #333;
  margin-bottom: 20rpx;
  font-weight: 500;
}

.media-grid-detail {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.media-item-detail {
  width: 200rpx;
  height: 200rpx;
  border-radius: 12rpx;
  overflow: hidden;
  background: #f5f5f5;
}

.media-image-detail {
  width: 100%;
  height: 100%;
}

.media-video-detail {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a1a2e, #2d2d44);
}

.play-icon-detail {
  font-size: 56rpx;
  color: rgba(255, 255, 255, 0.8);
}

/* 媒体选择器 */
.media-picker-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8rpx;
}

.picker-media-item {
  aspect-ratio: 1;
  border-radius: 8rpx;
  overflow: hidden;
  position: relative;
  background: #f5f5f5;
}

.picker-image {
  width: 100%;
  height: 100%;
}

.picker-video {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a1a2e, #2d2d44);
}

.picker-play {
  font-size: 48rpx;
  color: rgba(255, 255, 255, 0.8);
}

.selected-check {
  position: absolute;
  top: 8rpx;
  right: 8rpx;
  width: 44rpx;
  height: 44rpx;
  background: #667eea;
  border-radius: 50%;
  color: #ffffff;
  font-size: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-media {
  text-align: center;
  padding: 80rpx 40rpx;
  color: #999;
  font-size: 28rpx;
}
</style>
