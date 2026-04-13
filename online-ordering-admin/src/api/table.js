import request from '@/utils/request'

/**
 * 添加餐台
 */
export const addTable = (data) => {
  return request({
    url: '/admin/table',
    method: 'post',
    data
  })
}

/**
 * 更新餐台
 */
export const updateTable = (data) => {
  return request({
    url: '/admin/table',
    method: 'put',
    data
  })
}

/**
 * 删除餐台
 */
export const deleteTable = (id) => {
  return request({
    url: `/admin/table/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除餐台
 */
export const deleteBatchTable = (ids) => {
  return request({
    url: '/admin/table/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 根据ID查询餐台
 */
export const getTableById = (id) => {
  return request({
    url: `/admin/table/${id}`,
    method: 'get'
  })
}

/**
 * 分页查询餐台
 */
export const pageQueryTable = (params) => {
  return request({
    url: '/admin/table/page',
    method: 'get',
    params
  })
}
