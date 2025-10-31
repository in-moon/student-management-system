<template>
  <div class="login-wrap">
    <div class="login-card">
      <div class="brand">
        <svg viewBox="0 0 48 48" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
          <defs>
            <linearGradient id="g" x1="0" y1="0" x2="1" y2="1">
              <stop offset="0%" stop-color="#409EFF"/>
              <stop offset="100%" stop-color="#67C23A"/>
            </linearGradient>
          </defs>
          <rect x="4" y="4" width="40" height="40" rx="10" fill="url(#g)"/>
          <path d="M14 26h20M14 18h20M14 34h12" stroke="#fff" stroke-width="3" stroke-linecap="round"/>
        </svg>
        <div class="title">学生管理系统</div>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top" @submit.prevent="onSubmit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="admin" clearable size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="admin" show-password clearable size="large" />
        </el-form-item>
        <el-button type="primary" size="large" class="login-btn" @click="onSubmit" :loading="loading">登 录</el-button>
      </el-form>
      <div class="tips">演示账号：admin / admin</div>
    </div>
  </div>
  
</template>

<script setup lang="ts">
import http from '@/api/http'
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const form = reactive({ username: 'admin', password: 'admin' })
const loading = ref(false)
const formRef = ref()
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function onSubmit() {
  await formRef.value?.validate()
  loading.value = true
  try {
    const { data } = await http.post('/auth/login', form)
    localStorage.setItem('token', data.token)
    router.push('/students')
  } catch (e) {
    ElMessage.error('登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrap{
  min-height:100vh;
  display:flex;
  align-items:center;
  justify-content:center;
  position: relative;
  background:
    linear-gradient(180deg, rgba(0,0,0,.15) 0%, rgba(0,0,0,.25) 100%),
    url('https://images.unsplash.com/photo-1523580846011-d3a5bc25702b?q=80&w=1920&auto=format&fit=crop') center/cover no-repeat fixed;
}
.login-card{
  width: 380px;
  background: rgba(255,255,255,0.18);
  border: 1px solid rgba(255,255,255,0.35);
  box-shadow: 0 20px 40px -12px rgba(0,0,0,.35);
  border-radius: 16px;
  padding: 28px;
  backdrop-filter: blur(10px);
}
.brand{ display:flex; align-items:center; gap:12px; margin-bottom:12px; }
.brand svg{ width:32px; height:32px; border-radius:8px; }
.brand .title{ font-weight:700; font-size:18px; }
.login-btn{ width:100%; margin-top:6px; }
.tips{ text-align:center; color:#fff; opacity:.9; margin-top:8px; font-size:12px; }
/* 输入框透明化与浅色主题 */
:deep(.el-form-item__label){ color:#fff; opacity:.95; }
:deep(.el-input__wrapper){
  background-color: transparent !important;
  box-shadow: 0 0 0 1px rgba(255,255,255,.35) inset !important;
  backdrop-filter: none;
}
:deep(.el-input__inner){ color:#fff; }
:deep(.el-input__inner::placeholder){ color: rgba(255,255,255,.75); }
</style>


