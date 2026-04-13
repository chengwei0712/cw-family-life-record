<template>
  <view class="media-page">
    <!-- 顶部筛选栏 -->
    <view class="filter-bar">
      <view 
        class="filter-item" 
        :class="{ active: currentFilter === 'all' }"
        @click="currentFilter = 'all'"
      >
        全部
      </view>
      <view 
        class="filter-item" 
        :class="{ active: currentFilter === 'photo' }"
        @click="currentFilter = 'photo'"
      >
        照片
      </view>
      <view 
        class="filter-item" 
        :class="{ active: currentFilter === 'video' }"
        @click="currentFilter = 'video'"
      >
        视频
      </view>
      <view class="stats-info">
        <text>{{ total }} 个文件</text>
      </view>
    </view>

    <!-- 上传按钮 -->
    <view class="upload-section">
      <button class="upload-btn" @click="showUploadAction">
        <text class="upload-icon">📤</text>
        <text>上传文件</text>
      </button>
    </view>

    <!-- 上传进度 -->
    <view v-if="uploading" class="upload-progress">
      <view class="progress-bar">
        <view class="progress-fill" :style="{ width: uploadProgress + '%' }"></view>
      </view>
      <text class="progress-text">正在上传 {{ uploadingCount }} 个文件...</text>
    </view>

    <!-- 空状态 -->
    <view v-if="!loading && mediaList.length === 0" class="empty-state">
      <view class="empty-icon">📷</view>
      <text class="empty-title">暂无照片或视频</text>
      <text class="empty-desc">点击上方按钮上传您的第一个文件</text>
    </view>

    <!-- 媒体网格 -->
    <view v-else class="media-grid">
      <view 
        v-for="item in mediaList" 
        :key="item.id"
        class="media-item"
        @click="previewMedia(item)"
      >
        <view class="media-thumb">
          <image 
            v-if="isImage(item.fileType)" 
            :src="item.filePath" 
            mode="aspectFill"
            class="thumb-image"
          />
          <view v-else class="video-thumb">
            <text class="play-icon">▶</text>
          </view>
        </view>
        <view class="media-info">
          <text class="media-name">{{ item.fileName }}</text>
          <text class="media-size">{{ item.fileSize }}</text>
        </view>
        <view class="media-actions" @click.stop>
          <button class="action-btn delete-btn" @click="confirmDelete(item)">
            🗑️
          </button>
        </view>
      </view>
    </view>

    <!-- 加载更多 -->
    <view v-if="loading" class="loading-more">
      <text>加载中...</text>
    </view>
    <view v-if="!loading && hasMore && mediaList.length > 0" class="load-more" @click="loadMore">
      <text>加载更多</text>
    </view>

    <!-- 图片预览 -->
    <view v-if="previewVisible" class="preview-overlay" @click="closePreview">
      <swiper 
        class="preview-swiper" 
        :current="previewIndex"
        @change="onPreviewChange"
      >
        <swiper-item v-for="(item, index) in previewList" :key="index">
          <image 
            v-if="isImage(item.fileType)"
            :src="item.filePath" 
            mode="aspectFit"
            class="preview-image"
            @click.stop
          />
          <video 
            v-else
            :src="item.filePath"
            class="preview-video"
            controls
            @click.stop
          ></video>
        </swiper-item>
      </swiper>
      <view class="preview-info">
        <text class="preview-name">{{ currentPreviewItem?.fileName }}</text>
        <text class="preview-counter">{{ previewIndex + 1 }} / {{ previewList.length }}</text>
      </view>
      <button class="preview-close" @click="closePreview">✕</button>
    </view>


  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onReachBottom } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { 
  getMediaList, 
  uploadMedia, 
  deleteMedia,
  type MediaItem 
} from '@/api/media'

const userStore = useUserStore()

// 状态
const mediaList = ref<MediaItem[]>([])
const currentFilter = ref<'all' | 'photo' | 'video'>('all')
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)
const loading = ref(false)
const uploading = ref(false)
const uploadProgress = ref(0)
const uploadingCount = ref(0)

// 预览相关
const previewVisible = ref(false)
const previewList = ref<MediaItem[]>([])
const previewIndex = ref(0)

// 删除相关
const deletePopupVisible = ref(false)
const deleteTarget = ref<MediaItem | null>(null)

// 计算属性
const hasMore = computed(() => mediaList.value.length < total.value)

const currentPreviewItem = computed(() => previewList.value[previewIndex.value])

// 判断是否是图片
const isImage = (fileType: string) => {
  return fileType && fileType.startsWith('image/')
}

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

// 加载数据
const loadData = async (refresh = false) => {
  if (loading.value) return
  
  if (refresh) {
    page.value = 1
    mediaList.value = []
  }
  
  loading.value = true
  try {
    const res = await getMediaList({
      page: page.value,
      size: pageSize.value,
      type: currentFilter.value === 'all' ? undefined : currentFilter.value
    })
    
    if (res.code === 200 || res.success) {
      if (refresh) {
        mediaList.value = res.data.list
      } else {
        mediaList.value = [...mediaList.value, ...res.data.list]
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

// 筛选变化
const onFilterChange = (filter: 'all' | 'photo' | 'video') => {
  currentFilter.value = filter
  loadData(true)
}

// 显示上传选项
const showUploadAction = () => {
  uni.showActionSheet({
    itemList: ['拍照', '从相册选择', '拍摄视频'],
    success: (res) => {
      if (res.tapIndex === 0) {
        takePhoto()
      } else if (res.tapIndex === 1) {
        chooseImage()
      } else if (res.tapIndex === 2) {
        chooseVideo()
      }
    }
  })
}

// 拍照
const takePhoto = () => {
  uni.chooseImage({
    count: 1,
    sourceType: ['camera'],
    success: (res) => {
      handleFileUpload(res.tempFilePaths)
    }
  })
}

// 从相册选择图片
const chooseImage = () => {
  uni.chooseImage({
    count: 9,
    sourceType: ['album'],
    success: (res) => {
      handleFileUpload(res.tempFilePaths)
    }
  })
}

// 选择视频
const chooseVideo = () => {
  uni.chooseVideo({
    sourceType: ['album', 'camera'],
    maxDuration: 60,
    success: (res) => {
      handleFileUpload([res.tempFilePath])
    }
  })
}

// 处理文件上传
const handleFileUpload = async (filePaths: string[]) => {
  if (!filePaths || filePaths.length === 0) return
  
  uploading.value = true
  uploadingCount.value = filePaths.length
  uploadProgress.value = 0
  
  try {
    let successCount = 0
    const totalFiles = filePaths.length
    
    for (let i = 0; i < filePaths.length; i++) {
      try {
        await uploadMedia(filePaths[i])
        successCount++
        uploadProgress.value = Math.round(((i + 1) / totalFiles) * 100)
      } catch (e) {
        console.error('上传失败', e)
      }
    }
    
    if (successCount > 0) {
      uni.showToast({
        title: `成功上传 ${successCount} 个文件`,
        icon: 'success'
      })
      loadData(true)
    } else {
      uni.showToast({
        title: '上传失败',
        icon: 'none'
      })
    }
  } catch (e) {
    console.error('上传失败', e)
    uni.showToast({
      title: '上传失败',
      icon: 'none'
    })
  } finally {
    uploading.value = false
    uploadProgress.value = 0
  }
}

// 预览媒体
const previewMedia = (item: MediaItem) => {
  // 只预览图片，视频直接播放
  if (isImage(item.fileType)) {
    previewList.value = mediaList.value.filter(m => isImage(m.fileType))
    previewIndex.value = previewList.value.findIndex(m => m.id === item.id)
    previewVisible.value = true
  } else {
    // 视频直接播放
    uni.showToast({
      title: '视频播放功能开发中',
      icon: 'none'
    })
  }
}

// 预览变化
const onPreviewChange = (e: any) => {
  previewIndex.value = e.detail.current
}

// 关闭预览
const closePreview = () => {
  previewVisible.value = false
}

// 确认删除
const confirmDelete = (item: MediaItem) => {
  deleteTarget.value = item
  uni.showModal({
    title: '确认删除',
    content: '删除后无法恢复，确定要删除这个文件吗？',
    confirmColor: '#ff4757',
    success: (res) => {
      if (res.confirm) {
        doDelete()
      }
    }
  })
}

// 执行删除
const doDelete = async () => {
  if (!deleteTarget.value) return
  
  try {
    const res = await deleteMedia(deleteTarget.value.id)
    if (res.code === 200 || res.success) {
      uni.showToast({
        title: '删除成功',
        icon: 'success'
      })
      loadData(true)
    }
  } catch (e) {
    console.error('删除失败', e)
  }
}
</script>

<style scoped>
.media-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 40rpx;
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  align-items: center;
  background: #ffffff;
  padding: 20rpx 30rpx;
  gap: 20rpx;
  position: sticky;
  top: 0;
  z-index: 10;
}

.filter-item {
  padding: 12rpx 32rpx;
  background: #f5f5f5;
  border-radius: 40rpx;
  font-size: 28rpx;
  color: #666;
  transition: all 0.3s;
}

.filter-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
}

.stats-info {
  margin-left: auto;
  font-size: 24rpx;
  color: #999;
}

/* 上传区域 */
.upload-section {
  padding: 30rpx;
}

.upload-btn {
  width: 100%;
  height: 100rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  border: none;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  font-size: 30rpx;
}

.upload-btn::after {
  border: none;
}

.upload-icon {
  font-size: 36rpx;
}

/* 上传进度 */
.upload-progress {
  padding: 0 30rpx 30rpx;
}

.progress-bar {
  width: 100%;
  height: 12rpx;
  background: #e5e5e5;
  border-radius: 6rpx;
  overflow: hidden;
  margin-bottom: 16rpx;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transition: width 0.3s;
}

.progress-text {
  font-size: 26rpx;
  color: #666;
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

/* 媒体网格 */
.media-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 4rpx;
  padding: 0 4rpx;
}

.media-item {
  position: relative;
  background: #ffffff;
  border-radius: 8rpx;
  overflow: hidden;
}

.media-thumb {
  aspect-ratio: 1;
  background: #f5f5f5;
  position: relative;
}

.thumb-image {
  width: 100%;
  height: 100%;
}

.video-thumb {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a1a2e, #2d2d44);
}

.play-icon {
  font-size: 60rpx;
  color: rgba(255, 255, 255, 0.8);
}

.media-info {
  padding: 12rpx;
}

.media-name {
  display: block;
  font-size: 22rpx;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 4rpx;
}

.media-size {
  display: block;
  font-size: 20rpx;
  color: #999;
}

.media-actions {
  position: absolute;
  top: 8rpx;
  right: 8rpx;
}

.action-btn {
  width: 56rpx;
  height: 56rpx;
  background: rgba(0, 0, 0, 0.5);
  border: none;
  border-radius: 50%;
  font-size: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-btn::after {
  border: none;
}

/* 加载更多 */
.loading-more,
.load-more {
  text-align: center;
  padding: 40rpx;
  font-size: 28rpx;
  color: #999;
}

.load-more {
  color: #667eea;
}

/* 预览弹窗 */
.preview-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.95);
  z-index: 1000;
  display: flex;
  flex-direction: column;
}

.preview-swiper {
  flex: 1;
}

.preview-image,
.preview-video {
  width: 100%;
  height: 100%;
}

.preview-info {
  position: absolute;
  bottom: 60rpx;
  left: 0;
  right: 0;
  text-align: center;
  color: #ffffff;
  padding: 0 40rpx;
}

.preview-name {
  display: block;
  font-size: 28rpx;
  margin-bottom: 12rpx;
}

.preview-counter {
  font-size: 24rpx;
  opacity: 0.7;
}

.preview-close {
  position: absolute;
  top: 60rpx;
  right: 40rpx;
  width: 72rpx;
  height: 72rpx;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 50%;
  color: #ffffff;
  font-size: 36rpx;
  z-index: 10;
}

.preview-close::after {
  border: none;
}
</style>
