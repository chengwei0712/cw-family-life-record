<template>
  <div class="register-container">
    <div class="register-form">
      <h2>注册新账号</h2>
      <p class="subtitle">创建您的账户</p>
      
      <form @submit.prevent="handleRegister">
        <div class="input-group">
          <label>用户名</label>
          <input 
            type="text" 
            v-model="registerForm.username" 
            placeholder="请输入用户名"
            required
          />
        </div>
        
        <div class="input-group">
          <label>手机号</label>
          <div class="phone-input">
            <select v-model="registerForm.countryCode">
              <option value="+86">+86 中国</option>
              <option value="+1">+1 美国</option>
            </select>
            <input 
              type="tel" 
              v-model="registerForm.phoneNumber" 
              placeholder="请输入手机号"
              required
            />
          </div>
        </div>
        
        <div class="input-group">
          <label>验证码</label>
          <div class="verification-input">
            <input 
              type="text" 
              v-model="registerForm.code" 
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
        
        <div class="input-group">
          <label>密码</label>
          <input 
            type="password" 
            v-model="registerForm.password" 
            placeholder="请输入密码"
            required
            minlength="6"
          />
        </div>
        
        <div class="input-group">
          <label>确认密码</label>
          <input 
            type="password" 
            v-model="registerForm.confirmPassword" 
            placeholder="请再次输入密码"
            required
            minlength="6"
          />
        </div>
        
        <button type="submit" class="register-btn" :disabled="isSubmitting">
          {{ isSubmitting ? '注册中...' : '注册' }}
        </button>
      </form>
      
      <div class="back-to-login">
        <p>已有账号? <a href="#" @click.prevent="goToLogin">立即登录</a></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import axios from 'axios'

// 提交状态
const isSubmitting = ref(false)

// 验证码倒计时
const countdown = ref(0)

// 注册表单数据
const registerForm = reactive({
  username: '',
  countryCode: '+86',
  phoneNumber: '',
  code: '',
  password: '',
  confirmPassword: ''
})

// 发送验证码
const sendVerificationCode = async () => {
  if (!registerForm.phoneNumber) {
    alert('请输入手机号')
    return
  }
  
  if (!/^\d{11}$/.test(registerForm.phoneNumber)) {
    alert('请输入正确的手机号码');
    return;
  }

  isSubmitting.value = true;
  
  try {
    const response = await axios.post('http://localhost:9003/picture/login', {
      phoneNumber: `${registerForm.countryCode}${registerForm.phoneNumber}`,
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

// 处理注册
const handleRegister = async () => {
  if (!registerForm.username) {
    alert('请输入用户名');
    return;
  }
  
  if (!/^\d{11}$/.test(registerForm.phoneNumber)) {
    alert('请输入正确的手机号码');
    return;
  }
  
  if (!registerForm.code) {
    alert('请输入验证码');
    return;
  }
  
  if (registerForm.password.length < 6) {
    alert('密码长度至少为6位');
    return;
  }
  
  if (registerForm.password !== registerForm.confirmPassword) {
    alert('两次输入的密码不一致');
    return;
  }

  isSubmitting.value = true;
  
  try {
    const response = await axios.post('http://localhost:9003/picture/register', {
      username: registerForm.username,
      phoneNumber: `${registerForm.countryCode}${registerForm.phoneNumber}`,
      code: registerForm.code,
      password: registerForm.password
    });
    
    if (response.data.success) {
      alert('注册成功，请登录');
      // 跳转到登录页面
      goToLogin();
    } else {
      alert(response.data.message || '注册失败');
    }
  } catch (error) {
    console.error('注册失败:', error);
    if (error.response) {
      alert(error.response.data.message || '注册失败');
    } else {
      alert('网络错误，请稍后重试');
    }
  } finally {
    isSubmitting.value = false;
  }
}

// 跳转到登录页面
const goToLogin = () => {
  // 触发自定义事件通知父组件切换到登录页面
  window.dispatchEvent(new CustomEvent('showLogin'));
}

defineExpose({
  goToLogin
});
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

.register-form {
  width: 100%;
  max-width: 400px;
  background: white;
  border-radius: 10px;
  padding: 30px;
  box-shadow: 0 15px 35px rgba(50, 50, 93, 0.1), 0 5px 15px rgba(0, 0, 0, 0.07);
}

.register-form h2 {
  text-align: center;
  margin: 0 0 10px;
  font-size: 24px;
  color: #333;
}

.subtitle {
  text-align: center;
  color: #666;
  margin-bottom: 30px;
}

.input-group {
  margin-bottom: 20px;
}

.input-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #555;
}

.input-group input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 16px;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.input-group input:focus {
  outline: none;
  border-color: #42b983;
}

.phone-input {
  display: flex;
  gap: 10px;
}

.phone-input select {
  width: 30%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 16px;
}

.phone-input input {
  width: 70%;
}

.verification-input {
  display: flex;
  gap: 10px;
}

.verification-input input {
  flex: 1;
}

.code-btn {
  padding: 12px 15px;
  background: #e9ecef;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.code-btn:hover:not(:disabled) {
  background: #42b983;
  color: white;
  border-color: #42b983;
}

.code-btn:disabled {
  background: #f8f9fa;
  color: #6c757d;
  cursor: not-allowed;
}

.register-btn {
  width: 100%;
  padding: 14px;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.3s;
}

.register-btn:hover:not(:disabled) {
  background: #359c6d;
}

.register-btn:disabled {
  background: #a3d9c5;
  cursor: not-allowed;
}

.back-to-login {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.back-to-login a {
  color: #42b983;
  text-decoration: none;
}

.back-to-login a:hover {
  text-decoration: underline;
}
</style>