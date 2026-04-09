import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// 只引入全局重置样式
import './assets/warm.css'

const app = createApp(App)
app.use(router)
app.mount('#app')