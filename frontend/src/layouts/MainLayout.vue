<template>
  <el-container style="height:100vh">
    <el-aside width="220px" style="border-right:1px solid var(--el-border-color)">
      <div style="height:60px;display:flex;align-items:center;justify-content:center;font-weight:600;font-size:18px;border-bottom:1px solid var(--el-border-color)">学生管理系统</div>
      <el-menu :default-active="$route.path" router>
        <el-menu-item index="/students">
          <el-icon><User /></el-icon>
          <span>学生管理</span>
        </el-menu-item>
        <el-menu-item index="/classes">
          <el-icon><Collection /></el-icon>
          <span>班级管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="display:flex;align-items:center;justify-content:space-between;border-bottom:1px solid var(--el-border-color);height:60px">
        <div />
        <div style="display:flex;align-items:center;gap:12px">
          <el-tag type="success">已登录</el-tag>
          <el-button size="small" @click="logout" type="danger" plain>退出</el-button>
        </div>
      </el-header>
      <el-main style="background:#f6f7fb">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
  
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { User, Collection } from '@element-plus/icons-vue'
const router = useRouter()
function logout(){
  localStorage.removeItem('token')
  router.push('/login')
}
</script>


