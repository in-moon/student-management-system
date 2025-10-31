<template>
  <div style="padding:16px">
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>{{ isEdit ? '编辑班级' : '新增班级' }}</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100" style="max-width:720px">
        <el-form-item label="班级编号" prop="classNo"><el-input v-model="form.classNo" /></el-form-item>
        <el-form-item label="班级名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="年级"><el-input v-model="form.grade" /></el-form-item>
        <el-form-item label="专业"><el-input v-model="form.major" /></el-form-item>
        <el-form-item label="辅导员"><el-input v-model="form.counselor" /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createClass, fetchClass, updateClass, type ClassEntity } from '@/api/class'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => route.params.id && route.params.id !== 'new')
const formRef = ref()
const form = reactive<ClassEntity>({ classNo: '', name: '', grade: '', major: '', counselor: '' })
const rules = { classNo: [{ required: true, message: '请输入班级编号', trigger: 'blur' }], name: [{ required: true, message: '请输入班级名称', trigger: 'blur' }] }

onMounted(async () => {
  if (isEdit.value) {
    const id = Number(route.params.id)
    const { data } = await fetchClass(id)
    Object.assign(form, data)
  }
})

async function onSubmit() {
  await formRef.value?.validate()
  if (isEdit.value) {
    await updateClass(Number(route.params.id), form)
  } else {
    await createClass(form)
  }
  router.push('/classes')
}
</script>


