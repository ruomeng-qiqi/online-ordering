<template>
  <div class="login-container">
    <div class="login-wrapper">
      <!-- 左侧装饰区域 -->
      <div class="login-left">
        <div class="decoration-content">
          <div class="logo-section">
            <el-icon :size="80" class="logo-icon">
              <Food />
            </el-icon>
            <h1 class="system-title">在线点餐管理系统</h1>
          </div>
          <div class="feature-list">
            <div class="feature-item">
              <el-icon :size="24"><CircleCheck /></el-icon>
              <span>智能订单管理</span>
            </div>
            <div class="feature-item">
              <el-icon :size="24"><CircleCheck /></el-icon>
              <span>实时数据统计</span>
            </div>
            <div class="feature-item">
              <el-icon :size="24"><CircleCheck /></el-icon>
              <span>便捷菜品管理</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="login-right">
        <div class="login-form-wrapper">
          <div class="form-header">
            <h2>欢迎登录</h2>
            <p>请输入您的账号和密码</p>
          </div>

          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            class="login-form"
            size="large"
          >
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                clearable
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                show-password
                @keyup.enter="handleLogin"
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                :loading="loading"
                class="login-button"
                @click="handleLogin"
              >
                <span v-if="!loading">登 录</span>
                <span v-else>登录中...</span>
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>

    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Food, CircleCheck } from '@element-plus/icons-vue'
import { login } from '@/api/admin'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      const res = await login(loginForm)

      // 保存用户信息
      userStore.setUserInfo(res.data)

      ElMessage.success('登录成功')

      // 跳转到首页
      router.push('/')
    } catch (error) {
      console.error('登录失败：', error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #2b3a4b 0%, #304156 50%, #409eff 100%);
  overflow: hidden;
}

.login-wrapper {
  position: relative;
  display: flex;
  width: 1000px;
  height: 600px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  z-index: 1;
}

/* 左侧装饰区域 */
.login-left {
  flex: 1;
  background: linear-gradient(135deg, #2b3a4b 0%, #304156 50%, #409eff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  color: #fff;
}

.decoration-content {
  text-align: center;
}

.logo-section {
  margin-bottom: 60px;
}

.logo-icon {
  color: #fff;
  margin-bottom: 20px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.system-title {
  font-size: 32px;
  font-weight: bold;
  margin: 20px 0 10px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.system-subtitle {
  font-size: 14px;
  opacity: 0.9;
  letter-spacing: 2px;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 15px;
  font-size: 16px;
  opacity: 0.95;
}

/* 右侧登录表单 */
.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  background: #fff;
}

.login-form-wrapper {
  width: 100%;
  max-width: 380px;
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.form-header h2 {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

.form-header p {
  font-size: 14px;
  color: #909399;
}

.login-form {
  margin-top: 30px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.login-form :deep(.el-input__wrapper) {
  padding: 12px 15px;
  border-radius: 8px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  transition: all 0.3s;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: bold;
  border-radius: 8px;
  background: linear-gradient(135deg, #304156 0%, #409eff 100%);
  border: none;
  transition: all 0.3s;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.4);
}

.login-button:active {
  transform: translateY(0);
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float-circle 20s infinite ease-in-out;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  right: 100px;
  animation-delay: 5s;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  right: -50px;
  animation-delay: 10s;
}

@keyframes float-circle {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -30px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.9);
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .login-wrapper {
    width: 90%;
    height: auto;
    flex-direction: column;
  }

  .login-left {
    padding: 40px;
  }

  .login-right {
    padding: 40px;
  }
}
</style>
