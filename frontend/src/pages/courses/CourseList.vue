<template>
  <div>
    <el-card>
      <div class="page-header">
        <h3>📚 课程管理</h3>
        <el-button type="primary" @click="$router.push('/courses/new')">新增课程</el-button>
      </div>

      <el-form :inline="true" :model="query" style="margin-top:12px">
        <el-form-item label="课程名称">
          <el-input v-model="query.name" placeholder="搜索课程" clearable @clear="fetchData" />
        </el-form-item>
        <el-form-item label="授课教师">
          <el-input v-model="query.teacher" placeholder="搜索教师" clearable @clear="fetchData" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="query = {}; fetchData()">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="courses" stripe v-loading="loading" style="margin-top:12px">
        <el-table-column prop="courseNo" label="课程编号" width="100" />
        <el-table-column prop="name" label="课程名称" min-width="150" />
        <el-table-column prop="credit" label="学分" width="70" />
        <el-table-column prop="teacher" label="授课教师" width="100" />
        <el-table-column prop="semester" label="学期" width="110" />
        <el-table-column prop="maxStudents" label="容量" width="70" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="$router.push(`/courses/${row.id}/edit`)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-if="total > 20"
        style="margin-top:16px;justify-content:flex-end"
        background
        layout="total, prev, pager, next"
        :total="total"
        :page-size="query.pageSize || 20"
        v-model:current-page="query.page"
        @current-change="fetchData"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCourses, deleteCourse, type Course } from '@/api/course'

const courses = ref<Course[]>([])
const total = ref(0)
const loading = ref(false)
const query = ref<{ name?: string; teacher?: string; page?: number; pageSize?: number }>({ page: 1, pageSize: 20 })

async function fetchData() {
  loading.value = true
  try {
    const res = await getCourses(query.value)
    courses.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function handleDelete(row: Course) {
  try {
    await ElMessageBox.confirm(`确定删除课程「${row.name}」吗？`, '确认删除', { type: 'warning' })
    await deleteCourse(row.id!)
    ElMessage.success('删除成功')
    fetchData()
  } catch {}
}

onMounted(fetchData)
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-header h3 {
  margin: 0;
}
</style>
