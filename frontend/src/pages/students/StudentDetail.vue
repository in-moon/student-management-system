<template>
  <div style="padding:16px">
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>学生详情</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="学号">{{ student?.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ student?.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ student?.gender }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ student?.age }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ student?.clazz }}</el-descriptions-item>
        <el-descriptions-item label="专业">{{ student?.major }}</el-descriptions-item>
        <el-descriptions-item label="入学日期">{{ student?.enrollDate }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ student?.phone }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { fetchStudent, type Student } from '@/api/student'

const route = useRoute()
const student = ref<Student>()

onMounted(async () => {
  const id = Number(route.params.id)
  const { data } = await fetchStudent(id)
  student.value = data
})
</script>


