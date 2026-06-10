// pages/payment-success/payment-success.js
Page({
  data: {
    orderId: null,
    orderNumber: '',
    amount: 0,
    points: 0
  },

  onLoad(options) {
    const orderId = options.orderId
    const orderNumber = options.orderNumber || ''
    const amount = options.amount || 0
    const points = options.points || 0

    this.setData({
      orderId,
      orderNumber: orderNumber || `ORD${orderId}`,
      amount: parseFloat(amount).toFixed(2),
      points: parseInt(points)
    })
  },

  // 查看订单
  viewOrder() {
    wx.redirectTo({
      url: `/pages/order-detail/order-detail?id=${this.data.orderId}`
    })
  },

  // 返回首页
  backToHome() {
    wx.switchTab({
      url: '/pages/index/index'
    })
  }
})
