<template>
  <el-dialog 
    v-model="dialogVisible" 
    title="顾客详情"
    width="700px"
    @close="handleClose"
  >
    <div class="detail-container">
      <div class="detail-header">
        <el-avatar :src="customer.avatar" :size="80">
          <img src="https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png" />
        </el-avatar>
        <div class="detail-info">
          <h3>{{ customer.nickname }}</h3>
          <div class="detail-tags">
            <el-tag :type="getStatusType(customer.status)" size="small">
              {{ getStatusText(customer.status) }}
            </el-tag>
            <el-tag v-if="customer.isMember" type="success" size="small" style="margin-left: 10px;">
              会员
            </el-tag>
          </div>
        </div>
      </div>
      
      <el-divider />
      
      <el-tabs v-model="activeTab">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <div class="detail-content">
            <div class="detail-row">
              <span class="detail-label">性别:</span>
              <span class="detail-value">{{ getGenderText(customer.gender) }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">会员状态:</span>
              <span class="detail-value">{{ customer.isMember ? '是' : '否' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">账号状态:</span>
              <span class="detail-value">{{ getStatusText(customer.status) }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">注册时间:</span>
              <span class="detail-value">{{ customer.createTime }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">更新时间:</span>
              <span class="detail-value">{{ customer.updateTime }}</span>
            </div>
          </div>
        </el-tab-pane>
        
        <!-- 积分信息（仅会员显示） -->
        <el-tab-pane v-if="customer.isMember" label="积分信息" name="points">
          <div class="points-info">
            <div class="points-summary">
              <div class="points-card current-points">
                <div class="points-label">当前积分</div>
                <div class="points-value">{{ customer.points || 0 }}</div>
              </div>
              <div class="points-card total-points">
                <div class="points-label">累计积分</div>
                <div class="points-value">{{ customer.totalPoints || 0 }}</div>
              </div>
            </div>
            
            <el-divider />
            
            <div class="points-actions">
              <el-button type="primary" @click="handleAdjustPoints">调整积分</el-button>
            </div>
            
            <div class="points-records">
              <h4>积分记录</h4>
              <el-table 
                :data="pointsRecords" 
                style="width: 100%; margin-top: 10px;"
                max-height="200"
              >
                <el-table-column prop="type" label="类型" width="100" align="center">
                  <template #default="{ row }">
                    <el-tag :type="getPointsTypeTag(row.type)" size="small">
                      {{ getPointsTypeText(row.type) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="points" label="积分变动" width="100" align="center">
                  <template #default="{ row }">
                    <span :style="{ color: row.points > 0 ? '#67c23a' : '#f56c6c', fontWeight: 'bold' }">
                      {{ row.points > 0 ? '+' : '' }}{{ row.points }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
                <el-table-column prop="createTime" label="时间" width="160" align="center" />
                <template #empty>
                  <el-empty description="暂无积分记录" :image-size="80" />
                </template>
              </el-table>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
    </template>

    <!-- 积分调整对话框 -->
    <el-dialog 
      v-model="adjustPointsDialogVisible" 
      title="调整积分"
      width="400px"
      append-to-body
    >
      <el-form :model="adjustPointsForm" label-width="80px">
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
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  customer: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:visible', 'refresh'])

const dialogVisible = ref(false)
const activeTab = ref('basic')
const adjustPointsDialogVisible = ref(false)
const adjustPointsForm = ref({
  type: 1,
  points: 0,
  remark: ''
})

// 模拟积分记录数据
const pointsRecords = ref([
  { id: 1, type: 1, points: 100, remark: '订单消费获得', createTime: '2026-04-10 15:20' },
  { id: 2, type: 2, points: -50, remark: '积分抵扣', createTime: '2026-04-09 12:30' },
  { id: 3, type: 3, points: 200, remark: '管理员手动调整', createTime: '2026-04-08 10:15' },
  { id: 4, type: 1, points: 80, remark: '订单消费获得', createTime: '2026-04-05 18:45' }
])

watch(() => props.visible, (val) => {
  dialogVisible.value = val
  if (val) {
    activeTab.value = 'basic'
  }
})

watch(dialogVisible, (val) => {
  emit('update:visible', val)
})

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
const handleConfirmAdjustPoints = () => {
  if (!adjustPointsForm.value.points || adjustPointsForm.value.points <= 0) {
    ElMessage.warning('请输入有效的积分数量')
    return
  }
  
  const pointsChange = adjustPointsForm.value.type === 1 
    ? adjustPointsForm.value.points 
    : -adjustPointsForm.value.points
  
  // 检查减少积分时是否超过当前积分
  if (pointsChange < 0 && Math.abs(pointsChange) > props.customer.points) {
    ElMessage.warning('减少的积分不能超过当前积分')
    return
  }
  
  // 更新顾客积分
  props.customer.points += pointsChange
  if (pointsChange > 0) {
    props.customer.totalPoints += pointsChange
  }
  
  // 添加积分记录
  const newRecord = {
    id: pointsRecords.value.length + 1,
    type: 3,
    points: pointsChange,
    remark: adjustPointsForm.value.remark || '管理员手动调整',
    createTime: '2026-04-13 ' + new Date().toTimeString().slice(0, 5)
  }
  pointsRecords.value.unshift(newRecord)
  
  ElMessage.success('积分调整成功')
  adjustPointsDialogVisible.value = false
  emit('refresh')
}

const handleClose = () => {
  dialogVisible.value = false
}
</script>

<style scoped>
.detail-container {
  padding: 20px;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 20px;
}

.detail-info h3 {
  margin: 0 0 10px 0;
  font-size: 18px;
}

.detail-tags {
  display: flex;
  align-items: center;
}

.detail-content {
  padding: 10px 0;
}

.detail-row {
  display: flex;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-label {
  width: 120px;
  color: #909399;
  font-size: 14px;
}

.detail-value {
  flex: 1;
  color: #606266;
  font-size: 14px;
}

.points-info {
  padding: 10px 0;
}

.points-summary {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-bottom: 20px;
}

.points-card {
  flex: 1;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  color: white;
}

.points-card.current-points {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}

.points-card.total-points {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.points-label {
  font-size: 14px;
  margin-bottom: 10px;
  opacity: 0.9;
}

.points-value {
  font-size: 32px;
  font-weight: bold;
}

.points-actions {
  text-align: center;
  margin-bottom: 20px;
}

.points-records h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  color: #303133;
}
</style>
