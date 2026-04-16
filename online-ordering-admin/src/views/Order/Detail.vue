<template>
  <div class="order-detail-container">
    <el-card class="detail-card">
      <!-- 顶部：返回按钮和订单信息 -->
      <div class="header-section">
        <el-button @click="handleBack" style="margin-bottom: 15px;">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>

        <div class="order-header">
          <div>
            <h2 style="margin: 0 0 10px 0;">订单号：{{ order.orderNumber }}</h2>
            <el-tag :type="getOrderStatusType(order.orderStatus)" size="large">
              {{ getOrderStatusText(order.orderStatus) }}
            </el-tag>
          </div>
          <div class="order-actions" v-if="order.orderStatus === 1">
            <el-button type="success" @click="handleCompleteOrder">完成订单</el-button>
            <el-button type="danger" @click="handleCancelOrder">取消订单</el-button>
          </div>
        </div>
      </div>

      <el-divider style="margin: 15px 0;" />

      <!-- 订单信息卡片 -->
      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span style="font-weight: bold;">顾客信息</span>
            </template>
            <div class="info-item" style="justify-content: center;">
              <div style="display: flex; align-items: center; gap: 10px;">
                <span style="font-size: 16px; font-weight: bold;">{{ order.customerName }}</span>
                <span style="color: #909399;">-</span>
                <el-tag :type="order.isMember ? 'success' : 'info'" size="small">
                  {{ order.isMember ? '会员' : '普通顾客' }}
                </el-tag>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span style="font-weight: bold;">餐台信息</span>
            </template>
            <div class="info-item" style="justify-content: center;">
              <div style="display: flex; align-items: center; gap: 10px;">
                <span style="font-size: 16px; font-weight: bold;">{{ order.tableNumber }}</span>
                <span style="color: #909399;">-</span>
                <span style="font-size: 14px; color: #606266;">{{ order.tableName }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col :span="24">
          <el-card shadow="hover">
            <template #header>
              <span style="font-weight: bold;">时间信息</span>
            </template>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="下单时间">{{ order.createTime }}</el-descriptions-item>
              <el-descriptions-item label="结账时间">{{ order.checkoutTime || '-' }}</el-descriptions-item>
              <el-descriptions-item label="支付方式" v-if="order.orderStatus === 2">
                {{ getPaymentMethodText(order.paymentMethod) }}
              </el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-col>
      </el-row>

      <!-- 订单明细卡片 -->
      <el-card shadow="hover" style="margin-bottom: 20px;">
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span style="font-weight: bold;">订单明细</span>
            <el-button type="primary" size="small" @click="handleEditOrder" v-if="order.orderStatus === 1">
              <el-icon><Edit /></el-icon>
              编辑订单
            </el-button>
          </div>
        </template>
        <el-table :data="orderDetails" style="width: 100%;">
          <el-table-column label="图片" width="80" align="center">
            <template #default="{ row }">
              <el-image 
                :src="row.image" 
                style="width: 50px; height: 50px; border-radius: 4px;"
                fit="cover"
              >
                <template #error>
                  <div style="width: 50px; height: 50px; background: #f5f7fa; display: flex; align-items: center; justify-content: center;">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="名称" min-width="150" />
          <el-table-column label="口味" min-width="150">
            <template #default="{ row }">
              <span v-if="row.flavor">{{ formatFlavor(row.flavor) }}</span>
              <span v-else style="color: #909399;">-</span>
            </template>
          </el-table-column>
          <el-table-column prop="price" label="单价" width="100" align="center">
            <template #default="{ row }">
              ¥{{ row.price }}
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" align="center" />
          <el-table-column prop="amount" label="小计" width="100" align="center">
            <template #default="{ row }">
              <span style="color: #f56c6c; font-weight: bold;">¥{{ row.amount.toFixed(2) }}</span>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 金额汇总 -->
        <div class="amount-summary">
          <div class="summary-item">
            <span>订单总额：</span>
            <span class="amount">¥{{ order.totalAmount.toFixed(2) }}</span>
          </div>
          <div class="summary-item" v-if="order.discountAmount > 0">
            <span>优惠金额：</span>
            <span class="amount discount">-¥{{ order.discountAmount.toFixed(2) }}</span>
          </div>
          <div class="summary-item" v-if="order.pointsDeduction > 0">
            <span>积分抵扣：</span>
            <span class="amount discount">-¥{{ order.pointsDeduction.toFixed(2) }} ({{ order.pointsUsed }}积分)</span>
          </div>
          <div class="summary-item total">
            <span>{{ order.orderStatus === 2 ? '实付金额：' : '预计实付：' }}</span>
            <span class="amount">¥{{ calculatedActualAmount.toFixed(2) }}</span>
          </div>
          <div class="summary-item" v-if="order.orderStatus === 2 && order.pointsEarned > 0">
            <span>获得积分：</span>
            <span class="amount" style="color: #67c23a;">+{{ order.pointsEarned }}积分</span>
          </div>
        </div>
      </el-card>

      <!-- 备注信息 -->
      <el-card shadow="hover" v-if="order.remark || order.cancelReason">
        <template #header>
          <span style="font-weight: bold;">备注信息</span>
        </template>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="订单备注" v-if="order.remark">{{ order.remark }}</el-descriptions-item>
          <el-descriptions-item label="取消原因" v-if="order.cancelReason">
            <span style="color: #f56c6c;">{{ order.cancelReason }}</span>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>
    </el-card>

    <!-- 取消订单对话框 -->
    <el-dialog 
      v-model="cancelDialogVisible" 
      title="取消订单"
      width="450px"
    >
      <el-form :model="cancelForm" label-width="90px">
        <el-form-item label="取消原因">
          <el-input 
            v-model="cancelForm.reason" 
            type="textarea" 
            :rows="4"
            placeholder="请输入取消原因"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancelDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmCancel">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Picture, Edit } from '@element-plus/icons-vue'
import { getOrderById, completeOrder, cancelOrder } from '@/api/order'

const router = useRouter()
const route = useRoute()

const cancelDialogVisible = ref(false)
const cancelForm = ref({
  reason: ''
})

const loading = ref(false)

// 订单数据
const order = ref({
  id: null,
  orderNumber: '',
  customerId: null,
  customerName: '',
  isMember: 0,
  tableId: null,
  tableNumber: '',
  tableName: '',
  checkoutTime: null,
  totalAmount: 0,
  actualAmount: 0,
  discountAmount: 0,
  pointsDeduction: 0,
  pointsUsed: 0,
  pointsEarned: 0,
  paymentMethod: null,
  orderStatus: 1,
  remark: '',
  cancelReason: '',
  createTime: ''
})

// 订单明细数据
const orderDetails = ref([])

// 计算实付金额
const calculatedActualAmount = computed(() => {
  // 如果是已完成订单且有实付金额，使用实际金额
  if (order.value.orderStatus === 2 && order.value.actualAmount !== null) {
    return order.value.actualAmount
  }
  // 否则计算预计实付金额
  return order.value.totalAmount - order.value.discountAmount - order.value.pointsDeduction
})

onMounted(() => {
  const orderId = route.params.id
  if (orderId) {
    loadOrderDetail(orderId)
  }
})

// 加载订单详情
const loadOrderDetail = async (id) => {
  loading.value = true
  try {
    const res = await getOrderById(id)
    if (res.code === 200) {
      const data = res.data
      order.value = {
        ...data,
        createTime: data.createTime?.replace('T', ' ') || '',
        checkoutTime: data.checkoutTime?.replace('T', ' ') || null
      }
      orderDetails.value = data.orderDetails || []
    }
  } catch (error) {
    ElMessage.error('获取订单详情失败')
    router.back()
  } finally {
    loading.value = false
  }
}

// 返回
const handleBack = () => {
  router.back()
}

// 格式化口味信息
const formatFlavor = (flavorJson) => {
  if (!flavorJson) return '-'
  try {
    const flavor = JSON.parse(flavorJson)
    return Object.values(flavor).join('，')
  } catch (e) {
    return flavorJson
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

// 获取支付方式文本
const getPaymentMethodText = (method) => {
  const methodMap = {
    1: '在线支付',
    2: '线下支付',
    null: '-'
  }
  return methodMap[method] || '-'
}

// 完成订单
const handleCompleteOrder = () => {
  ElMessageBox.confirm('确定要完成该订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await completeOrder(order.value.id)
      if (res.code === 200) {
        ElMessage.success('订单已完成')
        router.push({ name: 'Order' })
      }
    } catch (error) {
      ElMessage.error('完成订单失败')
    }
  }).catch(() => {})
}

// 取消订单
const handleCancelOrder = () => {
  cancelForm.value.reason = ''
  cancelDialogVisible.value = true
}

// 确认取消订单
const handleConfirmCancel = async () => {
  if (!cancelForm.value.reason) {
    ElMessage.warning('请输入取消原因')
    return
  }
  
  try {
    const res = await cancelOrder({
      id: order.value.id,
      cancelReason: cancelForm.value.reason
    })
    if (res.code === 200) {
      cancelDialogVisible.value = false
      ElMessage.success('订单已取消')
      router.push({ name: 'Order' })
    }
  } catch (error) {
    ElMessage.error('取消订单失败')
  }
}

// 编辑订单 - 跳转到编辑页面
const handleEditOrder = () => {
  router.push({ name: 'OrderEdit', params: { id: order.value.id } })
}
</script>

<style scoped>
.order-detail-container {
  padding: 20px;
}

.detail-card {
  max-width: 1200px;
  margin: 0 auto;
}

.header-section {
  flex-shrink: 0;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-actions {
  display: flex;
  gap: 10px;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
}

.amount-summary {
  margin-top: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 14px;
}

.summary-item.total {
  border-top: 1px solid #dcdfe6;
  margin-top: 10px;
  padding-top: 15px;
  font-size: 16px;
  font-weight: bold;
}

.summary-item .amount {
  font-weight: bold;
  color: #303133;
}

.summary-item .amount.discount {
  color: #67c23a;
}

.summary-item.total .amount {
  font-size: 20px;
  color: #f56c6c;
}
</style>
