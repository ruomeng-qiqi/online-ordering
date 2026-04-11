import request from '@/utils/request'

/**
 * 分页查询套餐
 */
export const getSetmealPage = (params) => {
  return request({
    url: '/admin/setmeal/page',
    method: 'get',
    params
  })
}

/**
 * 添加套餐
 */
export const addSetmeal = (data) => {
  return request({
    url: '/admin/setmeal',
    method: 'post',
    data
  })
}

/**
 * 更新套餐
 */
export const updateSetmeal = (data) => {
  return request({
    url: '/admin/setmeal',
    method: 'put',
    data
  })
}

/**
 * 删除套餐
 */
export const deleteSetmeal = (id) => {
  return request({
    url: `/admin/setmeal/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除套餐
 */
export const deleteSetmealBatch = (ids) => {
  return request({
    url: '/admin/setmeal/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 更新套餐状态
 */
export const updateSetmealStatus = (id, status) => {
  return request({
    url: `/admin/setmeal/${id}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 根据ID查询套餐
 */
export const getSetmealById = (id) => {
  return request({
    url: `/admin/setmeal/${id}`,
    method: 'get'
  })
}
