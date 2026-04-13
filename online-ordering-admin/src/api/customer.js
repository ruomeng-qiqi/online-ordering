import request from '@/utils/request'

/**
 * 分页查询顾客
 */
export const pageQueryCustomer = (params) => {
  return request({
    url: '/admin/customer/page',
    method: 'get',
    params
  })
}

/**
 * 根据ID查询顾客详情
 */
export const getCustomerById = (id) => {
  return request({
    url: `/admin/customer/${id}`,
    method: 'get'
  })
}

/**
 * 更新顾客状态
 */
export const updateCustomerStatus = (id, status) => {
  return request({
    url: `/admin/customer/${id}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 删除顾客
 */
export const deleteCustomer = (id) => {
  return request({
    url: `/admin/customer/${id}`,
    method: 'delete'
  })
}

/**
 * 调整顾客积分
 */
export const adjustPoints = (data) => {
  return request({
    url: '/admin/customer/points/adjust',
    method: 'post',
    data
  })
}

/**
 * 查询顾客积分记录
 */
export const getPointsRecords = (customerId) => {
  return request({
    url: `/admin/customer/${customerId}/points/records`,
    method: 'get'
  })
}
