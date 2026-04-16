<template>
  <div class="setmeal-container">
    <el-card>
      <!-- 搜索栏 -->
      <div class="search-bar">
        <span style="margin-right: 10px; font-size: 14px; white-space: nowrap;">套餐名称:</span>
        <el-input 
          v-model="searchName" 
          placeholder="请输入套餐名称" 
          style="width: 150px; margin-right: 15px;"
          clearable
        />
        <span style="margin-right: 10px; font-size: 14px; white-space: nowrap;">套餐分类:</span>
        <el-select 
          v-model="searchCategoryId" 
          placeholder="请选择" 
          style="width: 150px; margin-right: 15px;"
          clearable
        >
          <el-option 
            v-for="category in categoryList" 
            :key="category.id" 
            :label="category.name" 
            :value="category.id" 
          />
        </el-select>
        <span style="margin-right: 10px; font-size: 14px; white-space: nowrap;">售卖状态:</span>
        <el-select 
          v-model="searchStatus" 
          placeholder="请选择" 
          style="width: 150px; margin-right: 15px;"
          clearable
        >
          <el-option label="起售" :value="1" />
          <el-option label="停售" :value="0" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
        
        <div style="flex: 1"></div>
        
        <el-button @click="handleBatchDelete">批量删除</el-button>
        <el-button type="primary" @click="handleAdd">+ 新增套餐</el-button>
      </div>

      <el-table 
        :data="setmealList" 
        v-loading="loading"
        style="width: 100%; margin-top: 20px;"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="套餐名称" min-width="150" />
        <el-table-column label="图片" min-width="100">
          <template #default="{ row }">
            <el-image 
              :src="row.image" 
              style="width: 50px; height: 50px; border-radius: 4px;"
              fit="cover"
            />
          </template>
        </el-table-column>
        <el-table-column prop="category" label="套餐分类" min-width="120" />
        <el-table-column prop="price" label="套餐价" min-width="100">
          <template #default="{ row }">
            ¥ {{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="售卖状态" min-width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '起售' : '停售' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="操作时间" min-width="160" align="center">
          <template #default="{ row }">
            {{ formatDateTime(row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">修改</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
            <el-button 
              link 
              :type="row.status === 1 ? 'danger' : 'primary'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '停售' : '起售' }}
            </el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无数据" />
        </template>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end;"
        @size-change="loadSetmealList"
        @current-change="loadSetmealList"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSetmealPage, deleteSetmeal, deleteSetmealBatch, updateSetmealStatus } from '@/api/setmeal'
import { getCategoryPage } from '@/api/category'

const router = useRouter()

const searchName = ref('')
const searchCategoryId = ref('')
const searchStatus = ref('')
const selectedRows = ref([])
const loading = ref(false)
const categoryList = ref([])

// 分页数据
const setmealList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 格式化时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return dateTime.replace('T', ' ')
}

// 加载分类列表
const loadCategoryList = async () => {
  try {
    const response = await getCategoryPage({ page: 1, pageSize: 100, type: 2 })
    if (response.code === 200) {
      categoryList.value = response.data.records
    }
  } catch (error) {
    console.error('加载分类列表失败:', error)
  }
}

// 加载套餐列表
const loadSetmealList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    }
    
    if (searchName.value) {
      params.name = searchName.value
    }
    
    if (searchCategoryId.value) {
      params.categoryId = searchCategoryId.value
    }
    
    if (searchStatus.value !== '') {
      params.status = searchStatus.value
    }
    
    const response = await getSetmealPage(params)
    if (response.code === 200) {
      setmealList.value = response.data.records
      total.value = response.data.total
    } else {
      ElMessage.error(response.message || '查询失败')
    }
  } catch (error) {
    console.error('查询套餐列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadSetmealList()
}

// 重置
const handleReset = () => {
  searchName.value = ''
  searchCategoryId.value = ''
  searchStatus.value = ''
  currentPage.value = 1
  loadSetmealList()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的套餐')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 个套餐吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const ids = selectedRows.value.map(row => row.id)
    const response = await deleteSetmealBatch(ids)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadSetmealList()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除套餐失败:', error)
    }
  }
}

// 添加套餐
const handleAdd = () => {
  router.push('/setmeal/edit')
}

// 编辑套餐
const handleEdit = (row) => {
  router.push(`/setmeal/edit?id=${row.id}`)
}

// 切换状态
const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '起售' : '停售'
  
  try {
    await ElMessageBox.confirm(`确定要${statusText}该套餐吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await updateSetmealStatus(row.id, newStatus)
    if (response.code === 200) {
      ElMessage.success(`${statusText}成功`)
      loadSetmealList()
    } else {
      ElMessage.error(response.message || `${statusText}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新状态失败:', error)
    }
  }
}

// 删除套餐
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该套餐吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await deleteSetmeal(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadSetmealList()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除套餐失败:', error)
    }
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadCategoryList()
  loadSetmealList()
})
</script>

<style scoped>
.setmeal-container {
  padding: 20px;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>
