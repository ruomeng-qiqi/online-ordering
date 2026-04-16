<template>
  <div class="order-edit-container" v-loading="loading">
    <el-card class="edit-card">
      <!-- 顶部：返回按钮和订单信息 -->
      <div class="header-section">
        <el-button @click="handleBack" style="margin-bottom: 15px;">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>

        <div class="order-header">
          <div>
            <h2 style="margin: 0 0 10px 0;">编辑订单：{{ order.orderNumber }}</h2>
            <div class="order-info">
              <span>顾客：{{ order.customerName }}</span>
              <span style="margin-left: 20px;">餐台：{{ order.tableNumber }}</span>
            </div>
          </div>
        </div>
      </div>

      <el-divider style="margin: 15px 0;" />

      <!-- 订单明细编辑 -->
      <el-card shadow="hover" style="margin-bottom: 20px;">
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span style="font-weight: bold;">订单明细</span>
            <el-button type="primary" size="small" @click="handleAddItem">
              添加菜品/套餐
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
          <el-table-column label="类型" width="80" align="center">
            <template #default="{ row }">
              <el-tag :type="row.setmealId ? 'success' : 'primary'" size="small">
                {{ row.setmealId ? '套餐' : '菜品' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="price" label="单价" width="120" align="center">
            <template #default="{ row }">
              ¥{{ row.price.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column label="数量" width="150" align="center">
            <template #default="{ row }">
              <el-input-number 
                v-model="row.quantity" 
                :min="1" 
                :max="99"
                size="small"
                @change="recalculateAmounts"
              />
            </template>
          </el-table-column>
          <el-table-column label="小计" width="120" align="center">
            <template #default="{ row }">
              <span style="color: #f56c6c; font-weight: bold;">¥{{ row.amount.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" align="center">
            <template #default="{ row, $index }">
              <el-button link type="danger" @click="handleDeleteDetail($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-empty v-if="orderDetails.length === 0" description="暂无订单明细，请添加菜品或套餐" />
      </el-card>

      <!-- 金额信息 -->
      <el-card shadow="hover" style="margin-bottom: 20px;">
        <template #header>
          <span style="font-weight: bold;">金额信息</span>
        </template>
        
        <el-form label-width="120px">
          <el-form-item label="订单总额">
            <span style="font-size: 18px; font-weight: bold;">¥{{ calculatedTotal.toFixed(2) }}</span>
          </el-form-item>
          <el-form-item label="优惠金额">
            <el-input-number 
              v-model="order.discountAmount" 
              :min="0" 
              :max="calculatedTotal"
              :precision="2"
              :step="1"
              style="width: 200px;"
            />
          </el-form-item>
          <el-form-item label="积分抵扣" v-if="order.pointsDeduction > 0">
            <span>¥{{ order.pointsDeduction.toFixed(2) }} ({{ order.pointsUsed }}积分)</span>
          </el-form-item>
          <el-form-item label="实付金额">
            <span style="font-size: 20px; font-weight: bold; color: #f56c6c;">
              ¥{{ calculatedActualAmount.toFixed(2) }}
            </span>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 操作按钮 -->
      <div style="text-align: right;">
        <el-button type="primary" @click="handleSave" size="large">保存订单</el-button>
      </div>
    </el-card>

    <!-- 添加菜品/套餐对话框 -->
    <el-dialog 
      v-model="addItemDialogVisible" 
      title="添加菜品/套餐"
      width="900px"
      :close-on-click-modal="false"
    >
      <el-tabs v-model="activeTab">
        <!-- 菜品选择 -->
        <el-tab-pane label="菜品" name="dish">
          <div class="search-bar" style="margin-bottom: 15px;">
            <el-input 
              v-model="searchDishName" 
              placeholder="搜索菜品名称" 
              style="width: 200px; margin-right: 10px;"
              clearable
            />
            <el-select 
              v-model="searchDishCategory" 
              placeholder="选择分类" 
              style="width: 150px; margin-right: 10px;"
              clearable
            >
              <el-option 
                v-for="category in dishCategoryList" 
                :key="category.id" 
                :label="category.name" 
                :value="category.id" 
              />
            </el-select>
            <el-button type="primary" @click="handleSearchDish">搜索</el-button>
          </div>

          <el-table 
            :data="dishList" 
            style="width: 100%;" 
            max-height="400"
            v-loading="dishLoading"
            @selection-change="handleDishSelectionChange"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column label="图片" width="80" align="center">
              <template #default="{ row }">
                <el-image 
                  :src="row.image" 
                  style="width: 50px; height: 50px; border-radius: 4px;"
                  fit="cover"
                />
              </template>
            </el-table-column>
            <el-table-column prop="name" label="菜品名称" min-width="150" />
            <el-table-column prop="category" label="分类" width="100" />
            <el-table-column prop="price" label="价格" width="100" align="center">
              <template #default="{ row }">
                ¥{{ row.price }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 1 ? '起售' : '停售' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 套餐选择 -->
        <el-tab-pane label="套餐" name="setmeal">
          <div class="search-bar" style="margin-bottom: 15px;">
            <el-input 
              v-model="searchSetmealName" 
              placeholder="搜索套餐名称" 
              style="width: 200px; margin-right: 10px;"
              clearable
            />
            <el-select 
              v-model="searchSetmealCategory" 
              placeholder="选择分类" 
              style="width: 150px; margin-right: 10px;"
              clearable
            >
              <el-option 
                v-for="category in setmealCategoryList" 
                :key="category.id" 
                :label="category.name" 
                :value="category.id" 
              />
            </el-select>
            <el-button type="primary" @click="handleSearchSetmeal">搜索</el-button>
          </div>

          <el-table 
            :data="setmealList" 
            style="width: 100%;" 
            max-height="400"
            v-loading="setmealLoading"
            @selection-change="handleSetmealSelectionChange"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column label="图片" width="80" align="center">
              <template #default="{ row }">
                <el-image 
                  :src="row.image" 
                  style="width: 50px; height: 50px; border-radius: 4px;"
                  fit="cover"
                />
              </template>
            </el-table-column>
            <el-table-column prop="name" label="套餐名称" min-width="150" />
            <el-table-column prop="price" label="价格" width="100" align="center">
              <template #default="{ row }">
                ¥{{ row.price }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 1 ? '起售' : '停售' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>

      <template #footer>
        <el-button @click="addItemDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmAdd">确定添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Picture } from '@element-plus/icons-vue'
import { getOrderById, updateOrder } from '@/api/order'
import { getDishPage } from '@/api/dish'
import { getSetmealPage } from '@/api/setmeal'
import { getCategoryPage } from '@/api/category'

const router = useRouter()
const route = useRoute()

const loading = ref(false)

// 订单信息
const order = ref({
  id: null,
  orderNumber: '',
  customerName: '',
  tableNumber: '',
  discountAmount: 0,
  pointsDeduction: 0,
  pointsUsed: 0
})

// 订单明细
const orderDetails = ref([])

// 添加菜品/套餐对话框
const addItemDialogVisible = ref(false)
const activeTab = ref('dish')
const searchDishName = ref('')
const searchDishCategory = ref('')
const searchSetmealName = ref('')
const searchSetmealCategory = ref('')
const selectedDishes = ref([])
const selectedSetmeals = ref([])

// 菜品和套餐列表
const dishList = ref([])
const setmealList = ref([])
const dishCategoryList = ref([])
const setmealCategoryList = ref([])
const dishLoading = ref(false)
const setmealLoading = ref(false)

// 计算订单总额
const calculatedTotal = computed(() => {
  return orderDetails.value.reduce((sum, item) => sum + item.amount, 0)
})

// 计算实付金额
const calculatedActualAmount = computed(() => {
  return calculatedTotal.value - order.value.discountAmount - order.value.pointsDeduction
})

onMounted(() => {
  const orderId = route.params.id
  if (orderId) {
    loadOrderData(orderId)
  }
})

// 加载订单数据
const loadOrderData = async (id) => {
  loading.value = true
  try {
    const res = await getOrderById(id)
    if (res.code === 200) {
      const data = res.data
      order.value = {
        id: data.id,
        orderNumber: data.orderNumber,
        customerName: data.customerName,
        tableNumber: data.tableNumber,
        discountAmount: data.discountAmount || 0,
        pointsDeduction: data.pointsDeduction || 0,
        pointsUsed: data.pointsUsed || 0
      }
      
      // 转换订单明细格式
      orderDetails.value = (data.orderDetails || []).map(item => ({
        id: item.id,
        dishId: item.dishId,
        setmealId: item.setmealId,
        name: item.name,
        image: item.image,
        quantity: item.quantity,
        price: item.price,
        amount: item.amount,
        flavor: item.flavor
      }))
    }
  } catch (error) {
    ElMessage.error('获取订单详情失败')
    router.back()
  } finally {
    loading.value = false
  }
}

// 加载菜品分类列表
const loadDishCategoryList = async () => {
  try {
    const res = await getCategoryPage({
      type: 1, // 1-菜品分类
      status: 1, // 只查询启用的分类
      page: 1,
      pageSize: 100
    })
    if (res.code === 200) {
      dishCategoryList.value = res.data.records
    }
  } catch (error) {
    console.error('获取菜品分类列表失败', error)
  }
}

// 加载套餐分类列表
const loadSetmealCategoryList = async () => {
  try {
    const res = await getCategoryPage({
      type: 2, // 2-套餐分类
      status: 1, // 只查询启用的分类
      page: 1,
      pageSize: 100
    })
    if (res.code === 200) {
      setmealCategoryList.value = res.data.records
    }
  } catch (error) {
    console.error('获取套餐分类列表失败', error)
  }
}

// 加载菜品列表
const loadDishList = async () => {
  dishLoading.value = true
  try {
    const res = await getDishPage({
      name: searchDishName.value || undefined,
      categoryId: searchDishCategory.value || undefined,
      status: 1, // 只查询起售的菜品
      page: 1,
      pageSize: 100
    })
    if (res.code === 200) {
      dishList.value = res.data.records
    }
  } catch (error) {
    ElMessage.error('获取菜品列表失败')
  } finally {
    dishLoading.value = false
  }
}

// 加载套餐列表
const loadSetmealList = async () => {
  setmealLoading.value = true
  try {
    const res = await getSetmealPage({
      name: searchSetmealName.value || undefined,
      categoryId: searchSetmealCategory.value || undefined,
      status: 1, // 只查询起售的套餐
      page: 1,
      pageSize: 100
    })
    if (res.code === 200) {
      setmealList.value = res.data.records
    }
  } catch (error) {
    ElMessage.error('获取套餐列表失败')
  } finally {
    setmealLoading.value = false
  }
}

// 返回
const handleBack = () => {
  router.back()
}

// 重新计算金额
const recalculateAmounts = () => {
  orderDetails.value.forEach(item => {
    item.amount = item.price * item.quantity
  })
}

// 删除明细
const handleDeleteDetail = (index) => {
  ElMessageBox.confirm('确定要删除该项吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    orderDetails.value.splice(index, 1)
    ElMessage.success('删除成功')
  }).catch(() => {})
}

// 打开添加菜品/套餐对话框
const handleAddItem = () => {
  selectedDishes.value = []
  selectedSetmeals.value = []
  activeTab.value = 'dish'
  addItemDialogVisible.value = true
  // 加载分类、菜品和套餐列表
  loadDishCategoryList()
  loadSetmealCategoryList()
  loadDishList()
  loadSetmealList()
}

// 搜索菜品
const handleSearchDish = () => {
  loadDishList()
}

// 搜索套餐
const handleSearchSetmeal = () => {
  loadSetmealList()
}

// 菜品选择变化
const handleDishSelectionChange = (selection) => {
  selectedDishes.value = selection
}

// 套餐选择变化
const handleSetmealSelectionChange = (selection) => {
  selectedSetmeals.value = selection
}

// 确认添加
const handleConfirmAdd = () => {
  if (selectedDishes.value.length === 0 && selectedSetmeals.value.length === 0) {
    ElMessage.warning('请选择要添加的菜品或套餐')
    return
  }

  // 添加选中的菜品
  selectedDishes.value.forEach(dish => {
    orderDetails.value.push({
      id: null, // 新增的明细没有ID
      dishId: dish.id,
      setmealId: null,
      name: dish.name,
      image: dish.image,
      quantity: 1,
      price: dish.price,
      amount: dish.price,
      flavor: null
    })
  })

  // 添加选中的套餐
  selectedSetmeals.value.forEach(setmeal => {
    orderDetails.value.push({
      id: null, // 新增的明细没有ID
      dishId: null,
      setmealId: setmeal.id,
      name: setmeal.name,
      image: setmeal.image,
      quantity: 1,
      price: setmeal.price,
      amount: setmeal.price,
      flavor: null
    })
  })

  ElMessage.success(`成功添加 ${selectedDishes.value.length + selectedSetmeals.value.length} 项`)
  addItemDialogVisible.value = false
}

// 保存订单
const handleSave = async () => {
  if (orderDetails.value.length === 0) {
    ElMessage.warning('订单明细不能为空')
    return
  }

  if (order.value.discountAmount > calculatedTotal.value) {
    ElMessage.warning('优惠金额不能超过订单总额')
    return
  }

  loading.value = true
  try {
    const data = {
      id: order.value.id,
      discountAmount: order.value.discountAmount,
      remark: order.value.remark || '',
      details: orderDetails.value.map(item => ({
        dishId: item.dishId,
        setmealId: item.setmealId,
        name: item.name,
        image: item.image,
        quantity: item.quantity,
        price: item.price,
        flavor: item.flavor
      }))
    }
    
    const res = await updateOrder(data)
    if (res.code === 200) {
      ElMessage.success('订单保存成功')
      setTimeout(() => {
        router.back()
      }, 500)
    }
  } catch (error) {
    ElMessage.error('保存订单失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.order-edit-container {
  padding: 20px;
}

.edit-card {
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

.order-info {
  font-size: 14px;
  color: #606266;
}

.search-bar {
  display: flex;
  align-items: center;
}
</style>
