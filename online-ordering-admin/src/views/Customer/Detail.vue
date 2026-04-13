<template>
  <div class="customer-detail-container">
    <el-card class="detail-card">
      <!-- 顶部：返回按钮和顾客信息 -->
      <div class="header-section">
        <el-button @click="handleBack" style="margin-bottom: 15px;">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>

        <div class="customer-header">
          <el-avatar :src="customer.avatar" :size="80">
            <img src="https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png" />
          </el-avatar>
          <div class="customer-info">
            <h2>{{ customer.nickname }}</h2>
            <div class="customer-tags">
              <el-tag :type="getStatusType(customer.status)" size="large">
                {{ getStatusText(customer.status) }}
              </el-tag>
              <el-tag v-if="customer.isMember" type="success" size="large" style="margin-left: 10px;">
                会员
              </el-tag>
            </div>
          </div>
        </div>
      </div>

      <el-divider style="margin: 15px 0;" />

      <!-- 积分信息（仅会员显示） -->
      <div v-if="customer.isMember" class="points-content">
        <!-- 积分信息和操作按钮 -->
        <div class="points-header">
          <div class="points-info">
            <span class="points-item">
              <span class="points-label">当前积分：</span>
              <span class="points-value current">{{ customer.points || 0 }}</span>
            </span>
            <span class="points-item">
              <span class="points-label">累计积分：</span>
              <span class="points-value total">{{ customer.totalPoints || 0 }}</span>
            </span>
          </div>
          <el-button type="primary" @click="handleAdjustPoints">
            调整积分
          </el-button>
        </div>

        <!-- 积分记录 -->
        <el-card class="points-records-card">
          <template #header>
            <span style="font-weight: bold;">积分记录</span>
          </template>
          <el-table :data="pointsRecords" style="width: 100%;" height="100%">
            <el-table-column prop="type" label="类型" width="110" align="center">
              <template #default="{ row }">
                <el-tag :type="getPointsTypeTag(row.type)" size="small">
                  {{ getPointsTypeText(row.type) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="points" label="积分变动" width="110" align="center">
              <template #default="{ row }">
                <span :style="{ color: row.points > 0 ? '#67c23a' : '#f56c6c', fontWeight: 'bold', fontSize: '15px' }">
                  {{ row.points > 0 ? '+' : '' }}{{ row.points }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
            <el-table-column prop="createTime" label="时间" width="160" align="center" />
            <template #empty>
              <el-empty description="暂无积分记录" :image-size="100" />
            </template>
          </el-table>
        </el-card>
      </div>
      
      <!-- 非会员提示 -->
      <div v-else class="non-member-tip">
        <el-empty description="该顾客不是会员，无积分信息" :image-size="120" />
      </div>
    </el-card>

    <!-- 积分调整对话框 -->
    <el-dialog 
      v-model="adjustPointsDialogVisible" 
      title="调整积分"
      width="450px"
    >
      <el-form :model="adjustPointsForm" label-width="90px">
        <el-form-item label="调整类型">
          <el-radio-group v-model="adjustPointsForm.type">
            <el-radio :label="1">增加</el-radio>
            <el-radio :label="2">减少</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="积分数量">
          <el-input-number 
            v-model="adjustPointsForm.points" 
            :min="1" 
            :max="10000"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input 
            v-model="adjustPointsForm.remark" 
            type="textarea" 
            :rows="3"
            placeholder="请输入调整原因"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustPointsDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmAdjustPoints">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getCustomerById, adjustPoints, getPointsRecords } from '@/api/customer'

const router = useRouter()
const route = useRoute()
const adjustPointsDialogVisible = ref(false)
const adjustPointsForm = ref({
  type: 1,
  points: 0,
  remark: ''
})

// 顾客数据
const customer = ref({
  id: null,
  nickname: '',
  avatar: '',
  gender: 0,
  isMember: 0,
  points: 0,
  totalPoints: 0,
  status: 1,
  createTime: '',
  updateTime: ''
})

// 积分记录数据
const pointsRecords = ref([])

onMounted(() => {
  const customerId = route.params.id
  if (customerId) {
    loadCustomerDetail(customerId)
    loadPointsRecords(customerId)
  }
})

// 加载顾客详情
const loadCustomerDetail = async (id) => {
  try {
    const res = await getCustomerById(id)
    // 格式化时间
    customer.value = {
      ...res.data,
      createTime: res.data.createTime ? res.data.createTime.replace('T', ' ') : '',
      updateTime: res.data.updateTime ? res.data.updateTime.replace('T', ' ') : ''
    }
  } catch (error) {
    console.error('加载顾客详情失败：', error)
    ElMessage.error('加载顾客详情失败')
  }
}

// 加载积分记录
const loadPointsRecords = async (customerId) => {
  try {
    const res = await getPointsRecords(customerId)
    // 格式化时间
    pointsRecords.value = res.data.map(item => ({
      ...item,
      createTime: item.createTime ? item.createTime.replace('T', ' ') : ''
    }))
  } catch (error) {
    console.error('加载积分记录失败：', error)
  }
}

// 返回
const handleBack = () => {
  router.back()
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

// 获取积分类型文本
const getPointsTypeText = (type) => {
  const typeMap = {
    1: '订单获得',
    2: '积分抵扣',
    3: '手动调整'
  }
  return typeMap[type] || '未知'
}

// 获取积分类型标签
const getPointsTypeTag = (type) => {
  const tagMap = {
    1: 'success',
    2: 'warning',
    3: 'info'
  }
  return tagMap[type] || 'info'
}

// 调整积分
const handleAdjustPoints = () => {
  adjustPointsForm.value = {
    type: 1,
    points: 0,
    remark: ''
  }
  adjustPointsDialogVisible.value = true
}

// 确认调整积分
const handleConfirmAdjustPoints = async () => {
  if (!adjustPointsForm.value.points || adjustPointsForm.value.points <= 0) {
    ElMessage.warning('请输入有效的积分数量')
    return
  }
  
  const pointsChange = adjustPointsForm.value.type === 1 
    ? adjustPointsForm.value.points 
    : -adjustPointsForm.value.points
  
  // 检查减少积分时是否超过当前积分
  if (pointsChange < 0 && Math.abs(pointsChange) > customer.value.points) {
    ElMessage.warning('减少的积分不能超过当前积分')
    return
  }
  
  try {
    await adjustPoints({
      customerId: customer.value.id,
      type: adjustPointsForm.value.type,
      points: adjustPointsForm.value.points,
      remark: adjustPointsForm.value.remark || undefined
    })
    
    ElMessage.success('积分调整成功')
    adjustPointsDialogVisible.value = false
    
    // 重新加载数据
    loadCustomerDetail(customer.value.id)
    loadPointsRecords(customer.value.id)
  } catch (error) {
    console.error('调整积分失败：', error)
  }
}
</script>

<style scoped>
.customer-detail-container {
  padding: 20px;
  height: calc(100vh - 100px);
  overflow: hidden;
}

.detail-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.detail-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header-section {
  flex-shrink: 0;
}

.customer-header {
  display: flex;
  align-items: center;
  gap: 20px;
}

.customer-info h2 {
  margin: 0 0 10px 0;
  font-size: 22px;
  color: #303133;
}

.customer-tags {
  display: flex;
  align-items: center;
  gap: 10px;
}

.points-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow: hidden;
}

.non-member-tip {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.points-header {
  flex-shrink: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.points-info {
  display: flex;
  gap: 40px;
  align-items: center;
}

.points-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.points-label {
  font-size: 14px;
  color: #606266;
}

.points-value {
  font-size: 20px;
  font-weight: bold;
}

.points-value.current {
  color: #409eff;
}

.points-value.total {
  color: #67c23a;
}

.points-records-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.points-records-card :deep(.el-card__header) {
  flex-shrink: 0;
}

.points-records-card :deep(.el-card__body) {
  flex: 1;
  padding: 0;
  overflow: hidden;
}
</style>
