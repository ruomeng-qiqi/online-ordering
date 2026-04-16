<template>
  <div class="order-container">
    <el-card>
      <!-- 搜索栏 -->
      <div class="search-bar">
        <span style="margin-right: 10px; font-size: 14px; white-space: nowrap;">订单号:</span>
        <el-input 
          v-model="searchOrderNumber" 
          placeholder="请输入订单号" 
          style="width: 180px; margin-right: 15px;"
          clearable
        />
        <span style="margin-right: 10px; font-size: 14px; white-space: nowrap;">顾客昵称:</span>
        <el-input 
          v-model="searchCustomerName" 
          placeholder="请输入顾客昵称" 
          style="width: 150px; margin-right: 15px;"
          clearable
        />
        <span style="margin-right: 10px; font-size: 14px; white-space: nowrap;">餐台号:</span>
        <el-input 
          v-model="searchTableNumber" 
          placeholder="请输入餐台号" 
          style="width: 120px; margin-right: 15px;"
          clearable
        />
        <span style="margin-right: 10px; font-size: 14px; white-space: nowrap;">订单状态:</span>
        <el-select 
          v-model="searchOrderStatus" 
          placeholder="请选择" 
          style="width: 120px; margin-right: 15px;"
          clearable
        >
          <el-option label="待支付" :value="1" />
          <el-option label="已完成" :value="2" />
          <el-option label="已取消" :value="3" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <!-- 订单列表 -->
      <el-table 
        :data="orderList" 
        style="width: 100%; margin-top: 20px;"
        v-loading="loading"
      >
        <el-table-column prop="orderNumber" label="订单号" min-width="180" align="center" />
        <el-table-column label="顾客昵称" min-width="100" align="center">
          <template #default="{ row }">
            {{ row.customerName }}
          </template>
        </el-table-column>
        <el-table-column prop="tableNumber" label="餐台号" min-width="80" align="center" />
        <el-table-column prop="totalAmount" label="订单金额" min-width="100" align="center">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="actualAmount" label="实付金额" min-width="100" align="center">
          <template #default="{ row }">
            <span v-if="row.orderStatus === 2" style="color: #67c23a; font-weight: bold;">¥{{ row.actualAmount }}</span>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="订单状态" min-width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="getOrderStatusType(row.orderStatus)" size="small">
              {{ getOrderStatusText(row.orderStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" min-width="160" align="center" />
        <el-table-column label="结账时间" min-width="160" align="center">
          <template #default="{ row }">
            <span v-if="row.checkoutTime">{{ row.checkoutTime }}</span>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button 
              link 
              type="primary" 
              @click="handleEdit(row)"
            >
              {{ row.orderStatus === 1 ? '编辑' : '查看' }}
            </el-button>
            <el-button 
              v-if="row.orderStatus === 1" 
              link 
              type="success" 
              @click="handleCompleteOrder(row)"
            >
              完成
            </el-button>
            <el-button 
              v-else
              link 
              type="danger" 
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无订单数据" />
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
import { pageQueryOrder, completeOrder, deleteOrder } from '@/api/order'

const router = useRouter()

// 搜索条件
const searchOrderNumber = ref('')
const searchCustomerName = ref('')
const searchTableNumber = ref('')
const searchOrderStatus = ref('')

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 订单列表
const orderList = ref([])

// 加载状态
const loading = ref(false)

// 获取订单列表
const getOrderList = async () => {
  loading.value = true
  try {
    const params = {
      orderNumber: searchOrderNumber.value || undefined,
      customerNickname: searchCustomerName.value || undefined,
      tableNumber: searchTableNumber.value || undefined,
      orderStatus: searchOrderStatus.value || undefined,
      page: currentPage.value,
      pageSize: pageSize.value
    }
    const res = await pageQueryOrder(params)
    if (res.code === 200) {
      orderList.value = res.data.records.map(item => ({
        ...item,
        createTime: item.createTime?.replace('T', ' ') || '',
        checkoutTime: item.checkoutTime?.replace('T', ' ') || null
      }))
      total.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

// 获取订单状态文本
const getOrderStatusText = (status) => {
  const statusMap = {
    1: '待支付',
    2: '已完成',
    3: '已取消'
  }
  return statusMap[status] || '未知'
}

// 获取订单状态标签类型
const getOrderStatusType = (status) => {
  const typeMap = {
    1: 'warning',
    2: 'success',
    3: 'info'
  }
  return typeMap[status] || 'info'
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getOrderList()
}

// 重置
const handleReset = () => {
  searchOrderNumber.value = ''
  searchCustomerName.value = ''
  searchTableNumber.value = ''
  searchOrderStatus.value = ''
  currentPage.value = 1
  getOrderList()
}

// 分页变化
const handlePageChange = (page) => {
  currentPage.value = page
  getOrderList()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  getOrderList()
}

// 编辑订单
const handleEdit = (row) => {
  router.push(`/order/detail/${row.id}`)
}

// 完成订单
const handleCompleteOrder = (row) => {
  ElMessageBox.confirm('确定要完成该订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await completeOrder(row.id)
      if (res.code === 200) {
        ElMessage.success('订单已完成')
        getOrderList()
      }
    } catch (error) {
      ElMessage.error('完成订单失败')
    }
  }).catch(() => {})
}

// 删除订单
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该订单吗？删除后将无法恢复！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteOrder(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getOrderList()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 页面加载时获取数据
onMounted(() => {
  getOrderList()
})
</script>

<style scoped>
.order-container {
  padding: 20px;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
</style>
