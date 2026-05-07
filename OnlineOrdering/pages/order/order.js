// pages/order/order.js
Page({
  data: {
    orderList: []
  },

  onLoad() {
    this.loadOrders()
  },

  onShow() {
    // 页面显示时刷新订单列表
    this.loadOrders()
  },

  loadOrders() {
    // TODO: 从后端加载订单数据
    console.log('加载订单列表')
  }
})
