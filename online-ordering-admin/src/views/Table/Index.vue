<template>
  <div class="table-container">
    <el-card>
      <!-- 搜索栏 -->
      <div class="search-bar">
        <span style="margin-right: 10px; font-size: 14px; white-space: nowrap;">餐台名称:</span>
        <el-input 
          v-model="searchName" 
          placeholder="请输入餐台名称" 
          style="width: 150px; margin-right: 15px;"
          clearable
        />
        <el-button type="primary" @click="handleSearch">查询</el-button>
        
        <div style="flex: 1"></div>
        
        <el-button type="primary" @click="handleAdd">+ 新增餐台</el-button>
      </div>

      <el-table 
        :data="filteredTableList" 
        style="width: 100%; margin-top: 20px;"
      >
        <el-table-column prop="name" label="餐台名称" min-width="150" align="center" />
        <el-table-column prop="seats" label="座位数" min-width="120" align="center" />
        <el-table-column prop="status" label="状态" min-width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'info'">
              {{ row.status === 0 ? '空闲' : '占用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="操作时间" min-width="160" align="center" />
        <el-table-column label="操作" min-width="180px" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">修改</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无数据" />
        </template>
      </el-table>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle"
      width="500px"
    >
      <el-form :model="formData" label-width="100px">
        <el-form-item label="餐台名称">
          <el-input v-model="formData.name" placeholder="请输入餐台名称" style="width: 300px;" />
        </el-form-item>
        <el-form-item label="座位数">
          <el-input v-model="formData.seats" placeholder="请输入座位数" style="width: 200px;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 模拟数据
const tableList = ref([
  { 
    id: 1, 
    name: '1号桌', 
    seats: 4, 
    status: 0,
    updateTime: '2024-01-15 10:30'
  },
  { 
    id: 2, 
    name: '2号桌', 
    seats: 6, 
    status: 1,
    updateTime: '2024-01-15 11:20'
  },
  { 
    id: 3, 
    name: '3号桌', 
    seats: 4, 
    status: 0,
    updateTime: '2024-01-15 09:15'
  },
  { 
    id: 4, 
    name: '包厢A', 
    seats: 10, 
    status: 0,
    updateTime: '2024-01-14 18:45'
  },
])

const searchName = ref('')
const dialogVisible = ref(false)
const dialogTitle = ref('添加餐台')
const formData = ref({
  id: null,
  name: '',
  seats: '',
  status: 0
})

// 过滤后的餐台列表
const filteredTableList = computed(() => {
  return tableList.value.filter(item => {
    const matchName = !searchName.value || item.name.includes(searchName.value)
    return matchName
  })
})

// 搜索
const handleSearch = () => {
  // 计算属性会自动更新
}

// 添加餐台
const handleAdd = () => {
  dialogTitle.value = '新增餐台'
  formData.value = {
    id: null,
    name: '',
    seats: '',
    status: 0
  }
  dialogVisible.value = true
}

// 编辑餐台
const handleEdit = (row) => {
  dialogTitle.value = '修改餐台'
  formData.value = { ...row }
  dialogVisible.value = true
}

// 删除餐台
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该餐台吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const index = tableList.value.findIndex(item => item.id === row.id)
    if (index > -1) {
      tableList.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  }).catch(() => {})
}

// 格式化时间为 yyyy-MM-dd HH:mm
const formatDateTime = (date = new Date()) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 提交表单
const handleSubmit = () => {
  if (!formData.value.name) {
    ElMessage.warning('请输入餐台名称')
    return
  }
  if (!formData.value.seats || formData.value.seats <= 0) {
    ElMessage.warning('请输入正确的座位数')
    return
  }

  const currentTime = formatDateTime()

  if (formData.value.id) {
    // 编辑
    const index = tableList.value.findIndex(item => item.id === formData.value.id)
    if (index > -1) {
      tableList.value[index] = { 
        ...formData.value,
        updateTime: currentTime
      }
      ElMessage.success('修改成功')
    }
  } else {
    // 添加
    const newTable = {
      ...formData.value,
      id: Date.now(),
      updateTime: currentTime
    }
    tableList.value.push(newTable)
    ElMessage.success('添加成功')
  }

  dialogVisible.value = false
}
</script>

<style scoped>
.table-container {
  padding: 20px;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>
