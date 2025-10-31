<template>
  <div style="padding:16px">
    <el-card>
      <div style="display:flex;gap:8px;align-items:center;margin-bottom:12px">
        <el-input v-model="query.keyword" placeholder="姓名/学号" style="width:220px" />
        <el-select v-model="query.gender" placeholder="性别" style="width:120px" clearable>
          <el-option label="男" value="男" />
          <el-option label="女" value="女" />
        </el-select>
        <el-input v-model="query.major" placeholder="专业" style="width:160px" />
        <el-button type="primary" @click="load">搜索</el-button>
        <el-button @click="reset">重置</el-button>
        <div style="flex:1"></div>
        <el-button type="primary" @click="$router.push('/students/new')">新增</el-button>
        <el-button type="danger" :disabled="!selectedIds.length" @click="removeBatch">批量删除</el-button>
      </div>

      <el-table :data="list" @selection-change="onSelectionChange" style="width:100%" border>
        <el-table-column type="selection" width="50" />
        <el-table-column prop="studentNo" label="学号" sortable="custom" />
        <el-table-column prop="name" label="姓名" sortable="custom" />
        <el-table-column prop="gender" label="性别" width="80" />
        <el-table-column prop="age" label="年龄" width="80" sortable="custom" />
        <el-table-column prop="clazz" label="班级" />
        <el-table-column prop="major" label="专业" />
        <el-table-column prop="enrollDate" label="入学日期" />
        <el-table-column prop="phone" label="联系方式" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" link @click="$router.push(`/students/${row.id}`)">详情</el-button>
            <el-button type="primary" link @click="$router.push(`/students/${row.id}/edit`)">编辑</el-button>
            <el-popconfirm title="确认删除该学生？" @confirm="removeOne(row.id)">
              <template #reference>
                <el-button type="danger" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div style="display:flex;justify-content:flex-end;margin-top:12px">
        <el-pagination
          background
          layout="prev, pager, next, sizes, total"
          :total="total"
          v-model:page-size="query.pageSize"
          v-model:current-page="query.pageNum"
          @current-change="load"
          @size-change="load"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { deleteStudent, deleteStudentsBatch, fetchStudents, type Student } from '@/api/student'

const query = reactive({ keyword: '', gender: '', major: '', sortBy: '', sortOrder: '', pageNum: 1, pageSize: 10 })
const list = ref<Student[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])

async function load() {
  const { data } = await fetchStudents(query)
  list.value = data.records
  total.value = data.total
}

function reset() {
  query.keyword = ''
  query.gender = ''
  query.major = ''
  query.pageNum = 1
  load()
}

function onSelectionChange(rows: Student[]) {
  selectedIds.value = rows.map(r => r.id!)
}

async function removeOne(id?: number) {
  if (!id) return
  await deleteStudent(id)
  load()
}

async function removeBatch() {
  await deleteStudentsBatch(selectedIds.value)
  selectedIds.value = []
  load()
}

onMounted(load)
</script>


