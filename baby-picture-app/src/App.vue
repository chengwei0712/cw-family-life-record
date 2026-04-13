<script setup lang="ts">
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'

onLaunch(() => {
  console.log('App Launch')
})

onShow(() => {
  console.log('App Show')
})

onHide(() => {
  console.log('App Hide')
})
</script>

<style>
/* 全局样式 */
page {
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
}

/* 温馨主题色 */
:root {
  --primary-color: #667eea;
  --primary-gradient: linear-gradient(135deg, #667eea, #764ba2);
  --success-color: #07c160;
  --warning-color: #ff976a;
  --danger-color: #ff4757;
  --text-color: #1a1a1a;
  --text-secondary: #888;
  --border-color: #e5e5e5;
  --bg-color: #f5f5f5;
  --card-bg: #ffffff;
}
</style>
