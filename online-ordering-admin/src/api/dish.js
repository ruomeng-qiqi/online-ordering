import request from '@/utils/request'

/**
 * 分页查询菜品
 */
export const getDishPage = (params) => {
  return request({
    url: '/admin/dish/page',
    method: 'get',
    params
  })
}

/**
 * 添加菜品
 */
export const addDish = (data) => {
  return request({
    url: '/admin/dish',
    method: 'post',
    data
  })
}

/**
 * 更新菜品
 */
export const updateDish = (data) => {
  return request({
    url: '/admin/dish',
    method: 'put',
    data
  })
}

/**
 * 删除菜品
 */
export const deleteDish = (id) => {
  return request({
    url: `/admin/dish/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除菜品
 */
export const deleteDishBatch = (ids) => {
  return request({
    url: '/admin/dish/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 更新菜品状态
 */
export const updateDishStatus = (id, status) => {
  return request({
    url: `/admin/dish/${id}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 根据ID查询菜品
 */
export const getDishById = (id) => {
  return request({
    url: `/admin/dish/${id}`,
    method: 'get'
  })
}
