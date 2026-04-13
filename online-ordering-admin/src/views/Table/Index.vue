<template>
  <div class="table-container">
    <el-card>
      <!-- 搜索栏 -->
      <div class="search-bar">
        <span style="margin-right: 10px; font-size: 14px; white-space: nowrap;">餐台号:</span>
        <el-input 
          v-model="searchTableNumber" 
          placeholder="请输入餐台号" 
          style="width: 150px; margin-right: 15px;"
          clearable
        />
        <span style="margin-right: 10px; font-size: 14px; white-space: nowrap;">餐台名称:</span>
        <el-input 
          v-model="searchTableName" 
          placeholder="请输入餐台名称" 
          style="width: 150px; margin-right: 15px;"
          clearable
        />
        <span style="margin-right: 10px; font-size: 14px; white-space: nowrap;">状态:</span>
        <el-select 
          v-model="searchStatus" 
          placeholder="请选择" 
          style="width: 150px; margin-right: 15px;"
          clearable
        >
          <el-option label="空闲" :value="0" />
          <el-option label="占用" :value="1" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        
        <div style="flex: 1"></div>
        
        <el-button @click="handleBatchDelete">批量删除</el-button>
        <el-button type="primary" @click="handleAdd">+ 新增餐台</el-button>
      </div>

      <el-table 
        :data="tableList" 
        style="width: 100%; margin-top: 20px;"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="tableNumber" label="餐台号" min-width="100" />
        <el-table-column prop="tableName" label="餐台名称" min-width="120" />
        <el-table-column prop="seats" label="座位数" min-width="80" align="center" />
        <el-table-column prop="sort" label="排序" min-width="80" align="center" />
        <el-table-column prop="status" label="状态" min-width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="二维码" min-width="80" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleShowQrCode(row)">
              查看
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="操作时间" min-width="160" align="center" />
        <el-table-column label="操作" min-width="80" align="center" fixed="right">
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
        <el-form-item label="餐台号" required>
          <el-input v-model="formData.tableNumber" placeholder="请输入餐台号（如：A01）" style="width: 300px;" />
        </el-form-item>
        <el-form-item label="餐台名称" required>
          <el-input v-model="formData.tableName" placeholder="请输入餐台名称" style="width: 300px;" />
        </el-form-item>
        <el-form-item label="座位数" required>
          <el-input v-model="formData.seats" placeholder="请输入座位数" style="width: 300px;" type="number" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input v-model="formData.sort" placeholder="请输入排序" style="width: 300px;" type="number" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 二维码对话框 -->
    <el-dialog 
      v-model="qrCodeDialogVisible" 
      title="餐台二维码"
      width="400px"
    >
      <div style="text-align: center;">
        <div style="font-size: 16px; font-weight: bold; margin-bottom: 20px;">
          {{ currentTable.tableNumber }} - {{ currentTable.tableName }}
        </div>
        <div style="display: inline-block; padding: 20px; background: #fff; border: 1px solid #ddd;">
          <img 
            :src="currentTable.qrCode" 
            style="width: 200px; height: 200px;" 
            @error="handleImageError"
          />
        </div>
        <div style="margin-top: 20px;">
          <el-button type="primary" @click="handlePrintQrCode">打印二维码</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageQueryTable, addTable, updateTable, deleteTable, deleteBatchTable } from '@/api/table'

const tableList = ref([])
const searchTableNumber = ref('')
const searchTableName = ref('')
const searchStatus = ref('')
const selectedRows = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加餐台')
const qrCodeDialogVisible = ref(false)
const currentTable = ref({})
const formData = ref({
  id: null,
  tableNumber: '',
  tableName: '',
  seats: '',
  sort: ''
})

// 加载餐台列表
const loadTableList = async () => {
  try {
    const params = {
      page: 1,
      pageSize: 1000,
      tableNumber: searchTableNumber.value || undefined,
      tableName: searchTableName.value || undefined,
      status: searchStatus.value !== '' ? searchStatus.value : undefined
    }
    const res = await pageQueryTable(params)
    if (res.code === 200) {
      // 格式化时间，处理二维码URL
      tableList.value = res.data.records.map(item => {
        let qrCodeUrl = item.qrCode || ''
        // 如果是相对路径，添加完整的服务器地址
        if (qrCodeUrl && !qrCodeUrl.startsWith('http')) {
          qrCodeUrl = `http://localhost:8080${qrCodeUrl}`
        }
        return {
          ...item,
          updateTime: item.updateTime ? item.updateTime.replace('T', ' ') : '',
          qrCode: qrCodeUrl
        }
      })
    }
  } catch (error) {
    console.error('加载餐台列表失败:', error)
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadTableList()
})

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '空闲',
    1: '占用'
  }
  return statusMap[status] || '未知'
}

// 获取状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    0: 'success',
    1: 'danger'
  }
  return typeMap[status] || 'info'
}

// 搜索
const handleSearch = () => {
  loadTableList()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的餐台')
    return
  }
  
  ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 个餐台吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const ids = selectedRows.value.map(row => row.id)
      const res = await deleteBatchTable(ids)
      if (res.code === 200) {
        ElMessage.success(res.msg || '删除成功')
        selectedRows.value = []
        loadTableList()
      }
    } catch (error) {
      console.error('批量删除失败:', error)
    }
  }).catch(() => {})
}

// 添加餐台
const handleAdd = () => {
  dialogTitle.value = '新增餐台'
  formData.value = {
    id: null,
    tableNumber: '',
    tableName: '',
    seats: '',
    sort: ''
  }
  dialogVisible.value = true
}

// 编辑餐台
const handleEdit = (row) => {
  dialogTitle.value = '修改餐台'
  formData.value = {
    id: row.id,
    tableNumber: row.tableNumber,
    tableName: row.tableName,
    seats: row.seats,
    sort: row.sort
  }
  dialogVisible.value = true
}

// 删除餐台
const handleDelete = async (row) => {
  ElMessageBox.confirm('确定要删除该餐台吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteTable(row.id)
      if (res.code === 200) {
        ElMessage.success(res.msg || '删除成功')
        loadTableList()
      }
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {})
}

// 显示二维码
const handleShowQrCode = (row) => {
  currentTable.value = row
  qrCodeDialogVisible.value = true
}

// 图片加载失败
const handleImageError = () => {
  ElMessage.error('二维码图片加载失败')
}

// 打印二维码
const handlePrintQrCode = () => {
  const printWindow = window.open('', '_blank')
  printWindow.document.write(`
    <html>
      <head>
        <title>打印二维码 - ${currentTable.value.tableNumber}</title>
        <style>
          body { text-align: center; padding: 20px; }
          h2 { margin-bottom: 20px; }
          img { width: 300px; height: 300px; }
        </style>
      </head>
      <body>
        <h2>${currentTable.value.tableNumber} - ${currentTable.value.tableName}</h2>
        <img src="${currentTable.value.qrCode}" />
      </body>
    </html>
  `)
  printWindow.document.close()
  printWindow.print()
}

// 提交表单
const handleSubmit = async () => {
  if (!formData.value.tableNumber) {
    ElMessage.warning('请输入餐台号')
    return
  }
  
  if (!formData.value.tableName) {
    ElMessage.warning('请输入餐台名称')
    return
  }
  
  if (!formData.value.seats || formData.value.seats <= 0) {
    ElMessage.warning('请输入正确的座位数')
    return
  }

  try {
    const data = {
      id: formData.value.id,
      tableNumber: formData.value.tableNumber,
      tableName: formData.value.tableName,
      seats: parseInt(formData.value.seats),
      sort: parseInt(formData.value.sort) || 0
    }

    if (formData.value.id) {
      // 编辑
      const res = await updateTable(data)
      if (res.code === 200) {
        ElMessage.success(res.msg || '修改成功')
        dialogVisible.value = false
        loadTableList()
      }
    } else {
      // 添加
      const res = await addTable(data)
      if (res.code === 200) {
        ElMessage.success(res.msg || '添加成功')
        dialogVisible.value = false
        loadTableList()
      }
    }
  } catch (error) {
    console.error('提交失败:', error)
  }
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
