<template>
  <el-dialog 
    v-model="visible" 
    title="添加菜品"
    width="900px"
    @open="handleOpen"
  >
    <div style="display: flex; height: 500px;">
      <!-- 左侧分类列表 -->
      <div style="width: 150px; border-right: 1px solid #e4e7ed; overflow-y: auto;">
        <div
          v-for="category in categories"
          :key="category.id"
          :class="['category-item', { active: selectedCategory === category.id }]"
          @click="handleCategoryChange(category.id)"
        >
          {{ category.name }}
        </div>
      </div>

      <!-- 中间菜品列表 -->
      <div style="flex: 1; padding: 0 20px; overflow-y: auto;">
        <el-table
          ref="dishTableRef"
          :data="filteredDishes"
          @selection-change="handleDishSelectionChange"
          height="450"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="name" label="菜品名称" />
          <el-table-column prop="price" label="价格" width="80">
            <template #default="{ row }">
              ¥{{ row.price }}
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 右侧已选菜品 -->
      <div style="width: 200px; border-left: 1px solid #e4e7ed; padding-left: 20px; overflow-y: auto;">
        <div style="font-weight: bold; margin-bottom: 10px;">已选菜品({{ tempSelectedDishes.length }})</div>
        <div
          v-for="dish in tempSelectedDishes"
          :key="dish.id"
          style="padding: 8px 0; border-bottom: 1px solid #f0f0f0;"
        >
          {{ dish.name }}
        </div>
      </div>
    </div>

    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleConfirm">添加</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getDishPage } from '@/api/dish'
import { getCategoryPage } from '@/api/category'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  selectedDishes: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue', 'confirm'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const dishTableRef = ref(null)
const tempSelectedDishes = ref([])
const selectedCategory = ref(null)
const categories = ref([])
const allDishes = ref([])

// 加载分类列表
const loadCategories = async () => {
  try {
    const response = await getCategoryPage({ page: 1, pageSize: 100, type: 1, status: 1 })
    if (response.code === 200) {
      categories.value = response.data.records
      if (categories.value.length > 0) {
        selectedCategory.value = categories.value[0].id
        loadDishes()
      }
    }
  } catch (error) {
    console.error('加载分类列表失败:', error)
  }
}

// 加载菜品列表（只加载起售的菜品）
const loadDishes = async () => {
  try {
    const response = await getDishPage({ 
      page: 1, 
      pageSize: 1000, 
      status: 1  // 只查询起售的菜品
    })
    if (response.code === 200) {
      allDishes.value = response.data.records
    }
  } catch (error) {
    console.error('加载菜品列表失败:', error)
  }
}

// 根据分类过滤菜品
const filteredDishes = computed(() => {
  if (!selectedCategory.value) return []
  return allDishes.value.filter(dish => dish.categoryId === selectedCategory.value)
})

// 对话框打开时
const handleOpen = async () => {
  tempSelectedDishes.value = [...props.selectedDishes]
  
  // 加载数据
  await loadCategories()
  
  // 等待渲染完成后设置选中状态
  nextTick(() => {
    if (dishTableRef.value) {
      // 清除所有选中
      dishTableRef.value.clearSelection()
      // 根据已选菜品设置选中状态
      filteredDishes.value.forEach(dish => {
        const isSelected = props.selectedDishes.some(d => d.id === dish.id)
        if (isSelected) {
          dishTableRef.value.toggleRowSelection(dish, true)
        }
      })
    }
  })
}

// 分类切换时更新选中状态
const handleCategoryChange = (categoryId) => {
  selectedCategory.value = categoryId
  
  // 等待渲染完成后设置选中状态
  nextTick(() => {
    if (dishTableRef.value) {
      // 清除所有选中
      dishTableRef.value.clearSelection()
      // 根据已选菜品设置选中状态
      filteredDishes.value.forEach(dish => {
        const isSelected = tempSelectedDishes.value.some(d => d.id === dish.id)
        if (isSelected) {
          dishTableRef.value.toggleRowSelection(dish, true)
        }
      })
    }
  })
}

// 菜品选择变化
const handleDishSelectionChange = (selection) => {
  // 合并当前分类的选择和其他分类已选的菜品
  const otherCategoryDishes = tempSelectedDishes.value.filter(
    dish => dish.categoryId !== selectedCategory.value
  )
  tempSelectedDishes.value = [...otherCategoryDishes, ...selection]
}

// 取消
const handleCancel = () => {
  visible.value = false
}

// 确认添加
const handleConfirm = () => {
  if (tempSelectedDishes.value.length === 0) {
    ElMessage.warning('请至少选择一个菜品')
    return
  }
  emit('confirm', tempSelectedDishes.value)
  visible.value = false
}
</script>

<style scoped>
.category-item {
  padding: 12px 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.category-item:hover {
  background-color: #f5f7fa;
}

.category-item.active {
  background-color: #ecf5ff;
  color: #409eff;
  font-weight: bold;
}
</style>
