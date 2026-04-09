<script setup>
defineProps({
  msg: {
    type: String,
    required: true,
  },
})
</script>

<template>
  <div class="greetings">
    <h1 class="green">{{ msg }}</h1>
    <h3>
      You’ve successfully created a project with
      <a href="https://vite.dev/" target="_blank" re<!-- src/App.vue -->
<template>
  <div id="app">
    <PhotoManager msg="Baby Picture Show - Photo Management System" />
  </div>
</template>

<script setup>
import PhotoManager from './components/PhotoManager.vue'
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}
</style><!-- src/components/PhotoManager.vue -->
<template>
  <div class="photo-manager">
    <div class="header">
      <h1>{{ msg }}</h1>
      <div class="controls">
        <input 
          type="file" 
          ref="fileInput" 
          @change="handleFileUpload" 
          accept="image/*" 
          multiple 
          class="file-input"
        />
        <button @click="triggerFileSelect" class="upload-btn">Upload Photos</button>
        <div class="search-box">
          <input 
            v-model="searchQuery" 
            placeholder="Search photos..." 
            class="search-input"
          />
        </div>
      </div>
    </div>

    <div class="photo-grid">
      <div 
        v-for="photo in filteredPhotos" 
        :key="photo.id" 
        class="photo-card"
      >
        <img :src="photo.url" :alt="photo.name" @click="openModal(photo)" />
        <div class="photo-info">
          <h3>{{ photo.name }}</h3>
          <p>{{ formatDate(photo.uploadDate) }}</p>
          <div class="photo-actions">
            <button @click="deletePhoto(photo.id)" class="delete-btn">Delete</button>
          </div>
        </div>
      </div><!-- src/components/PhotoManager.vue -->
<template>
  <div class="photo-manager">
    <div class="header">
      <h1>{{ msg }}</h1>
      <div class="controls">
        <input 
          type="file" 
          ref="fileInput" 
          @change="handleFileUpload" 
          accept="image/*" 
          multiple 
          class="file-input"
        />
        <button @click="triggerFileSelect" class="upload-btn">Upload Photos</button>
        <div class="search-box">
          <input 
            v-model="searchQuery" 
            placeholder="Search photos..." 
            class="search-input"
          />
        </div>
      </div>
    </div>

    <div class="photo-grid">
      <div 
        v-for="photo in filteredPhotos" 
        :key="photo.id" 
        class="photo-card"
      >
        <img :src="photo.url" :alt="photo.name" @click="openModal(photo)" />
        <div class="photo-info">
          <h3>{{ photo.name }}</h3>
          <p>{{ formatDate(photo.uploadDate) }}</p>
          <div class="photo-actions">
            <button @click="deletePhoto(photo.id)" class="delete-btn">Delete</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal for photo preview -->
    <div v-if="selectedPhoto" class="modal" @click="closeModal">
      <div class="modal-content" @click.stop>
        <img :src="selectedPhoto.url" :alt="selectedPhoto.name" />
        <div class="modal-info">
          <h3>{{ selectedPhoto.name }}</h3>
          <p>Uploaded: {{ formatDate(selectedPhoto.uploadDate) }}</p>
        </div>
        <button class="close-btn" @click="closeModal">Close</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// Define props
const props = defineProps({
  msg: {
    type: String,
    required: true
  }
})

// Reactive data
const photos = ref([])
const fileInput = ref(null)
const searchQuery = ref('')
const selectedPhoto = ref(null)

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
  photos.value = photos.value.filter(photo => photo.id !== id)
}

// Open modal to view photo
const openModal = (photo) => {
  selectedPhoto.value = photo
}

// Close modal
const closeModal = () => {
  selectedPhoto.value = null
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
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
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
  background-color: #42b883;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.upload-btn:hover {
  background-color: #359c6d;
}

.search-input {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 200px;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.photo-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.photo-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.photo-card img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  cursor: pointer;
}

.photo-info {
  padding: 15px;
}

.photo-info h3 {
  margin: 0 0 8px 0;
  font-size: 1.1rem;
  color: #333;
}

.photo-info p {
  margin: 0 0 10px 0;
  color: #666;
  font-size: 0.9rem;
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
  font-size: 0.9rem;
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
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  max-width: 80%;
  max-height: 80%;
  text-align: center;
  background: white;
  border-radius: 8px;
  padding: 20px;
  position: relative;
}

.modal-content img {
  max-width: 100%;
  max-height: 70vh;
  object-fit: contain;
  border-radius: 4px;
}

.modal-info {
  margin-top: 15px;
}

.modal-info h3 {
  margin: 10px 0;
}

.close-btn {
  margin-top: 15px;
  padding: 10px 20px;
  background-color: #34495e;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style><!-- src/App.vue -->
<template>
  <div id="app">
    <PhotoManager msg="Baby Picture Show - Photo Management System" />
  </div>
</template>

<script setup>
import PhotoManager from './components/PhotoManager.vue'
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}
</style>
    </div>

    <!-- Modal for photo preview -->
    <div v-if="selectedPhoto" class="modal" @click="closeModal">
      <div class="modal-content" @click.stop>
        <img :src="selectedPhoto.url" :alt="selectedPhoto.name" />
        <div class="modal-info">
          <h3>{{ selectedPhoto.name }}</h3>
          <p>Uploaded: {{ formatDate(selectedPhoto.uploadDate) }}</p>
        </div>
        <button class="close-btn" @click="closeModal">Close</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// Define props
const props = defineProps({
  msg: {
    type: String,
    required: true
  }
})

// Reactive data
const photos = ref([])
const fileInput = ref(null)
const searchQuery = ref('')
const selectedPhoto = ref(null)

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
  photos.value = photos.value.filter(photo => photo.id !== id)
}

// Open modal to view photo
const openModal = (photo) => {
  selectedPhoto.value = photo
}

// Close modal
const closeModal = () => {
  selectedPhoto.value = null
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
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
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
  background-color: #42b883;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.upload-btn:hover {
  background-color: #359c6d;
}

.search-input {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 200px;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.photo-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.photo-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.photo-card img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  cursor: pointer;
}

.photo-info {
  padding: 15px;
}

.photo-info h3 {
  margin: 0 0 8px 0;
  font-size: 1.1rem;
  color: #333;
}

.photo-info p {
  margin: 0 0 10px 0;
  color: #666;
  font-size: 0.9rem;
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
  font-size: 0.9rem;
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
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  max-width: 80%;
  max-height: 80%;
  text-align: center;
  background: white;
  border-radius: 8px;
  padding: 20px;
  position: relative;
}

.modal-content img {
  max-width: 100%;
  max-height: 70vh;
  object-fit: contain;
  border-radius: 4px;
}

.modal-info {
  margin-top: 15px;
}

.modal-info h3 {
  margin: 10px 0;
}

.close-btn {
  margin-top: 15px;
  padding: 10px 20px;
  background-color: #34495e;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>l="noopener">Vite</a> +
      <a href="https://vuejs.org/" target="_blank" rel="noopener">Vue 3</a>.
    </h3>
  </div>
</template>

<style scoped>
h1 {
  font-weight: 500;
  font-size: 2.6rem;
  position: relative;
  top: -10px;
}

h3 {
  font-size: 1.2rem;
}

.greetings h1,
.greetings h3 {
  text-align: center;
}

@media (min-width: 1024px) {
  .greetings h1,
  .greetings h3 {
    text-align: left;
  }
}
</style>
