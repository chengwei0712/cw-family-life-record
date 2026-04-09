<template>
  <router-view />
  
  <!-- 全局Toast提示 -->
  <div v-if="toast.show" :class="['toast-container', toast.type]">
    <span class="toast-icon">{{ toast.type === 'success' ? '✓' : toast.type === 'error' ? '✕' : '⚠' }}</span>
    <span class="toast-msg">{{ toast.msg }}</span>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const toast = ref({
  show: false,
  msg: '',
  type: 'success'
})

let timer = null

const showToast = (e) => {
  const { msg, type = 'success' } = e.detail
  toast.value = { show: true, msg, type }
  
  if (timer) clearTimeout(timer)
  timer = setTimeout(() => {
    toast.value.show = false
  }, 3000)
}

onMounted(() => {
  window.addEventListener('show-toast', showToast)
})

onUnmounted(() => {
  window.removeEventListener('show-toast', showToast)
  if (timer) clearTimeout(timer)
})
</script>

<style>
/* Toast样式 */
.toast-container {
  position: fixed;
  top: 80px;
  left: 50%;
  transform: translateX(-50%);
  padding: 14px 28px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 10px;
  z-index: 9999;
  animation: slideDown 0.3s ease;
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
}

.toast-container.success {
  background: #4caf50;
  color: #fff;
}

.toast-container.error {
  background: #f44336;
  color: #fff;
}

.toast-container.warning {
  background: #ff9800;
  color: #fff;
}

.toast-icon {
  font-size: 18px;
  font-weight: bold;
}

.toast-msg {
  font-size: 15px;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}
</style>