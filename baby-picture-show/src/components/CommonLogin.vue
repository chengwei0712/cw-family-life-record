<template>
  <div class="common-login-container">
    <div class="login-card">
      <div class="login-header">
        <h1>Baby Picture Show</h1>
        <p class="subtitle">欢迎使用宝宝照片管理系统</p>
      </div>
      
      <!-- 登录表单 -->
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="input-group">
          <label for="username">用户名/邮箱</label>
          <input 
            id="username"
            type="text" 
            v-model="loginForm.username" 
            placeholder="请输入用户名或邮箱"
            required
          />
        </div>
        
        <div class="input-group">
          <label for="password">密码</label>
          <input 
            id="password"
            type="password" 
            v-model="loginForm.password" 
            placeholder="请输入密码"
            required
          />
        </div>
        
        <div class="form-options">
          <label class="remember">
            <input type="checkbox" v-model="loginForm.remember">
            <span>记住我</span>
          </label>
          <a href="#" class="forgot">忘记密码?</a>
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
          <button class="social-btn wechat" @click="handleWechatLogin">
            <svg class="icon" viewBox="0 0 24 24" width="18" height="18">
              <path fill="currentColor" d="M19.5,9a.5.5,0,0,0-.5.5v1a.5.5,0,0,0,.5.5h1a.5.5,0,0,0,.5-.5v-1a.5.5,0,0,0-.5-.5ZM17.5,10a.5.5,0,0,0,.5.5h1a.5.5,0,0,0,.5-.5v-1a.5.5,0,0,0-.5-.5h-1a.5.5,0,0,0-.5.5ZM15,10a.5.5,0,0,0,.5.5h1a.5.5,0,0,0,.5-.5v-1a.5.5,0,0,0-.5-.5h-1a.5.5,0,0,0-.5.5ZM12.5,10a.5.5,0,0,0,.5.5h1a.5.5,0,0,0,.5-.5v-1a.5.5,0,0,0-.5-.5h-1a.5.5,0,0,0-.5.5ZM10,10a.5.5,0,0,0,.5.5h1a.5.5,0,0,0,.5-.5v-1a.5.5,0,0,0-.5-.5h-1a.5.5,0,0,0-.5.5ZM7.5,10a.5.5,0,0,0,.5.5h1a.5.5,0,0,0,.5-.5v-1a.5.5,0,0,0-.5-.5h-1a.5.5,0,0,0-.5.5Z"/>
            </svg>
            微信登录
          </button>
          
          <button class="social-btn phone" @click="showPhoneLogin = true">
            <svg class="icon" viewBox="0 0 24 24" width="18" height="18">
              <path fill="currentColor" d="M17,3H7A2,2,0,0,0,5,5V19a2,2,0,0,0,2,2H17a2,2,0,0,0,2-2V5A2,2,0,0,0,17,3ZM18,19a1,1,0,0,1-1,1H7a1,1,0,0,1-1-1V5A1,1,0,0,1,7,4H17a1,1,0,0,1,1,1ZM12,18a3,3,0,1,1,3-3A3,3,0,0,1,12,18ZM12,13a1,1,0,1,0-1,1A1,1,0,0,0,12,13Z"/>
            </svg>
            手机登录
          </button>
        </div>
      </div>
      
      <!-- 手机登录弹窗 -->
      <div v-if="showPhoneLogin" class="phone-login-modal">
        <div class="modal-content">
          <div class="modal-header">
            <h3>手机登录</h3>
            <button class="close-btn" @click="showPhoneLogin = false">&times;</button>
          </div>
          
          <div class="phone-form">
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
              <label for="phone-code">验证码</label>
              <div class="verification-input">
                <input 
                  id="phone-code"
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
            
            <button 
              type="button" 
              class="login-btn" 
              :disabled="isSubmitting"
              @click="handlePhoneLogin"
            >
              {{ isSubmitting ? '登录中...' : '登录' }}
            </button>
          </div>
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

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

// 手机登录表单数据
const phoneForm = reactive({
  phoneNumber: '',
  code: ''
})

// 提交状态
const isSubmitting = ref(false)

// 显示手机登录弹窗
const showPhoneLogin = ref(false)

// 验证码倒计时
const countdown = ref(0)

// 处理账号密码登录
const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    alert('请输入用户名和密码');
    return;
  }

  isSubmitting.value = true;
  
  try {
    const response = await axios.post('http://localhost:9003/picture/login', {
      username: loginForm.username,
      password: loginForm.password,
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
  if (!phoneForm.phoneNumber) {
    alert('请输入手机号');
    return;
  }
  
  if (!phoneForm.code) {
    alert('请输入验证码');
    return;
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
      
      // 关闭弹窗
      showPhoneLogin.value = false;
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

// 微信登录处理
const handleWechatLogin = () => {
  alert('微信登录功能将在后续版本中实现');
}

// 显示注册页面
const showRegister = () => {
  alert('注册功能将在后续版本中实现');
}
</script>

<style scoped>
.common-login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 420px; /* 保留最大宽度限制 */
  background: white;
  border-radius: 12px;
  box-shadow: 0 15px 35px rgba(50, 50, 93, 0.1), 0 5px 15px rgba(0, 0, 0, 0.07);
  padding: 40px;
  position: relative;
}

/* 移除 .login-card 的固定宽度和高度限制 */
.login-card {
}

/* 使用 Flexbox 确保登录表单居中 */
.common-login-container {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 更新背景样式以适应全屏 */
.common-login-container {
  background-size: cover;
  background-position: center;
}

/* 调整其他样式以适应全屏布局 */
.login-header {
  text-align: center;
  margin-bottom: 35px;
}

.login-header h1 {
  color: #333;
  font-size: 26px;
  font-weight: 700;
  margin: 0 0 10px;
}

.login-header .subtitle {
  color: #666;
  font-size: 15px;
  margin: 0;
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
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s, box-shadow 0.3s;
  box-sizing: border-box;
}

.input-group input:focus {
  outline: none;
  border-color: #42b983;
  box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.1);
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
  accent-color: #42b983;
}

.forgot {
  color: #42b983;
  text-decoration: none;
  font-weight: 500;
}

.forgot:hover {
  text-decoration: underline;
}

.login-btn {
  width: 100%;
  padding: 16px;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  letter-spacing: 0.5px;
}

.login-btn:hover:not(:disabled) {
  background: #359c6d;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.3);
}

.login-btn:disabled {
  background: #a3d9c5;
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
  background: white;
  padding: 0 15px;
  color: #666;
  font-size: 14px;
  z-index: 2;
  font-weight: 500;
}

.social-options {
  display: flex;
  justify-content: center;
  gap: 15px;
}

.social-btn {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  background: white;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  transition: all 0.3s ease;
  font-weight: 500;
}

.social-btn:hover {
  border-color: #42b983;
  color: #42b983;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}

.social-btn.wechat {
  color: #1aad19;
  border-color: #1aad19;
}

.social-btn.wechat:hover {
  background: #1aad19;
  color: white;
}

.social-btn.phone {
  color: #42b983;
  border-color: #42b983;
}

.social-btn.phone:hover {
  background: #42b983;
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
  color: #42b983;
  text-decoration: none;
  font-weight: 600;
}

.register-link a:hover {
  text-decoration: underline;
}

/* 手机登录弹窗样式 */
.phone-login-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 100%;
  max-width: 400px;
  padding: 30px;
  position: relative;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.modal-header h3 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  color: #666;
}

.verification-input {
  display: flex;
  gap: 10px;
}

.verification-input input {
  flex: 1;
}

.code-btn {
  padding: 15px 10px;
  background: #f1f3f5;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  white-space: nowrap;
}

.code-btn:hover:not(:disabled) {
  background: #e9ecef;
  color: #333;
}

.code-btn:disabled {
  background: #f8f9fa;
  color: #adb5bd;
  cursor: not-allowed;
}

@media (max-width: 480px) {
  .login-card {
    padding: 30px 20px;
  }
  
  .social-options {
    flex-direction: column;
  }
  
  .form-options {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
