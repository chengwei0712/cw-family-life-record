<template>
  <div class="page diary-page">
    <header class="page-header">
      <div>
        <h1>📔 日记本</h1>
        <p>记录家庭的每一天</p>
      </div>
      <button class="btn-primary" @click="openWriteModal">+ 写日记</button>
    </header>
    
    <!-- 日记列表 -->
    <div v-if="diaryList.length > 0" class="diary-list">
      <div v-for="diary in diaryList" :key="diary.id" class="diary-card" @click="viewDiary(diary)">
        <div class="diary-date">
          <div class="date-day">{{ formatDay(diary.createTime) }}</div>
          <div class="date-month">{{ formatMonth(diary.createTime) }}</div>
        </div>
        <div class="diary-content">
          <h3 class="diary-title">{{ diary.title || '无标题' }}</h3>
          <p class="diary-text">{{ truncate(diary.content, 150) }}</p>
          <!-- 日记配图 -->
          <div v-if="diary.mediaList && diary.mediaList.length > 0" class="diary-media">
            <div v-for="media in diary.mediaList.slice(0, 3)" :key="media.id" class="media-thumb-small">
              <img v-if="isImage(media.fileType)" :src="media.filePath" />
              <div v-else class="video-icon">▶</div>
            </div>
            <span v-if="diary.mediaList.length > 3" class="more-count">+{{ diary.mediaList.length - 3 }}</span>
          </div>
          <div class="diary-meta">
            <span v-if="diary.mood" class="meta-item">{{ getMoodEmoji(diary.mood) }} {{ diary.mood }}</span>
            <span v-if="diary.weather" class="meta-item">{{ getWeatherEmoji(diary.weather) }} {{ diary.weather }}</span>
          </div>
        </div>
        <div class="diary-actions">
          <button class="action-btn" @click.stop="editDiary(diary)">编辑</button>
          <button class="action-btn danger" @click.stop="confirmDelete(diary.id)">删除</button>
        </div>
      </div>
    </div>
    
    <!-- 空状态 -->
    <div v-else class="empty-state">
      <div class="empty-icon">📔</div>
      <h3>还没有日记</h3>
      <p>点击上方按钮开始记录</p>
    </div>
    
    <!-- 分页 -->
    <div v-if="total > pageSize" class="pagination">
      <button :disabled="page === 1" @click="prevPage">上一页</button>
      <span>{{ page }} / {{ totalPages }}</span>
      <button :disabled="page >= totalPages" @click="nextPage">下一页</button>
    </div>
    
    <!-- 写日记弹窗 -->
    <div v-if="showWriteModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content diary-modal">
        <div class="modal-header">
          <h2>{{ editingDiary ? '编辑日记' : '写日记' }}</h2>
          <button class="close-btn" @click="closeModal">✕</button>
        </div>
        
        <div class="modal-body">
          <div class="form-item">
            <label>标题</label>
            <input v-model="form.title" type="text" placeholder="给日记起个标题" maxlength="100" />
          </div>
          
          <div class="form-row">
            <div class="form-item half">
              <label>心情</label>
              <div class="emoji-selector">
                <button 
                  v-for="mood in moods" 
                  :key="mood.value"
                  type="button"
                  :class="['emoji-btn', { active: form.mood === mood.value }]"
                  @click="form.mood = mood.value"
                  :title="mood.value"
                >
                  {{ mood.emoji }}
                </button>
              </div>
            </div>
            <div class="form-item half">
              <label>天气</label>
              <div class="emoji-selector">
                <button 
                  v-for="weather in weathers" 
                  :key="weather.value"
                  type="button"
                  :class="['emoji-btn', { active: form.weather === weather.value }]"
                  @click="form.weather = weather.value"
                  :title="weather.value"
                >
                  {{ weather.emoji }}
                </button>
              </div>
            </div>
          </div>
          
          <div class="form-item">
            <label>内容</label>
            <textarea v-model="form.content" placeholder="写下今天的故事..." rows="6" required></textarea>
          </div>
          
          <!-- 图片视频上传 -->
          <div class="form-item">
            <label>添加图片/视频</label>
            <div class="media-upload-area">
              <div class="uploaded-media">
                <div v-for="(media, index) in uploadedMedia" :key="index" class="media-preview">
                  <img v-if="isImage(media.fileType)" :src="media.filePath" />
                  <div v-else class="video-preview">
                    <span>▶</span>
                  </div>
                  <button class="remove-btn" @click="removeMedia(index)">✕</button>
                </div>
                <label class="add-media-btn" v-if=" uploadedMedia.length < 9">
                  <span>+</span>
                  <input 
                    type="file" 
                    multiple 
                    accept="image/*,video/*" 
                    @change="handleMediaSelect" 
                    style="display:none"
                  />
                </label>
              </div>
              <p class="upload-tip">支持上传图片和视频，最多9个</p>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button type="button" class="btn-cancel" @click="closeModal">取消</button>
          <button type="button" class="btn-primary" @click="submitDiary" :disabled="submitting">
            {{ submitting ? '保存中...' : '保存日记' }}
          </button>
        </div>
      </div>
    </div>
    
    <!-- 查看日记弹窗 -->
    <div v-if="viewingDiary" class="modal-overlay" @click.self="viewingDiary = null">
      <div class="modal-content diary-view-modal">
        <div class="modal-header">
          <h2>{{ viewingDiary.title || '无标题' }}</h2>
          <button class="close-btn" @click="viewingDiary = null">✕</button>
        </div>
        <div class="view-meta">
          <span>{{ formatDate(viewingDiary.createTime) }}</span>
          <span v-if="viewingDiary.mood">{{ getMoodEmoji(viewingDiary.mood) }} {{ viewingDiary.mood }}</span>
          <span v-if="viewingDiary.weather">{{ getWeatherEmoji(viewingDiary.weather) }} {{ viewingDiary.weather }}</span>
        </div>
        <!-- 查看时的图片视频 -->
        <div v-if="viewingDiary.mediaList && viewingDiary.mediaList.length > 0" class="view-media">
          <div v-for="media in viewingDiary.mediaList" :key="media.id" class="view-media-item" @click="previewMedia(media)">
            <img v-if="isImage(media.fileType)" :src="media.filePath" />
            <video v-else :src="media.filePath"></video>
            <div v-if="!isImage(media.fileType)" class="play-overlay">▶</div>
          </div>
        </div>
        <div class="view-content">
          <p>{{ viewingDiary.content }}</p>
        </div>
      </div>
    </div>
    
    <!-- 确认删除弹窗 -->
    <div v-if="showDeleteConfirm" class="modal-overlay" @click.self="showDeleteConfirm = false">
      <div class="modal-content confirm-modal">
        <div class="confirm-icon">🗑️</div>
        <h3>确认删除</h3>
        <p>删除后无法恢复，确定要删除这篇日记吗？</p>
        <div class="confirm-actions">
          <button class="btn-cancel" @click="showDeleteConfirm = false">取消</button>
          <button class="btn-danger" @click="doDelete">确认删除</button>
        </div>
      </div>
    </div>
    
    <!-- 媒体预览弹窗 -->
    <div v-if="previewingMedia" class="media-preview-modal" @click="previewingMedia = null">
      <button class="close-btn" @click="previewingMedia = null">✕</button>
      <img v-if="isImage(previewingMedia.fileType)" :src="previewingMedia.filePath" />
      <video v-else :src="previewingMedia.filePath" controls autoplay></video>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getDiaryList, createDiary, updateDiary, deleteDiary } from '@/api/diary'
import { uploadMedia, getMediaDetail } from '@/api/media'

const diaryList = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const showWriteModal = ref(false)
const viewingDiary = ref(null)
const editingDiary = ref(null)
const submitting = ref(false)
const showDeleteConfirm = ref(false)
const deleteTargetId = ref(null)
const previewingMedia = ref(null)

const form = ref({
  title: '',
  content: '',
  mood: '',
  weather: ''
})

const uploadedMedia = ref([])

const moods = [
  { value: '开心', emoji: '😊' },
  { value: '平静', emoji: '😌' },
  { value: '兴奋', emoji: '🤩' },
  { value: '感动', emoji: '🥰' },
  { value: '难过', emoji: '😢' },
  { value: '疲惫', emoji: '😴' }
]

const weathers = [
  { value: '晴天', emoji: '☀️' },
  { value: '多云', emoji: '⛅' },
  { value: '阴天', emoji: '☁️' },
  { value: '雨天', emoji: '🌧️' },
  { value: '雪天', emoji: '❄️' }
]

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const isImage = (fileType) => fileType && fileType.startsWith('image/')

const showToast = (msg, type = 'success') => {
  // 发送全局事件
  window.dispatchEvent(new CustomEvent('show-toast', { detail: { msg, type } }))
}

const loadData = async () => {
  try {
    const res = await getDiaryList({ page: page.value, size: pageSize.value })
    if (res.code === 200 || res.success) {
      diaryList.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (e) {
    console.error('加载失败', e)
  }
}

const prevPage = () => {
  if (page.value > 1) {
    page.value--
    loadData()
  }
}

const nextPage = () => {
  if (page.value < totalPages.value) {
    page.value++
    loadData()
  }
}

const openWriteModal = () => {
  editingDiary.value = null
  form.value = { title: '', content: '', mood: '', weather: '' }
  uploadedMedia.value = []
  showWriteModal.value = true
}

const closeModal = () => {
  showWriteModal.value = false
  editingDiary.value = null
}

const editDiary = async (diary) => {
  editingDiary.value = diary
  form.value = {
    title: diary.title || '',
    content: diary.content || '',
    mood: diary.mood || '',
    weather: diary.weather || ''
  }
  // 加载已有媒体
  uploadedMedia.value = diary.mediaList || []
  showWriteModal.value = true
}

const viewDiary = (diary) => {
  viewingDiary.value = diary
}

const handleMediaSelect = async (e) => {
  const files = e.target.files
  if (!files || files.length === 0) return
  
  const remaining = 9 - uploadedMedia.value.length
  const toUpload = Array.from(files).slice(0, remaining)
  
  if (toUpload.length < files.length) {
    showToast('最多只能上传9个文件', 'warning')
  }
  
  const formData = new FormData()
  toUpload.forEach(file => formData.append('files', file))
  
  try {
    const res = await uploadMedia(formData)
    if (res.code === 200 || res.success) {
      const successList = res.data.successList || []
      if (successList.length > 0) {
        uploadedMedia.value.push(...successList)
        showToast(`成功上传 ${successList.length} 个文件`)
      }
      if (res.data.failedCount > 0) {
        showToast('部分文件上传失败', 'warning')
      }
    } else {
      showToast(res.msg || '上传失败', 'error')
    }
  } catch (e) {
    showToast('上传失败', 'error')
  }
  e.target.value = ''
}

const removeMedia = (index) => {
  uploadedMedia.value.splice(index, 1)
}

const submitDiary = async () => {
  if (!form.value.content) {
    showToast('请输入日记内容', 'warning')
    return
  }
  
  submitting.value = true
  
  // 提取媒体ID列表（已经是字符串格式）
  const mediaIds = uploadedMedia.value
    .filter(m => m.id)
    .map(m => m.id)
  
  try {
    const data = {
      title: form.value.title,
      content: form.value.content,
      mood: form.value.mood,
      weather: form.value.weather,
      mediaIds: mediaIds
    }
    
    let res
    if (editingDiary.value) {
      res = await updateDiary(editingDiary.value.id, data)
    } else {
      res = await createDiary(data)
    }
    
    if (res.code === 200 || res.success) {
      showToast(editingDiary.value ? '更新成功' : '保存成功')
      closeModal()
      loadData()
    } else {
      showToast(res.msg || '操作失败', 'error')
    }
  } catch (e) {
    showToast('操作失败', 'error')
  }
  submitting.value = false
}

const confirmDelete = (id) => {
  deleteTargetId.value = id
  showDeleteConfirm.value = true
}

const doDelete = async () => {
  try {
    const res = await deleteDiary(deleteTargetId.value)
    if (res.code === 200 || res.success) {
      showToast('删除成功')
      loadData()
    }
  } catch (e) {
    showToast('删除失败', 'error')
  }
  showDeleteConfirm.value = false
}

const previewMedia = (media) => {
  previewingMedia.value = media
}

// 格式化
const formatDay = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).getDate()
}

const formatMonth = (dateStr) => {
  const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  return months[new Date(dateStr).getMonth()]
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
}

const truncate = (text, len) => {
  if (!text) return ''
  return text.length > len ? text.slice(0, len) + '...' : text
}

const getMoodEmoji = (mood) => moods.find(m => m.value === mood)?.emoji || '😊'
const getWeatherEmoji = (weather) => weathers.find(w => w.value === weather)?.emoji || '☀️'

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.diary-page {
  padding: 32px;
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

.page-header p {
  color: #888;
  font-size: 15px;
}

.btn-primary {
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  cursor: pointer;
}

/* 日记列表 */
.diary-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.diary-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  gap: 20px;
  cursor: pointer;
  transition: all 0.2s;
}

.diary-card:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
}

.diary-date {
  width: 60px;
  text-align: center;
  flex-shrink: 0;
}

.date-day {
  font-size: 32px;
  font-weight: 700;
  color: #667eea;
  line-height: 1;
}

.date-month {
  font-size: 13px;
  color: #888;
  margin-top: 4px;
}

.diary-content {
  flex: 1;
  min-width: 0;
}

.diary-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 8px;
}

.diary-text {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin: 0 0 12px;
}

/* 日记配图 */
.diary-media {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  align-items: center;
}

.media-thumb-small {
  width: 48px;
  height: 48px;
  border-radius: 6px;
  overflow: hidden;
  background: #f5f5f5;
}

.media-thumb-small img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-icon {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #1a1a2e;
  color: #fff;
}

.more-count {
  font-size: 12px;
  color: #888;
}

.diary-meta {
  display: flex;
  gap: 16px;
}

.meta-item {
  font-size: 13px;
  color: #888;
}

.diary-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.action-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  background: #f5f5f5;
  color: #666;
}

.action-btn:hover {
  background: #eee;
}

.action-btn.danger {
  color: #ff4757;
}

/* 空状态 */
.empty-state {
  background: #fff;
  border-radius: 16px;
  padding: 80px;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 20px;
  margin-bottom: 8px;
}

.empty-state p {
  color: #888;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 32px;
  align-items: center;
}

.pagination button {
  padding: 8px 20px;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
}

.pagination button:disabled {
  opacity: 0.5;
}

/* 弹窗 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: #fff;
  border-radius: 16px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
}

.diary-modal {
  max-width: 600px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #eee;
}

.modal-header h2 {
  margin: 0;
  font-size: 20px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #888;
  cursor: pointer;
}

.modal-body {
  padding: 24px;
}

.modal-footer {
  padding: 16px 24px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.form-item {
  margin-bottom: 20px;
}

.form-item label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
}

.form-item input,
.form-item textarea {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #ddd;
  border-radius: 10px;
  font-size: 15px;
}

.form-item input:focus,
.form-item textarea:focus {
  outline: none;
  border-color: #667eea;
}

.form-row {
  display: flex;
  gap: 20px;
}

.form-item.half {
  flex: 1;
}

/* 表情选择器 */
.emoji-selector {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.emoji-btn {
  width: 40px;
  height: 40px;
  border: 2px solid #eee;
  border-radius: 10px;
  background: #fff;
  font-size: 20px;
  cursor: pointer;
}

.emoji-btn.active {
  border-color: #667eea;
  background: #f0f3ff;
}

/* 媒体上传 */
.media-upload-area {
  border: 2px dashed #ddd;
  border-radius: 12px;
  padding: 16px;
}

.uploaded-media {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.media-preview {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  background: #f5f5f5;
}

.media-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-preview {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #1a1a2e;
  color: #fff;
  font-size: 20px;
}

.remove-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 20px;
  height: 20px;
  background: rgba(0,0,0,0.6);
  color: #fff;
  border: none;
  border-radius: 50%;
  font-size: 12px;
  cursor: pointer;
}

.add-media-btn {
  width: 80px;
  height: 80px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #aaa;
  cursor: pointer;
}

.add-media-btn:hover {
  border-color: #667eea;
  color: #667eea;
}

.upload-tip {
  margin-top: 12px;
  font-size: 12px;
  color: #888;
}

.btn-cancel {
  padding: 12px 24px;
  background: #f5f5f5;
  border: none;
  border-radius: 10px;
  cursor: pointer;
}

.btn-danger {
  padding: 12px 24px;
  background: #ff4757;
  color: #fff;
  border: none;
  border-radius: 10px;
  cursor: pointer;
}

/* 查看日记 */
.diary-view-modal {
  max-width: 700px;
}

.view-meta {
  padding: 16px 24px;
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #888;
  border-bottom: 1px solid #eee;
}

.view-media {
  padding: 16px 24px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
}

.view-media-item {
  aspect-ratio: 1;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  position: relative;
}

.view-media-item img,
.view-media-item video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.play-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 50px;
  height: 50px;
  background: rgba(0,0,0,0.6);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
}

.view-content {
  padding: 24px;
}

.view-content p {
  white-space: pre-wrap;
  line-height: 1.8;
}

/* 确认弹窗 */
.confirm-modal {
  max-width: 360px;
  text-align: center;
  padding: 32px;
}

.confirm-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.confirm-modal h3 {
  margin-bottom: 8px;
}

.confirm-modal p {
  color: #888;
  margin-bottom: 24px;
}

.confirm-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

/* 媒体预览 */
.media-preview-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1001;
}

.media-preview-modal .close-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  color: #fff;
  font-size: 28px;
}

.media-preview-modal img,
.media-preview-modal video {
  max-width: 90vw;
  max-height: 90vh;
}
</style>