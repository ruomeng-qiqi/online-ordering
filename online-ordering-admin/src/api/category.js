import request from '@/utils/request'

/**
 * 分页查询分类
 */
export const getCategoryPage = (params) => {
  return request({
    url: '/admin/category/page',
    method: 'get',
    params
  })
}

/**
 * 添加分类
 */
export const addCategory = (data) => {
  return request({
    url: '/admin/category',
    method: 'post',
    data
  })
}

/**
 * 更新分类
 */
export const updateCategory = (data) => {
  return request({
    url: '/admin/category',
    method: 'put',
    data
  })
}

/**
 * 删除分类
 */
export const deleteCategory = (id) => {
  return request({
    url: `/admin/category/${id}`,
    method: 'delete'
  })
}

/**
 * 更新分类状态
 */
export const updateCategoryStatus = (id, status) => {
  return request({
    url: `/admin/category/${id}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 根据ID查询分类
 */
export const getCategoryById = (id) => {
  return request({
    url: `/admin/category/${id}`,
    method: 'get'
  })
}
