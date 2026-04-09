<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1>Baby Picture Show</h1>
        <p class="subtitle">欢迎使用宝宝照片管理系统</p>
      </div>
      
      <!-- Tab切换 -->
      <div class="tabs">
        <div 
          class="tab" 
          :class="{ active: activeTab === 'account' }"
          @click="activeTab = 'account'"
        >
          <span>账号登录</span>
        </div>
        <div 
          class="tab" 
          :class="{ active: activeTab === 'phone' }"
          @click="activeTab = 'phone'"
        >
          <span>手机登录</span>
        </div>
      </div>
      
      <!-- 账号密码登录 -->
      <form v-if="activeTab === 'account'" @submit.prevent="handleAccountLogin" class="login-form">
        <div class="input-group">
          <label for="username">账号</label>
          <input 
            id="username"
            type="text" 
            v-model="accountForm.username" 
            placeholder="请输入用户名/邮箱"
            required
          />
        </div>
        
        <div class="input-group">
          <label for="password">密码</label>
          <input 
            id="password"
            type="password" 
            v-model="accountForm.password" 
            placeholder="请输入密码"
            required
          />
        </div>
        
        <div class="form-options">
          <label class="remember">
            <input type="checkbox" v-model="accountForm.remember">
            <span>记住我</span>
          </label>
          <a href="#" class="forgot">忘记密码?</a>
        </div>
        
        <button type="submit" class="login-btn" :disabled="isSubmitting">
          {{ isSubmitting ? '登录中...' : '登录' }}
        </button>
      </form>
      
      <!-- 手机号登录 -->
      <form v-if="activeTab === 'phone'" @submit.prevent="handlePhoneLogin" class="login-form">
        <div class="input-group">
          <label for="phone">手机号</label>
          <input 
            id="phone"
            type="tel" 
            v-model="phoneForm.phoneNumber" 
            placeholder="请输入手机号"
            required
          />
        </div>
        
        <div class="input-group">
          <label for="code">验证码</label>
          <div class="verification-input">
            <input 
              id="code"
              type="text" 
              v-model="phoneForm.code" 
              placeholder="请输入验证码"
              required
            />
            <button 
              type="button" 
              :disabled="countdown > 0 || isSubmitting"
              @click="sendVerificationCode"
              class="code-btn"
            >
              {{ countdown > 0 ? `${countdown}s后重发` : '获取验证码' }}
            </button>
          </div>
        </div>
        
        <button type="submit" class="login-btn" :disabled="isSubmitting">
          {{ isSubmitting ? '登录中...' : '登录' }}
        </button>
      </form>
      
      <!-- 社交登录 -->
      <div class="social-login">
        <div class="divider">
          <span>其他登录方式</span>
        </div>
        <div class="social-options">
          <button class="social-btn wechat" @click="showQRCode">
            <svg class="icon" viewBox="0 0 24 24" width="18" height="18">
              <path fill="currentColor" d="M19.5,9a.5.5,0,0,0-.5.5v1a.5.5,0,0,0,.5.5h1a.5.5,0,0,0,.5-.5v-1a.5.5,0,0,0-.5-.5ZM17.5,10a.5.5,0,0,0,.5.5h1a.5.5,0,0,0,.5-.5v-1a.5.5,0,0,0-.5-.5h-1a.5.5,0,0,0-.5.5ZM15,10a.5.5,0,0,0,.5.5h1a.5.5,0,0,0,.5-.5v-1a.5.5,0,0,0-.5-.5h-1a.5.5,0,0,0-.5.5ZM12.5,10a.5.5,0,0,0,.5.5h1a.5.5,0,0,0,.5-.5v-1a.5.5,0,0,0-.5-.5h-1a.5.5,0,0,0-.5.5ZM10,10a.5.5,0,0,0,.5.5h1a.5.5,0,0,0,.5-.5v-1a.5.5,0,0,0-.5-.5h-1a.5.5,0,0,0-.5.5ZM7.5,10a.5.5,0,0,0,.5.5h1a.5.5,0,0,0,.5-.5v-1a.5.5,0,0,0-.5-.5h-1a.5.5,0,0,0-.5.5Z"/>
            </svg>
            微信
          </button>
        </div>
      </div>
      
      <!-- 注册链接 -->
      <div class="register-link">
        <p>还没有账号? <a href="#" @click.prevent="showRegister">立即注册</a></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import axios from 'axios'

// 当前激活的标签页
const activeTab = ref('account')

// 提交状态
const isSubmitting = ref(false)

// 账号登录表单数据
const accountForm = reactive({
  username: '',
  password: '',
  remember: false
})

// 手机登录表单数据
const phoneForm = reactive({
  phoneNumber: '',
  code: ''
})

// 验证码倒计时
const countdown = ref(0)

// 显示二维码登录
const showQRCode = () => {
  alert('微信扫码登录功能将在后续版本中实现');
}

// 显示注册页面
const showRegister = () => {
  alert('注册功能将在后续版本中实现');
}

// 发送验证码
const sendVerificationCode = async () => {
  if (!phoneForm.phoneNumber) {
    alert('请输入手机号')
    return
  }
  
  if (!/^\d{11}$/.test(phoneForm.phoneNumber)) {
    alert('请输入正确的手机号码');
    return;
  }

  isSubmitting.value = true;
  
  try {
    const response = await axios.post('http://localhost:9003/picture/login', {
      phoneNumber: phoneForm.phoneNumber,
      type: 'verification_code_request' // 特殊类型表示请求验证码
    });
    
    if (response.data.success) {
      // 启动倒计时
      countdown.value = 60
      const timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          clearInterval(timer)
        }
      }, 1000);
      
      alert('验证码已发送');
    } else {
      alert(response.data.message || '发送验证码失败');
    }
  } catch (error) {
    console.error('发送验证码失败:', error);
    if (error.response) {
      alert(error.response.data.message || '发送验证码失败');
    } else {
      alert('网络错误，请稍后重试');
    }
  } finally {
    isSubmitting.value = false;
  }
}

// 处理账号密码登录
const handleAccountLogin = async () => {
  if (!accountForm.username || !accountForm.password) {
    alert('请输入账号和密码');
    return;
  }

  isSubmitting.value = true;
  
  try {
    const response = await axios.post('http://localhost:9003/picture/login', {
      username: accountForm.username,
      password: accountForm.password,
      type: 'account'
    });
    
    if (response.data.success) {
      // 登录成功，保存用户信息
      localStorage.setItem('isLoggedIn', 'true');
      localStorage.setItem('user', JSON.stringify(response.data.user));
      localStorage.setItem('token', response.data.token);
      
      // 触发登录成功事件
      window.dispatchEvent(new CustomEvent('loginSuccess'));
    } else {
      alert(response.data.message || '登录失败');
    }
  } catch (error) {
    console.error('登录失败:', error);
    if (error.response) {
      alert(error.response.data.message || '登录失败');
    } else {
      alert('网络错误，请稍后重试');
    }
  } finally {
    isSubmitting.value = false;
  }
}

// 处理手机验证码登录
const handlePhoneLogin = async () => {
  if (!phoneForm.code) {
    alert('请输入验证码');
    return
  }
  
  isSubmitting.value = true;
  
  try {
    const response = await axios.post('http://localhost:9003/picture/login', {
      phoneNumber: phoneForm.phoneNumber,
      code: phoneForm.code,
      type: 'phone'
    });
    
    if (response.data.success) {
      // 登录成功，保存用户信息
      localStorage.setItem('isLoggedIn', 'true');
      localStorage.setItem('user', JSON.stringify(response.data.user));
      localStorage.setItem('token', response.data.token);
      
      // 触发登录成功事件
      window.dispatchEvent(new CustomEvent('loginSuccess'));
    } else {
      alert(response.data.message || '登录失败');
    }
  } catch (error) {
    console.error('登录失败:', error);
    if (error.response) {
      alert(error.response.data.message || '登录失败');
    } else {
      alert('网络错误，请稍后重试');
    }
  } finally {
    isSubmitting.value = false;
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: 
    radial-gradient(circle at 10% 20%, rgba(102, 126, 234, 0.1) 0%, transparent 20%),
    radial-gradient(circle at 90% 80%, rgba(118, 75, 162, 0.1) 0%, transparent 20%),
    linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
}

.login-card {
  width: 100%;
  max-width: 400px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  padding: 40px;
  position: relative;
  z-index: 2;
  margin-top: 100px; /* 偏下位置 */
  margin-right: 15%; /* 偏右位置 */
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  color: #333;
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 10px;
  letter-spacing: 0.5px;
}

.login-header .subtitle {
  color: #666;
  font-size: 15px;
  margin: 0;
}

.tabs {
  display: flex;
  background: #f0f2f5;
  border-radius: 10px;
  padding: 5px;
  margin-bottom: 30px;
}

.tab {
  flex: 1;
  text-align: center;
  padding: 14px;
  cursor: pointer;
  border-radius: 8px;
  font-weight: 600;
  color: #666;
  transition: all 0.3s ease;
  font-size: 15px;
}

.tab.active {
  background: white;
  color: #667eea;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.input-group {
  margin-bottom: 24px;
}

.input-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #555;
  font-size: 14px;
}

.input-group input {
  width: 100%;
  padding: 15px;
  border: 1px solid #e1e5e9;
  border-radius: 10px;
  font-size: 16px;
  transition: border-color 0.3s, box-shadow 0.3s;
  box-sizing: border-box;
  background: #f8fafc;
}

.input-group input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.15);
  background: white;
}

.verification-input {
  display: flex;
  gap: 10px;
}

.verification-input input {
  flex: 1;
}

.code-btn {
  padding: 15px 12px;
  background: #eef2f7;
  border: 1px solid #e1e5e9;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  color: #495057;
  white-space: nowrap;
  transition: all 0.3s;
}

.code-btn:hover:not(:disabled) {
  background: #e0e7ff;
  color: #4f46e5;
  border-color: #a5b4fc;
}

.code-btn:disabled {
  background: #f1f5f9;
  color: #94a3b8;
  cursor: not-allowed;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
  font-size: 14px;
}

.remember {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #666;
  font-weight: 500;
}

.remember input {
  margin-right: 8px;
  margin-top: 0;
  width: 16px;
  height: 16px;
  accent-color: #667eea;
}

.forgot {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
}

.forgot:hover {
  text-decoration: underline;
}

.login-btn {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  letter-spacing: 0.5px;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.5);
}

.login-btn:disabled {
  background: #a3b6d9;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.social-login {
  margin: 35px 0 30px;
}

.divider {
  text-align: center;
  position: relative;
  margin-bottom: 25px;
}

.divider:before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: #e1e5e9;
  z-index: 1;
}

.divider span {
  position: relative;
  background: rgba(255, 255, 255, 0.95);
  padding: 0 15px;
  color: #666;
  font-size: 14px;
  z-index: 2;
  font-weight: 500;
}

.social-options {
  display: flex;
  justify-content: center;
}

.social-btn {
  display: flex;
  align-items: center;
  padding: 12px 24px;
  border: 1px solid #e1e5e9;
  border-radius: 10px;
  background: white;
  cursor: pointer;
  font-size: 15px;
  color: #666;
  transition: all 0.3s ease;
  margin: 0 5px;
  font-weight: 500;
}

.social-btn:hover {
  border-color: #667eea;
  color: #667eea;
  box-shadow: 0 6px 15px rgba(102, 126, 234, 0.2);
}

.social-btn.wechat {
  color: #1aad19;
  border-color: #1aad19;
}

.social-btn.wechat:hover {
  background: #1aad19;
  color: white;
}

.icon {
  margin-right: 8px;
  vertical-align: middle;
}

.register-link {
  text-align: center;
  padding-top: 25px;
  border-top: 1px solid #eee;
  margin-top: 10px;
}

.register-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
}

.register-link a:hover {
  text-decoration: underline;
}

@media (max-width: 1024px) {
  .login-card {
    margin-right: 10%;
  }
}

@media (max-width: 768px) {
  .login-container {
    padding: 15px;
  }
  
  .login-card {
    max-width: 100%;
    margin: 100px 0 0; /* 居中而不是偏右 */
    padding: 30px 25px;
  }
  
  .form-options {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>