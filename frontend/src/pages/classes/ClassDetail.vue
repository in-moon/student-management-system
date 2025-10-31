<template>
  <div style="padding:16px">
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>班级详情</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="班级编号">{{ data?.classNo }}</el-descriptions-item>
        <el-descriptions-item label="班级名称">{{ data?.name }}</el-descriptions-item>
        <el-descriptions-item label="年级">{{ data?.grade }}</el-descriptions-item>
        <el-descriptions-item label="专业">{{ data?.major }}</el-descriptions-item>
        <el-descriptions-item label="辅导员">{{ data?.counselor }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ data?.createdAt }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { fetchClass, type ClassEntity } from '@/api/class'

const route = useRoute()
const data = ref<ClassEntity>()

onMounted(async () => {
  const id = Number(route.params.id)
  const { data:resp } = await fetchClass(id)
  data.value = resp
})
</script>


