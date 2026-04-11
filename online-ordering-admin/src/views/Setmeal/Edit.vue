<template>
  <div class="setmeal-edit-container">
    <el-card>
      <div class="header">
        <h2>{{ isEdit ? '修改套餐' : '新增套餐' }}</h2>
      </div>

      <el-form :model="formData" label-width="100px" style="max-width: 800px; margin-top: 30px;">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="套餐名称" required>
              <el-input v-model="formData.name" placeholder="请输入套餐名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="套餐分类" required>
              <el-select v-model="formData.categoryId" placeholder="请选择分类" style="width: 100%;">
                <el-option 
                  v-for="category in categoryList" 
                  :key="category.id" 
                  :label="category.name" 
                  :value="category.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="套餐价格" required>
              <el-input v-model="formData.price" placeholder="请输入价格" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="套餐菜品" required>
          <div style="width: 100%;">
            <el-button type="primary" @click="dishDialogVisible = true">+ 添加菜品</el-button>
            <el-table 
              v-if="selectedDishes.length > 0" 
              :data="selectedDishes" 
              style="width: 100%; margin-top: 15px;"
              border
              header-align="center"
            >
              <el-table-column prop="name" label="菜品名称" min-width="200" align="center" />
              <el-table-column prop="price" label="原价" min-width="150" align="center">
                <template #default="{ row }">
                  ¥{{ row.price }}
                </template>
              </el-table-column>
              <el-table-column label="份数" min-width="200" align="center">
                <template #default="{ row }">
                  <div style="display: flex; align-items: center; justify-content: center;">
                    <el-button @click="decreaseCount(row.id)" style="width: 32px; padding: 0;">-</el-button>
                    <el-input 
                      v-model="row.count" 
                      style="width: 60px; margin: 0 10px;"
                      input-style="text-align: center;"
                      @input="validateCount(row)"
                    />
                    <el-button @click="increaseCount(row.id)" style="width: 32px; padding: 0;">+</el-button>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="操作" min-width="100" align="center">
                <template #default="{ row }">
                  <el-button type="danger" link @click="removeDish(row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-form-item>
        <el-form-item label="套餐图片">
          <el-upload
            class="avatar-uploader"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
          >
            <img v-if="formData.image" :src="formData.image" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div style="color: #999; font-size: 12px; margin-top: 5px;">
            支持 jpg、png 格式，大小不超过 2MB
          </div>
        </el-form-item>
        <el-form-item label="套餐描述">
          <el-input 
            v-model="formData.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入套餐描述"
            style="width: 500px;"
          />
        </el-form-item>

        <el-form-item>
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="handleSubmit">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 添加菜品对话框 -->
    <DishSelectDialog 
      v-model="dishDialogVisible"
      :selected-dishes="selectedDishes"
      @confirm="handleDishConfirm"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import DishSelectDialog from './DishSelectDialog.vue'
import { getSetmealById, addSetmeal, updateSetmeal } from '@/api/setmeal'
import { getCategoryPage } from '@/api/category'

const route = useRoute()
const router = useRouter()

const isEdit = ref(false)
const categoryList = ref([])
const formData = ref({
  id: null,
  name: '',
  categoryId: '',
  price: '',
  image: '',
  description: ''
})

// 菜品相关
const dishDialogVisible = ref(false)
const selectedDishes = ref([])

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

onMounted(async () => {
  await loadCategoryList()
  
  // 如果有 id 参数，说明是编辑模式
  if (route.query.id) {
    isEdit.value = true
    loadSetmealData(route.query.id)
  }
})

// 加载套餐数据
const loadSetmealData = async (id) => {
  try {
    const response = await getSetmealById(id)
    if (response.code === 200) {
      const setmeal = response.data
      formData.value = {
        id: setmeal.id,
        name: setmeal.name,
        categoryId: setmeal.categoryId,
        price: setmeal.price,
        image: setmeal.image,
        description: setmeal.description
      }
      
      // 转换套餐菜品数据
      if (setmeal.dishes && setmeal.dishes.length > 0) {
        selectedDishes.value = setmeal.dishes.map(dish => ({
          id: dish.dishId,
          name: dish.dishName,
          price: dish.price,
          count: dish.copies
        }))
      }
    } else {
      ElMessage.error(response.message || '加载套餐数据失败')
    }
  } catch (error) {
    console.error('加载套餐数据失败:', error)
  }
}

// 文件上传前验证
const beforeUpload = (file) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传 JPG/PNG 格式的图片!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 文件选择变化
const handleFileChange = (file) => {
  if (beforeUpload(file.raw)) {
    formData.value.image = URL.createObjectURL(file.raw)
  }
}

// 上传成功
const handleUploadSuccess = (response, file) => {
  formData.value.image = URL.createObjectURL(file.raw)
  ElMessage.success('上传成功')
}

// 确认添加菜品
const handleDishConfirm = (dishes) => {
  // 为新添加的菜品设置默认份数
  const newDishes = dishes.map(dish => {
    const existing = selectedDishes.value.find(d => d.id === dish.id)
    return existing || { ...dish, count: 1 }
  })
  selectedDishes.value = newDishes
  ElMessage.success(`已添加 ${selectedDishes.value.length} 个菜品`)
}

// 增加份数
const increaseCount = (dishId) => {
  const dish = selectedDishes.value.find(d => d.id === dishId)
  if (dish) {
    dish.count = parseInt(dish.count) + 1
  }
}

// 减少份数
const decreaseCount = (dishId) => {
  const dish = selectedDishes.value.find(d => d.id === dishId)
  if (dish && dish.count > 1) {
    dish.count = parseInt(dish.count) - 1
  }
}

// 验证份数输入
const validateCount = (dish) => {
  const count = parseInt(dish.count)
  if (isNaN(count) || count < 1) {
    dish.count = 1
  } else {
    dish.count = count
  }
}

// 移除菜品
const removeDish = (dishId) => {
  selectedDishes.value = selectedDishes.value.filter(dish => dish.id !== dishId)
}

// 取消
const handleCancel = () => {
  router.back()
}

// 提交表单
const handleSubmit = async () => {
  if (!formData.value.name) {
    ElMessage.warning('请输入套餐名称')
    return
  }
  if (!formData.value.categoryId) {
    ElMessage.warning('请选择分类')
    return
  }
  if (!formData.value.price || formData.value.price <= 0) {
    ElMessage.warning('请输入正确的价格')
    return
  }
  if (selectedDishes.value.length === 0) {
    ElMessage.warning('请至少添加一个菜品')
    return
  }

  try {
    // 构建提交数据
    const submitData = {
      ...formData.value,
      dishes: selectedDishes.value.map(dish => ({
        dishId: dish.id,
        copies: parseInt(dish.count)
      }))
    }
    
    let response
    if (isEdit.value) {
      response = await updateSetmeal(submitData)
    } else {
      response = await addSetmeal(submitData)
    }
    
    if (response.code === 200) {
      ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
      router.push('/setmeal')
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
  }
}
</script>

<style scoped>
.setmeal-edit-container {
  padding: 20px;
}

.header {
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 148px;
  height: 148px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar {
  width: 148px;
  height: 148px;
  display: block;
  object-fit: cover;
}
</style>
