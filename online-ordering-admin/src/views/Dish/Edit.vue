<template>
  <div class="dish-edit-container">
    <el-card>
      <div class="header">
        <h2>{{ isEdit ? '修改菜品' : '新增菜品' }}</h2>
      </div>

      <el-form :model="formData" label-width="100px" style="max-width: 800px; margin-top: 30px;">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="菜品名称" required>
              <el-input v-model="formData.name" placeholder="请输入菜品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜品分类" required>
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
            <el-form-item label="价格" required>
              <el-input v-model="formData.price" placeholder="请输入价格" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="口味">
          <div style="width: 100%;">
            <div 
              v-for="(flavor, index) in formData.flavors" 
              :key="index"
              style="display: flex; align-items: center; margin-bottom: 10px;"
            >
              <el-select 
                v-model="flavor.name" 
                placeholder="请选择"
                style="width: 150px; margin-right: 10px;"
                @change="(val) => handleFlavorTypeChange(index, val)"
              >
                <el-option
                  v-for="item in flavorOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
              <div style="flex: 1; max-width: 500px; display: flex; align-items: center; flex-wrap: wrap; border: 1px solid #dcdfe6; border-radius: 4px; padding: 5px 10px; min-height: 32px;">
                <el-tag
                  v-for="(option, tagIndex) in flavor.options"
                  :key="tagIndex"
                  closable
                  size="small"
                  @close="removeFlavorTag(index, tagIndex)"
                  style="margin: 2px 5px 2px 0; font-size: 12px;"
                >
                  {{ option }}
                </el-tag>
              </div>
              <el-button 
                link 
                type="danger" 
                @click="removeFlavorRow(index)"
                style="margin-left: 10px;"
              >
                删除
              </el-button>
            </div>
            <el-button type="primary" @click="addFlavorRow">添加口味</el-button>
          </div>
        </el-form-item>
        <el-form-item label="图片">
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
        <el-form-item label="描述">
          <el-input 
            v-model="formData.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入菜品描述"
            style="width: 500px;"
          />
        </el-form-item>

        <el-form-item>
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="handleSubmit">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getDishById, addDish, updateDish } from '@/api/dish'
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
  description: '',
  flavors: []
})

// 口味选项
const flavorOptions = [
  { 
    label: '甜味', 
    value: '甜味',
    options: ['无糖', '少糖', '半糖', '多糖', '全糖']
  },
  { 
    label: '温度', 
    value: '温度',
    options: ['热饮', '常温', '去冰', '少冰', '多冰']
  },
  { 
    label: '忌口', 
    value: '忌口',
    options: ['不要葱', '不要蒜', '不要香菜', '不要辣']
  },
  { 
    label: '辣度', 
    value: '辣度',
    options: ['不辣', '微辣', '中辣', '特辣']
  },
]

// 加载分类列表
const loadCategoryList = async () => {
  try {
    const response = await getCategoryPage({ page: 1, pageSize: 100, type: 1 })
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
    loadDishData(route.query.id)
  }
})

// 加载菜品数据
const loadDishData = async (id) => {
  try {
    const response = await getDishById(id)
    if (response.code === 200) {
      const dish = response.data
      formData.value = {
        id: dish.id,
        name: dish.name,
        categoryId: dish.categoryId,
        price: dish.price,
        image: dish.image,
        description: dish.description,
        flavors: dish.flavors || []
      }
    } else {
      ElMessage.error(response.message || '加载菜品数据失败')
    }
  } catch (error) {
    console.error('加载菜品数据失败:', error)
  }
}

// 添加口味行
const addFlavorRow = () => {
  formData.value.flavors.push({
    name: '',
    options: []
  })
}

// 删除口味行
const removeFlavorRow = (index) => {
  formData.value.flavors.splice(index, 1)
}

// 口味类型选择变化
const handleFlavorTypeChange = (index, value) => {
  const selectedFlavor = flavorOptions.find(item => item.value === value)
  if (selectedFlavor) {
    formData.value.flavors[index].name = value
    formData.value.flavors[index].options = [...selectedFlavor.options]
  }
}

// 删除口味标签
const removeFlavorTag = (flavorIndex, tagIndex) => {
  formData.value.flavors[flavorIndex].options.splice(tagIndex, 1)
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

// 取消
const handleCancel = () => {
  router.back()
}

// 提交表单
const handleSubmit = async () => {
  if (!formData.value.name) {
    ElMessage.warning('请输入菜品名称')
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

  try {
    let response
    if (isEdit.value) {
      response = await updateDish(formData.value)
    } else {
      response = await addDish(formData.value)
    }
    
    if (response.code === 200) {
      ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
      router.push('/dish')
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
  }
}
</script>

<style scoped>
.dish-edit-container {
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
