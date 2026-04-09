<template>
  <div class="media-page">
    <header class="page-header">
      <div>
        <h1>📷 照片视频</h1>
        <p>管理和浏览您的家庭照片与视频</p>
      </div>
      <div class="header-actions">
        <label class="btn-primary">
          + 上传文件
          <input
              type="file"
              multiple
              accept="image/*,video/*"
              @change="handleFileSelect"
              style="display:none"
          >
        </label>
      </div>
    </header>
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filter-tabs">
        <button
            :class="['filter-tab', { active: filter === 'all' }]"
            @click="changeFilter('all')"
        >
          全部
        </button>
        <button
            :class="['filter-tab', { active: filter === 'photo' }]"
            @click="changeFilter('photo')"
        >
          照片
        </button>
        <button
            :class="['filter-tab', { active: filter === 'video' }]"
            @click="changeFilter('video')"
        >
          视频
        </button>
      </div>
      <span class="count">共 {{ total }} 个文件</span>
    </div>
    <!-- 拖拽上传区域 -->
    <div
        v-if="mediaList.length === 0"
        class="drop-zone"
        :class="{ 'drag-over': isDragging }"
        @dragover.prevent="isDragging = true"
        @dragleave.prevent="isDragging = false"
        @drop.prevent="handleDrop"
        @click="triggerUpload"
    >
      <div class="drop-icon">📤</div>
      <h3>拖拽文件到此处上传</h3>
      <p>支持 JPG、PNG、GIF、MP4、MOV 等格式</p>
      <p class="drop-hint">或点击选择文件</p>
      <input
          ref="fileInput"
          type="file"
          multiple
          accept="image/*,video/*"
          @change="handleFileSelect"
          style="display:none"
      >
    </div>
    <!-- 有文件时的小型拖拽区 -->
    <div
        v-else
        class="drop-zone-small"
        :class="{ 'drag-over': isDragging }"
        @dragover.prevent="isDragging = true"
        @dragleave.prevent="isDragging = false"
        @drop.prevent="handleDrop"
    >
      <span v-if="isDragging">松开鼠标上传文件</span>
      <span v-else>拖拽文件到此处上传更多</span>
    </div>
    <!-- 上传进度 -->
    <div v-if="uploading" class="upload-progress">
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: uploadProgress + '%' }"></div>
      </div>
      <span>正在上传 {{ uploadingCount }} 个文件...</span>
    </div>
    <!-- 媒体网格 -->
    <div v-if="mediaList.length > 0" class="media-grid">
      <div
          v-for="item in mediaList"
          :key="item.id"
          :ref="el => { if (item.id === highlightId) highlightRef = el }"
          :class="['media-card', { highlight: item.id === highlightId }]"
      >
        <div class="media-thumb" @click="preview(item)">
          <img v-if="isImage(item.fileType)" :src="item.filePath" :alt="item.fileName">
          <div v-else class="video-thumb">
            <span class="play-icon">▶</span>
          </div>
          <div v-if="item.id === highlightId" class="highlight-tag">当前查看</div>
        </div>
        <div class="media-info">
          <div class="media-name" :title="item.fileName">{{ item.fileName }}</div>
          <div class="media-meta">{{ item.fileSize }}</div>
        </div>
        <div class="media-actions">
          <button class="action-btn preview-btn" @click="preview(item)">👁 预览</button>
          <button class="action-btn delete-btn" @click="confirmDelete(item.id)">🗑 删除</button>
        </div>
      </div>
    </div>
    <!-- 分页 -->
    <div v-if="total > pageSize" class="pagination">
      <button :disabled="page === 1" @click="prevPage">上一页</button>
      <span>{{ page }} / {{ totalPages }}</span>
      <button :disabled="page >= totalPages" @click="nextPage">下一页</button>
    </div>
    <!-- 预览弹窗优化 -->
    <div v-if="previewItem" class="preview-modal">
      <!-- 左侧点击热区：上一张 -->
      <div
          v-if="isImage(previewItem.fileType)"
          :class="['preview-side', 'preview-side-left', { 'disabled': currentIndex === 0 }]"
          @click.stop="prevPreview"
      >
        <button class="preview-arrow-btn" :disabled="currentIndex === 0">&larr;</button>
      </div>
      <!-- 中间内容展示区 -->
      <div class="preview-content" @click.stop>
        <button class="close-btn" @click="previewItem = null">&times;</button>
        <!-- 改用纯CSS动画代替Transition组件，规避编译解析报错 -->
        <img
            v-if="isImage(previewItem.fileType)"
            :src="previewItem.filePath"
            :key="previewItem.id"
            class="preview-media fade-enter-active"
        >
        <video
            v-else
            :src="previewItem.filePath"
            controls
            autoplay
            class="preview-media"
            :key="previewItem.id"
        ></video>
        <div class="preview-info">
          <h3>{{ previewItem.fileName }}</h3>
          <p>{{ previewItem.fileSize }}</p>
        </div>
      </div>
      <!-- 右侧点击热区：下一张 -->
      <div
          v-if="isImage(previewItem.fileType)"
          :class="['preview-side', 'preview-side-right', { 'disabled': currentIndex === imageList.length - 1 }]"
          @click.stop="nextPreview"
      >
        <button class="preview-arrow-btn" :disabled="currentIndex === imageList.length - 1">&rarr;</button>
      </div>
    </div>
    <!-- 删除确认弹窗 -->
    <div v-if="showDeleteConfirm" class="modal-overlay" @click.self="showDeleteConfirm = false">
      <div class="confirm-modal">
        <div class="confirm-icon">🗑️</div>
        <h3>确认删除</h3>
        <p>删除后无法恢复，确定要删除这个文件吗？</p>
        <div class="confirm-actions">
          <button class="btn-cancel" @click="showDeleteConfirm = false">取消</button>
          <button class="btn-danger" @click="doDelete">确认删除</button>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted, nextTick, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getMediaList, uploadMedia, deleteMedia } from '@/api/media'
const route = useRoute()
const router = useRouter()
const mediaList = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)
const filter = ref('all')
const uploading = ref(false)
const uploadProgress = ref(0)
const uploadingCount = ref(0)
const previewItem = ref(null)
const isDragging = ref(false)
const fileInput = ref(null)
const highlightId = ref(null)
const highlightRef = ref(null)
const showDeleteConfirm = ref(false)
const deleteTargetId = ref(null)
const currentIndex = ref(0)
const imageList = computed(() => mediaList.value.filter(item => isImage(item.fileType)))
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))
const isImage = (fileType) => fileType && fileType.startsWith('image/')
const showToast = (msg, type = 'success') => {
  window.dispatchEvent(new CustomEvent('show-toast', { detail: { msg, type } }))
}
const loadData = async () => {
  try {
    const res = await getMediaList({
      page: page.value,
      size: pageSize.value,
      type: filter.value === 'all' ? null : filter.value
    })
    if (res.code === 200 || res.success) {
      mediaList.value = res.data.list || []
      total.value = res.data.total || 0
      if (highlightId.value) {
        await nextTick()
        scrollToHighlight()
      }
    }
  } catch (e) {
    console.error('加载失败', e)
  }
}
const scrollToHighlight = () => {
  if (highlightRef.value) {
    highlightRef.value.scrollIntoView({ behavior: 'smooth', block: 'center' })
    setTimeout(() => {
      highlightId.value = null
      router.replace({ query: {} })
    }, 3000)
  }
}
const changeFilter = (type) => {
  filter.value = type
  page.value = 1
  highlightId.value = null
  loadData()
}
const prevPage = () => {
  if (page.value > 1) { page.value--; loadData() }
}
const nextPage = () => {
  if (page.value < totalPages.value) { page.value++; loadData() }
}
const triggerUpload = () => fileInput.value?.click()
const handleFileSelect = (e) => {
  const files = e.target.files
  if (files && files.length > 0) doUpload(files)
  e.target.value = ''
}
const handleDrop = (e) => {
  isDragging.value = false
  const files = e.dataTransfer.files
  if (files && files.length > 0) doUpload(files)
}
const doUpload = async (files) => {
  uploading.value = true
  uploadingCount.value = files.length
  const formData = new FormData()
  for (let file of files) formData.append('files', file)
  try {
    const res = await uploadMedia(formData)
    if (res.code === 200 || res.success) {
      if (res.data.failedCount > 0) {
        showToast('部分文件上传失败', 'warning')
      } else {
        showToast(`成功上传 ${res.data.successCount} 个文件`)
      }
      loadData()
    } else {
      showToast(res.msg || '上传失败', 'error')
    }
  } catch (e) {
    showToast('上传失败', 'error')
  } finally {
    uploading.value = false
  }
}
const confirmDelete = (id) => {
  deleteTargetId.value = id
  showDeleteConfirm.value = true
}
const doDelete = async () => {
  try {
    const res = await deleteMedia(deleteTargetId.value)
    if (res.code === 200 || res.success) {
      showToast('删除成功')
      loadData()
    } else {
      showToast(res.msg || '删除失败', 'error')
    }
  } catch (e) {
    showToast('删除失败', 'error')
  }
  showDeleteConfirm.value = false
}
const preview = (item) => {
  previewItem.value = item
  if (isImage(item.fileType)) {
    currentIndex.value = imageList.value.findIndex(img => img.id === item.id)
  }
}
const prevPreview = () => {
  if (currentIndex.value <= 0) return
  currentIndex.value--
  previewItem.value = imageList.value[currentIndex.value]
}
const nextPreview = () => {
  if (currentIndex.value >= imageList.value.length - 1) return
  currentIndex.value++
  previewItem.value = imageList.value[currentIndex.value]
}
const handleKeydown = (e) => {
  if (!previewItem.value) return
  if (e.key === 'Escape') {
    previewItem.value = null
    return
  }
  if (!isImage(previewItem.value.fileType)) return
  if (e.key === 'ArrowLeft') prevPreview()
  if (e.key === 'ArrowRight') nextPreview()
}
onMounted(() => {
  window.addEventListener('keydown', handleKeydown)
  if (route.query.highlight) highlightId.value = route.query.highlight
  loadData()
})
onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
})
</script>
<style>
.media-page {
  padding: 32px;
  width: 100%;
  min-height: 100%;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}
.page-header h1 {
  font-size: 28px;
  color: #1a1a1a;
  margin-bottom: 8px;
}
.page-header p { color: #888; font-size: 15px; }
.btn-primary {
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  cursor: pointer;
}
.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.filter-tabs { display: flex; gap: 8px; }
.filter-tab {
  padding: 8px 20px;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 20px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
}
.filter-tab.active {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  border-color: transparent;
}
.count { color: #888; font-size: 14px; }
.drop-zone {
  background: #fff;
  border: 2px dashed #ddd;
  border-radius: 16px;
  padding: 60px 40px;
  text-align: center;
  cursor: pointer;
  margin-bottom: 24px;
  transition: all 0.3s;
}
.drop-zone:hover { border-color: #667eea; background: #f8f9ff; }
.drop-zone.drag-over { border-color: #667eea; background: #f0f3ff; }
.drop-icon { font-size: 64px; margin-bottom: 16px; }
.drop-zone h3 { font-size: 20px; color: #1a1a1a; margin-bottom: 8px; }
.drop-zone p { color: #888; font-size: 14px; margin-bottom: 4px; }
.drop-hint { color: #667eea !important; font-weight: 500; }
.drop-zone-small {
  background: #fff;
  border: 2px dashed #ddd;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  color: #888;
  font-size: 14px;
  margin-bottom: 24px;
}
.drop-zone-small.drag-over { border-color: #667eea; background: #f0f3ff; color: #667eea; }
.upload-progress {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
}
.progress-bar { flex: 1; height: 8px; background: #eee; border-radius: 4px; overflow: hidden; }
.progress-fill { height: 100%; background: linear-gradient(135deg, #667eea, #764ba2); }
.media-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
  width: 100%;
}
.media-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  border: 2px solid transparent;
}
.media-card.highlight {
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.2);
}
.media-thumb {
  aspect-ratio: 1;
  background: #f5f5f5;
  overflow: hidden;
  cursor: pointer;
  position: relative;
}
.media-thumb img { width: 100%; height: 100%; object-fit: cover; }
.video-thumb {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a1a2e, #2d2d44);
}
.play-icon { font-size: 40px; color: rgba(255,255,255,0.8); }
.highlight-tag {
  position: absolute;
  top: 8px;
  left: 8px;
  background: #667eea;
  color: #fff;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 4px;
}
.media-info { padding: 12px; }
.media-name { font-size: 13px; color: #1a1a1a; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.media-meta { font-size: 12px; color: #888; margin-top: 4px; }
.media-actions { display: flex; gap: 8px; padding: 0 12px 12px; }
.action-btn {
  flex: 1;
  padding: 8px 12px;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}
.preview-btn { background: #f0f3ff; color: #667eea; }
.delete-btn { background: #fff0f0; color: #ff4757; }
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 32px;
}
.pagination button {
  padding: 8px 20px;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
}
.pagination button:disabled { opacity: 0.5; }
/* ====== 预览弹窗改造 ====== */
.preview-modal {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.preview-side {
  flex: 1;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.3s ease;
}
.preview-side-left:hover {
  background: linear-gradient(to right, rgba(255,255,255,0.1), transparent);
}
.preview-side-right:hover {
  background: linear-gradient(to left, rgba(255,255,255,0.1), transparent);
}
.preview-side.disabled {
  cursor: not-allowed;
}
.preview-side.disabled:hover {
  background: transparent;
}
.preview-arrow-btn {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: rgba(255,255,255,0.15);
  color: #fff;
  font-size: 22px;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  opacity: 0;
}
.preview-side:hover .preview-arrow-btn {
  opacity: 1;
  background: rgba(255,255,255,0.3);
}
.preview-arrow-btn:disabled {
  opacity: 0.2 !important;
  cursor: not-allowed;
  background: transparent;
}
.preview-content {
  flex: 0 0 auto;
  max-width: 70vw;
  max-height: 90vh;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.preview-media {
  max-width: 100%;
  max-height: 80vh;
  border-radius: 8px;
  object-fit: contain;
}
.close-btn {
  position: absolute;
  top: -40px;
  right: 0;
  background: none;
  border: none;
  color: #fff;
  font-size: 28px;
  cursor: pointer;
  z-index: 20;
}
.preview-info {
  text-align: center;
  color: #fff;
  margin-top: 16px;
}
/* 替代 Transition 的纯 CSS 淡入动画 */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
.fade-enter-active {
  animation: fadeIn 0.3s ease;
}
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.confirm-modal {
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  text-align: center;
  max-width: 360px;
}
.confirm-icon { font-size: 48px; margin-bottom: 16px; }
.confirm-modal h3 { margin-bottom: 8px; }
.confirm-modal p { color: #888; margin-bottom: 24px; }
.confirm-actions { display: flex; gap: 12px; justify-content: center; }
.btn-cancel { padding: 12px 24px; background: #f5f5f5; border: none; border-radius: 10px; cursor: pointer; }
.btn-danger { padding: 12px 24px; background: #ff4757; color: #fff; border: none; border-radius: 10px; cursor: pointer; }
@media (max-width: 768px) {
  .media-page { padding: 20px; }
  .media-grid { grid-template-columns: repeat(2, 1fr); gap: 12px; }
  .preview-content {
    max-width: 90vw;
  }
  .preview-arrow-btn {
    opacity: 0.8;
    width: 40px;
    height: 40px;
    font-size: 18px;
  }
}
</style>