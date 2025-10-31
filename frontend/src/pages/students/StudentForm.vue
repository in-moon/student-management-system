<template>
  <div style="padding:16px">
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>{{ isEdit ? '编辑学生' : '新增学生' }}</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>
      <el-form :model="form" label-width="100" style="max-width:720px">
        <el-form-item label="学号"><el-input v-model="form.studentNo" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender" style="width:120px">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄"><el-input-number v-model="form.age" :min="0" /></el-form-item>
        <el-form-item label="班级"><el-input v-model="form.clazz" /></el-form-item>
        <el-form-item label="专业"><el-input v-model="form.major" /></el-form-item>
        <el-form-item label="入学日期"><el-date-picker v-model="form.enrollDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="联系方式"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createStudent, fetchStudent, updateStudent, type Student } from '@/api/student'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => route.params.id && route.params.id !== 'new')

const form = reactive<Student>({ studentNo: '', name: '', gender: '男', age: 18, clazz: '', major: '', enrollDate: '', phone: '' })

onMounted(async () => {
  if (isEdit.value) {
    const id = Number(route.params.id)
    const { data } = await fetchStudent(id)
    Object.assign(form, data)
  }
})

async function onSubmit() {
  if (isEdit.value) {
    await updateStudent(Number(route.params.id), form)
  } else {
    await createStudent(form)
  }
  router.push('/students')
}
</script>


