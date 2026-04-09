<template>
  <div class="photo-manager">
    <div class="main-container">
      <!-- 左侧图片列表菜单 -->
      <div class="sidebar">
        <h2>Photos List</h2>
        <div class="search-box">
          <input 
            v-model="searchQuery" 
            placeholder="Search photos..." 
            class="search-input"
          />
        </div>
        <div class="photos-list">
          <div 
            v-for="(photo, index) in filteredPhotos" 
            :key="photo.id" 
            class="photo-item"
            :class="{ active: selectedPhoto && selectedPhoto.id === photo.id }"
            @click="selectPhoto(photo)"
          >
            <div class="photo-preview">
              <img :src="photo.url" :alt="photo.name" />
            </div>
            <div class="photo-details">
              <div class="photo-name">{{ photo.name }}</div>
              <div class="photo-date">{{ formatDate(photo.uploadDate) }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 主内容区域 -->
      <div class="main-content">
        <div class="header">
          <h1>{{ title }}</h1>
          <div class="controls">
            <input 
              type="file" 
              ref="fileInput" 
              @change="handleFileUpload" 
              accept="image/*" 
              multiple 
              class="file-input"
            />
            <button @click="triggerFileSelect" class="upload-btn">
              <span>+</span> Upload Photos
            </button>
          </div>
        </div>

        <!-- 轮播图区域 -->
        <div v-if="photos.length > 0" class="carousel-container">
          <div class="carousel">
            <div class="carousel-track" :style="{ transform: `translateX(-${currentSlide * 100}%)` }">
              <div 
                v-for="(photo, index) in photos" 
                :key="`carousel-${index}`" 
                class="carousel-slide"
              >
                <img :src="photo.url" :alt="photo.name" />
                <div class="carousel-label">{{ photo.name }}</div>
              </div>
            </div>
            
            <!-- 轮播控制 -->
            <button class="carousel-btn prev" @click="prevSlide" v-if="photos.length > 1">
              &#10094;
            </button>
            <button class="carousel-btn next" @click="nextSlide" v-if="photos.length > 1">
              &#10095;
            </button>
            
            <!-- 轮播指示器 -->
            <div class="carousel-indicators" v-if="photos.length > 1">
              <span 
                v-for="(photo, index) in photos" 
                :key="`indicator-${index}`" 
                :class="{ active: currentSlide === index }"
                @click="goToSlide(index)"
                class="indicator"
              ></span>
            </div>
          </div>
        </div>

        <div v-if="photos.length === 0" class="empty-state">
          <h2>No photos yet</h2>
          <p>Upload your first photo to get started!</p>
        </div>

        <div class="photo-grid">
          <div 
            v-for="photo in filteredPhotos" 
            :key="photo.id" 
            class="photo-card"
          >
            <div class="image-container">
              <img :src="photo.url" :alt="photo.name" @click="openModal(photo)" />
            </div>
            <div class="photo-info">
              <h3>{{ photo.name }}</h3>
              <p class="date">{{ formatDate(photo.uploadDate) }}</p>
              <div class="photo-actions">
                <button @click="deletePhoto(photo.id)" class="delete-btn">Delete</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal for photo preview -->
    <div v-if="modalPhoto" class="modal" @click="closeModal">
      <div class="modal-content" @click.stop>
        <img :src="modalPhoto.url" :alt="modalPhoto.name" />
        <div class="modal-info">
          <h3>{{ modalPhoto.name }}</h3>
          <p>Uploaded: {{ formatDate(modalPhoto.uploadDate) }}</p>
        </div>
        <button class="close-btn" @click="closeModal">Close</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

// Define props
const props = defineProps({
  title: {
    type: String,
    default: 'Baby Picture Show - Photo Management System'
  }
})

// Reactive data
const photos = ref([])
const fileInput = ref(null)
const searchQuery = ref('')
const modalPhoto = ref(null)
const selectedPhoto = ref(null)
const currentSlide = ref(0)
const autoSlideInterval = ref(null)

// Load photos from localStorage on component mount
onMounted(() => {
  const savedPhotos = localStorage.getItem('babyPhotos')
  if (savedPhotos) {
    photos.value = JSON.parse(savedPhotos).map(photo => ({
      ...photo,
      uploadDate: new Date(photo.uploadDate) // Convert back to Date object
    }))
  }
  
  // Start auto slide for carousel
  startAutoSlide()
})

// Cleanup interval on component unmount
onUnmounted(() => {
  if (autoSlideInterval.value) {
    clearInterval(autoSlideInterval.value)
  }
})

// Start auto sliding for carousel
const startAutoSlide = () => {
  if (autoSlideInterval.value) {
    clearInterval(autoSlideInterval.value)
  }
  
  autoSlideInterval.value = setInterval(() => {
    nextSlide()
  }, 5000) // Change slide every 5 seconds
}

// Computed property for filtered photos based on search
const filteredPhotos = computed(() => {
  if (!searchQuery.value) return photos.value
  return photos.value.filter(photo => 
    photo.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

// Handle file upload
const handleFileUpload = (event) => {
  const files = Array.from(event.target.files)
  
  files.forEach(file => {
    if (file.type.startsWith('image/')) {
      const reader = new FileReader()
      reader.onload = (e) => {
        const photo = {
          id: Date.now() + Math.random(),
          name: file.name,
          url: e.target.result,
          uploadDate: new Date(),
          size: file.size
        }
        photos.value.unshift(photo) // Add to beginning of array
        
        // Save to localStorage
        localStorage.setItem('babyPhotos', JSON.stringify(photos.value))
        
        // Reset auto slide
        startAutoSlide()
      }
      reader.readAsDataURL(file)
    }
  })
  
  // Reset file input
  event.target.value = ''
}

// Trigger file selection
const triggerFileSelect = () => {
  fileInput.value.click()
}

// Delete photo
const deletePhoto = (id) => {
  if (confirm('Are you sure you want to delete this photo?')) {
    // Remove from photos array
    const index = photos.value.findIndex(photo => photo.id === id)
    if (index !== -1) {
      photos.value.splice(index, 1)
      
      // Update localStorage
      localStorage.setItem('babyPhotos', JSON.stringify(photos.value))
      
      // Adjust current slide if needed
      if (currentSlide.value >= photos.value.length && photos.value.length > 0) {
        currentSlide.value = photos.value.length - 1
      } else if (photos.value.length === 0) {
        currentSlide.value = 0
      }
      
      // Reset auto slide
      startAutoSlide()
    }
  }
}

// Select photo from sidebar
const selectPhoto = (photo) => {
  selectedPhoto.value = photo
  modalPhoto.value = photo
}

// Open modal to view photo
const openModal = (photo) => {
  modalPhoto.value = photo
  selectedPhoto.value = photo
}

// Close modal
const closeModal = () => {
  modalPhoto.value = null
}

// Carousel functions
const nextSlide = () => {
  currentSlide.value = (currentSlide.value + 1) % photos.value.length
}

const prevSlide = () => {
  currentSlide.value = (currentSlide.value - 1 + photos.value.length) % photos.value.length
}

const goToSlide = (index) => {
  currentSlide.value = index
}

// Format date for display
const formatDate = (date) => {
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
.photo-manager {
  max-width: 100%;
  height: 100vh;
  display: flex;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.main-container {
  display: flex;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.sidebar {
  width: 300px;
  background-color: #f8f9fa;
  border-right: 1px solid #dee2e6;
  display: flex;
  flex-direction: column;
  padding: 20px;
  overflow-y: auto;
}

.sidebar h2 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #2c3e50;
  text-align: center;
}

.search-input {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 100%;
  margin-bottom: 20px;
  font-size: 1rem;
}

.photos-list {
  overflow-y: auto;
  flex-grow: 1;
}

.photo-item {
  display: flex;
  align-items: center;
  padding: 10px;
  border-radius: 6px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.photo-item:hover {
  background-color: #e9ecef;
}

.photo-item.active {
  background-color: #d1f0e0;
}

.photo-preview {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  overflow: hidden;
  margin-right: 12px;
  flex-shrink: 0;
}

.photo-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.photo-details {
  flex-grow: 1;
  overflow: hidden;
}

.photo-name {
  font-weight: 500;
  font-size: 0.9rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #2c3e50;
}

.photo-date {
  font-size: 0.75rem;
  color: #6c757d;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
  overflow-y: auto;
  background-color: #ffffff;
}

.header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 25px;
  gap: 15px;
}

@media (min-width: 768px) {
  .header {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }
}

.header h1 {
  margin: 0;
  font-size: 1.8rem;
  color: #2c3e50;
  text-align: center;
}

.controls {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: center;
}

.file-input {
  display: none;
}

.upload-btn {
  background: linear-gradient(135deg, #42d392, #2ecc71);
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

.upload-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 8px rgba(0,0,0,0.15);
}

.carousel-container {
  margin: 20px 0 30px;
}

.carousel {
  position: relative;
  overflow: hidden;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.15);
  max-width: 100%;
  margin: 0 auto;
}

.carousel-track {
  display: flex;
  transition: transform 0.5s ease-in-out;
}

.carousel-slide {
  min-width: 100%;
  position: relative;
}

.carousel-slide img {
  width: 100%;
  display: block;
  max-height: 400px;
  object-fit: cover;
}

.carousel-label {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0,0,0,0.7));
  color: white;
  padding: 40px 20px 15px;
  font-size: 1.2rem;
  text-align: center;
}

.carousel-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(255,255,255,0.7);
  border: none;
  font-size: 24px;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.3s;
}

.carousel-btn:hover {
  background: rgba(255,255,255,0.9);
}

.prev {
  left: 20px;
}

.next {
  right: 20px;
}

.carousel-indicators {
  position: absolute;
  bottom: 15px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
}

.indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255,255,255,0.5);
  cursor: pointer;
  transition: background 0.3s;
}

.indicator.active {
  background: white;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #7f8c8d;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.empty-state h2 {
  font-size: 1.8rem;
  margin-bottom: 10px;
  color: #34495e;
}

.image-container {
  overflow: hidden;
  border-radius: 8px 8px 0 0;
  height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f8f9fa;
}

.image-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.image-container img:hover {
  transform: scale(1.05);
}

.photo-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  background: white;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.photo-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.photo-info {
  padding: 15px;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.photo-info h3 {
  margin: 0 0 8px 0;
  font-size: 1rem;
  color: #2c3e50;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.date {
  margin: 0 0 12px 0;
  color: #7f8c8d;
  font-size: 0.8rem;
  flex-grow: 1;
}

.photo-actions {
  display: flex;
  justify-content: flex-end;
}

.delete-btn {
  background-color: #e74c3c;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: background-color 0.2s;
}

.delete-btn:hover {
  background-color: #c0392b;
}

/* Modal styles */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.9);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  max-width: 90%;
  max-height: 90%;
  text-align: center;
  background: white;
  border-radius: 8px;
  padding: 20px;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.modal-content img {
  max-width: 100%;
  max-height: 70vh;
  object-fit: contain;
  border-radius: 4px;
  margin-bottom: 15px;
}

.modal-info {
  width: 100%;
  text-align: center;
}

.modal-info h3 {
  margin: 10px 0;
  color: #2c3e50;
}

.close-btn {
  margin-top: 15px;
  padding: 10px 25px;
  background-color: #34495e;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.2s;
}

.close-btn:hover {
  background-color: #2c3e50;
}
</style>