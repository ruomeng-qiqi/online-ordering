const { request } = require('../utils/request.js')

/**
 * 查询订单列表
 */
function getOrderList(customerId) {
  return request({
    url: '/user/order/list',
    method: 'GET',
    data: { customerId }
  })
}

/**
 * 根据ID查询订单详情
 */
function getOrderById(id) {
  return request({
    url: `/user/order/${id}`,
    method: 'GET'
  })
}

/**
 * 创建订单
 */
function createOrder(orderData) {
  return request({
    url: '/user/order',
    method: 'POST',
    data: orderData
  })
}

/**
 * 查询餐台的待支付订单
 */
function getPendingOrder(tableId) {
  return request({
    url: '/user/order/pending',
    method: 'GET',
    data: { tableId }
  })
}

/**
 * 更新订单（追加菜品）
 */
function updateOrder(orderData) {
  return request({
    url: '/user/order',
    method: 'PUT',
    data: orderData
  })
}

/**
 * 支付订单
 */
function payOrder(paymentData) {
  return request({
    url: '/user/order/pay',
    method: 'POST',
    data: paymentData
  })
}

/**
 * 删除订单
 */
function deleteOrder(id) {
  return request({
    url: `/user/order/${id}`,
    method: 'DELETE'
  })
}

module.exports = {
  getOrderList,
  getOrderById,
  createOrder,
  getPendingOrder,
  updateOrder,
  payOrder,
  deleteOrder
}
