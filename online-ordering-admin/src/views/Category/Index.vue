<template>
  <div class="category-container">
    <el-card>
      <!-- 搜索栏 -->
      <div class="search-bar">
        <span style="margin-right: 10px; font-size: 14px; white-space: nowrap;">分类名称:</span>
        <el-input 
          v-model="searchName" 
          placeholder="请输入分类名称" 
          style="width: 150px; margin-right: 15px;"
          clearable
        />
        <span style="margin-right: 10px; font-size: 14px; white-space: nowrap;">分类类型:</span>
        <el-select 
          v-model="searchType" 
          placeholder="请选择" 
          style="width: 150px; margin-right: 15px;"
          clearable
        >
          <el-option label="菜品分类" value="1" />
          <el-option label="套餐分类" value="2" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
        
        <div style="flex: 1"></div>
        
        <el-button @click="handleAdd(1)">
          + 新增菜品分类
        </el-button>
        <el-button type="primary" @click="handleAdd(2)">
          + 新增套餐分类
        </el-button>
      </div>

      <el-table 
        :data="categoryList" 
        v-loading="loading"
        style="width: 100%; margin-top: 20px;"
      >
        <el-table-column prop="name" label="分类名称" min-width="150" />
        <el-table-column prop="type" label="分类类型" min-width="120">
          <template #default="{ row }">
            {{ row.type === 1 ? '菜品分类' : '套餐分类' }}
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" min-width="100" align="center" />
        <el-table-column prop="status" label="状态" min-width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
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
              {{ row.status === 1 ? '禁用' : '启用' }}
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
        @size-change="loadCategoryList"
        @current-change="loadCategoryList"
      />
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle"
      width="500px"
    >
      <el-form :model="formData" label-width="100px">
        <el-form-item label="分类名称">
          <el-input v-model="formData.name" placeholder="请输入分类名称" style="width: 300px;" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input v-model="formData.sort" placeholder="请输入排序" style="width: 300px;" />
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCategoryPage, addCategory, updateCategory, deleteCategory, updateCategoryStatus } from '@/api/category'

const searchName = ref('')
const searchType = ref('')
const dialogVisible = ref(false)
const dialogTitle = ref('添加分类')
const loading = ref(false)
const formData = ref({
  id: null,
  name: '',
  type: 1,
  sort: ''
})

// 格式化时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return dateTime.replace('T', ' ')
}

// 分页数据
const categoryList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 加载分类列表
const loadCategoryList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    }
    
    if (searchName.value) {
      params.name = searchName.value
    }
    
    if (searchType.value) {
      params.type = parseInt(searchType.value)
    }
    
    const response = await getCategoryPage(params)
    if (response.code === 200) {
      categoryList.value = response.data.records
      total.value = response.data.total
    } else {
      ElMessage.error(response.message || '查询失败')
    }
  } catch (error) {
    console.error('查询分类列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadCategoryList()
}

// 重置
const handleReset = () => {
  searchName.value = ''
  searchType.value = ''
  currentPage.value = 1
  loadCategoryList()
}

// 添加分类
const handleAdd = (type) => {
  dialogTitle.value = type === 1 ? '新增菜品分类' : '新增套餐分类'
  formData.value = {
    id: null,
    name: '',
    type: type,
    sort: ''
  }
  dialogVisible.value = true
}

// 编辑分类
const handleEdit = (row) => {
  dialogTitle.value = row.type === 1 ? '修改菜品分类' : '修改套餐分类'
  formData.value = {
    id: row.id,
    name: row.name,
    type: row.type,
    sort: row.sort
  }
  dialogVisible.value = true
}

// 切换状态
const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '启用' : '禁用'
  
  try {
    await ElMessageBox.confirm(`确定要${statusText}该分类吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await updateCategoryStatus(row.id, newStatus)
    if (response.code === 200) {
      ElMessage.success(`${statusText}成功`)
      loadCategoryList()
    } else {
      ElMessage.error(response.message || `${statusText}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新状态失败:', error)
    }
  }
}

// 删除分类
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该分类吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await deleteCategory(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadCategoryList()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除分类失败:', error)
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formData.value.name) {
    ElMessage.warning('请输入分类名称')
    return
  }
  
  if (!formData.value.sort) {
    ElMessage.warning('请输入排序')
    return
  }

  try {
    let response
    if (formData.value.id) {
      // 编辑
      response = await updateCategory(formData.value)
    } else {
      // 添加
      response = await addCategory(formData.value)
    }
    
    if (response.code === 200) {
      ElMessage.success(formData.value.id ? '修改成功' : '添加成功')
      dialogVisible.value = false
      loadCategoryList()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadCategoryList()
})
</script>

<style scoped>
.category-container {
  padding: 20px;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>
