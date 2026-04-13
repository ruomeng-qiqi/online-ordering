<template>
  <div class="customer-container">
    <el-card>
      <!-- 搜索栏 -->
      <div class="search-bar">
        <span style="margin-right: 10px; font-size: 14px; white-space: nowrap;">昵称:</span>
        <el-input 
          v-model="searchNickname" 
          placeholder="请输入昵称" 
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
          <el-option label="正常" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <span style="margin-right: 10px; font-size: 14px; white-space: nowrap;">会员:</span>
        <el-select 
          v-model="searchIsMember" 
          placeholder="请选择" 
          style="width: 150px; margin-right: 15px;"
          clearable
        >
          <el-option label="是" :value="1" />
          <el-option label="否" :value="0" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table 
        :data="customerList" 
        style="width: 100%; margin-top: 20px;"
      >
        <el-table-column label="头像" width="80" align="center">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" :size="50">
              <img src="https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png" />
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template #default="{ row }">
            {{ getGenderText(row.gender) }}
          </template>
        </el-table-column>
        <el-table-column prop="isMember" label="会员" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isMember ? 'success' : 'info'" size="small">
              {{ row.isMember ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="当前积分" width="100" align="center">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold;">{{ row.points || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" min-width="160" align="center" />
        <el-table-column prop="updateTime" label="更新时间" min-width="160" align="center" />
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewDetail(row)">查看详情</el-button>
            <el-button 
              link 
              :type="row.status === 1 ? 'danger' : 'success'" 
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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
        :page-sizes="[10, 20, 30, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end;"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageQueryCustomer, updateCustomerStatus, deleteCustomer } from '@/api/customer'

const router = useRouter()

const customerList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const searchNickname = ref('')
const searchStatus = ref('')
const searchIsMember = ref('')

onMounted(() => {
  loadCustomerList()
})

// 加载顾客列表
const loadCustomerList = async () => {
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      nickname: searchNickname.value || undefined,
      status: searchStatus.value !== '' ? searchStatus.value : undefined,
      isMember: searchIsMember.value !== '' ? searchIsMember.value : undefined
    }
    
    const res = await pageQueryCustomer(params)
    // 格式化时间
    customerList.value = res.data.records.map(item => ({
      ...item,
      createTime: item.createTime ? item.createTime.replace('T', ' ') : '',
      updateTime: item.updateTime ? item.updateTime.replace('T', ' ') : ''
    }))
    total.value = res.data.total
  } catch (error) {
    console.error('加载顾客列表失败：', error)
  }
}

// 获取性别文本
const getGenderText = (gender) => {
  const genderMap = {
    0: '未知',
    1: '男',
    2: '女'
  }
  return genderMap[gender] || '未知'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '禁用',
    1: '正常'
  }
  return statusMap[status] || '未知'
}

// 获取状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    0: 'danger',
    1: 'success'
  }
  return typeMap[status] || 'info'
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadCustomerList()
}

// 重置
const handleReset = () => {
  searchNickname.value = ''
  searchStatus.value = ''
  searchIsMember.value = ''
  currentPage.value = 1
  loadCustomerList()
}

// 分页变化
const handlePageChange = (page) => {
  currentPage.value = page
  loadCustomerList()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadCustomerList()
}

// 查看详情
const handleViewDetail = (row) => {
  router.push(`/customer/detail/${row.id}`)
}

// 切换状态
const handleToggleStatus = async (row) => {
  const action = row.status === 1 ? '禁用' : '启用'
  const newStatus = row.status === 1 ? 0 : 1
  
  try {
    await ElMessageBox.confirm(`确定要${action}该顾客吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await updateCustomerStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    loadCustomerList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新状态失败：', error)
    }
  }
}

// 删除顾客
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该顾客吗？删除后将无法恢复！', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteCustomer(row.id)
    ElMessage.success('删除成功')
    loadCustomerList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除顾客失败：', error)
    }
  }
}
</script>

<style scoped>
.customer-container {
  padding: 20px;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>
