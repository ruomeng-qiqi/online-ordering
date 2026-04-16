import request from '@/utils/request'

/**
 * 分页查询订单
 */
export const pageQueryOrder = (params) => {
  return request({
    url: '/admin/order/page',
    method: 'get',
    params
  })
}

/**
 * 根据ID查询订单详情
 */
export const getOrderById = (id) => {
  return request({
    url: `/admin/order/${id}`,
    method: 'get'
  })
}

/**
 * 更新订单
 */
export const updateOrder = (data) => {
  return request({
    url: '/admin/order',
    method: 'put',
    data
  })
}

/**
 * 完成订单
 */
export const completeOrder = (id) => {
  return request({
    url: `/admin/order/${id}/complete`,
    method: 'put'
  })
}

/**
 * 取消订单
 */
export const cancelOrder = (data) => {
  return request({
    url: '/admin/order/cancel',
    method: 'put',
    data
  })
}

/**
 * 删除订单
 */
export const deleteOrder = (id) => {
  return request({
    url: `/admin/order/${id}`,
    method: 'delete'
  })
}
