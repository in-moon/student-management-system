<template>
  <el-card>
    <h3>{{ isEdit ? '编辑课程' : '新增课程' }}</h3>
    <el-form :model="form" label-width="100px" style="max-width:600px">
      <el-form-item label="课程编号" required>
        <el-input v-model="form.courseNo" placeholder="如 CS101" />
      </el-form-item>
      <el-form-item label="课程名称" required>
        <el-input v-model="form.name" placeholder="课程名称" />
      </el-form-item>
      <el-form-item label="学分">
        <el-input-number v-model="form.credit" :min="0" :max="10" :step="0.5" />
      </el-form-item>
      <el-form-item label="授课教师">
        <el-input v-model="form.teacher" placeholder="教师姓名" />
      </el-form-item>
      <el-form-item label="学期">
        <el-select v-model="form.semester" placeholder="选择学期" style="width:100%">
          <el-option label="2025-2026-1" value="2025-2026-1" />
          <el-option label="2025-2026-2" value="2025-2026-2" />
          <el-option label="2026-2027-1" value="2026-2027-1" />
        </el-select>
      </el-form-item>
      <el-form-item label="课程容量">
        <el-input-number v-model="form.maxStudents" :min="1" :max="500" />
      </el-form-item>
      <el-form-item label="课程描述">
        <el-input v-model="form.description" type="textarea" :rows="3" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
        <el-button @click="$router.back()">返回</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCourse, createCourse, updateCourse, type Course } from '@/api/course'

const route = useRoute()
const router = useRouter()
const isEdit = ref(!!route.params.id)
const saving = ref(false)

const form = ref<Course>({
  courseNo: '',
  name: '',
  credit: 3,
  teacher: '',
  semester: '2025-2026-1',
  maxStudents: 60,
  description: ''
})

onMounted(async () => {
  if (isEdit.value) {
    const res = await getCourse(Number(route.params.id))
    form.value = res.data
  }
})

async function handleSave() {
  if (!form.value.courseNo || !form.value.name) {
    ElMessage.warning('请填写课程编号和名称')
    return
  }
  saving.value = true
  try {
    if (isEdit.value) {
      await updateCourse(Number(route.params.id), form.value)
      ElMessage.success('更新成功')
    } else {
      await createCourse(form.value)
      ElMessage.success('创建成功')
    }
    router.push('/courses')
  } finally {
    saving.value = false
  }
}
</script>
