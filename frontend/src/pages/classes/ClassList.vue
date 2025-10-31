<template>
  <div style="padding:16px">
    <el-card>
      <div style="display:flex;gap:8px;align-items:center;margin-bottom:12px">
        <el-input v-model="query.keyword" placeholder="班级名称/编号/专业" style="width:240px" />
        <el-input v-model="query.grade" placeholder="年级" style="width:140px" />
        <el-button type="primary" @click="load">搜索</el-button>
        <el-button @click="reset">重置</el-button>
        <div style="flex:1"></div>
        <el-button type="primary" @click="$router.push('/classes/new')">新增</el-button>
        <el-button type="danger" :disabled="!selectedIds.length" @click="removeBatch">批量删除</el-button>
      </div>

      <el-table :data="list" @selection-change="onSelectionChange" border style="width:100%">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="classNo" label="班级编号" sortable="custom" />
        <el-table-column prop="name" label="班级名称" sortable="custom" />
        <el-table-column prop="grade" label="年级" width="120" sortable="custom" />
        <el-table-column prop="major" label="专业" />
        <el-table-column prop="counselor" label="辅导员" />
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button type="primary" link @click="$router.push(`/classes/${row.id}`)">详情</el-button>
            <el-button type="primary" link @click="$router.push(`/classes/${row.id}/edit`)">编辑</el-button>
            <el-popconfirm title="确认删除该班级？" @confirm="removeOne(row.id)">
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
import { deleteClass, deleteClassesBatch, fetchClasses, type ClassEntity } from '@/api/class'

const query = reactive({ keyword: '', grade: '', sortBy: '', sortOrder: '', pageNum: 1, pageSize: 10 })
const list = ref<ClassEntity[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])

async function load() {
  const { data } = await fetchClasses(query)
  list.value = data.records
  total.value = data.total
}

function reset() {
  query.keyword = ''
  query.grade = ''
  query.pageNum = 1
  load()
}

function onSelectionChange(rows: ClassEntity[]) {
  selectedIds.value = rows.map(r => r.id!)
}

async function removeOne(id?: number) {
  if (!id) return
  await deleteClass(id)
  load()
}

async function removeBatch() {
  await deleteClassesBatch(selectedIds.value)
  selectedIds.value = []
  load()
}

onMounted(load)
</script>


