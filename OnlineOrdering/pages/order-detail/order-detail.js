// pages/order-detail/order-detail.js
const orderApi = require('../../api/order.js')

Page({
  data: {
    orderId: null,
    // 订单信息
    order: {
      id: null,
      orderNumber: '',
      customerId: null,
      tableId: null,
      tableNumber: '',
      checkoutTime: null,
      totalAmount: 0,
      actualAmount: null,
      discountAmount: 0,
      pointsDeduction: 0,
      pointsUsed: 0,
      pointsEarned: 0,
      paymentMethod: null,
      orderStatus: null,
      remark: '',
      cancelReason: '',
      createTime: ''
    },
    // 订单明细
    orderDetails: [],
    // 计算后的实付金额
    actualAmount: 0,
    // 状态文本
    orderStatusText: '',
    paymentMethodText: ''
  },

  onLoad(options) {
    const orderId = options.id
    if (orderId) {
      this.setData({ orderId })
      this.loadOrderDetail(orderId)
    }
  },

  // 加载订单详情
  loadOrderDetail(id) {
    wx.showLoading({
      title: '加载中...'
    })

    // 调用后端API获取订单详情
    orderApi.getOrderById(id).then(result => {
      wx.hideLoading()
      
      const orderData = result.data

      // 处理订单明细，解析口味信息
      const details = orderData.details.map(item => {
        if (item.flavor) {
          try {
            const flavorObj = JSON.parse(item.flavor)
            item.flavorText = Object.values(flavorObj).join('、')
          } catch (e) {
            item.flavorText = ''
          }
        } else {
          item.flavorText = ''
        }
        return item
      })

      // 计算实付金额
      const actualAmount = orderData.actualAmount || 
        (orderData.totalAmount - orderData.discountAmount - orderData.pointsDeduction)

      // 处理时间格式，将 T 替换为空格
      if (orderData.createTime) {
        orderData.createTime = orderData.createTime.replace('T', ' ')
      }
      if (orderData.checkoutTime) {
        orderData.checkoutTime = orderData.checkoutTime.replace('T', ' ')
      }

      // 处理桌号显示：如果没有则显示 '-'
      if (!orderData.tableNumber) {
        orderData.tableNumber = '-'
      }

      this.setData({
        order: orderData,
        orderDetails: details,
        actualAmount: actualAmount.toFixed(2),
        orderStatusText: this.getOrderStatusText(orderData.orderStatus),
        paymentMethodText: this.getPaymentMethodText(orderData.paymentMethod)
      })
    }).catch(err => {
      wx.hideLoading()
      console.error('加载订单详情失败', err)
      wx.showToast({
        title: err.message || '加载失败',
        icon: 'none'
      })
      
      // 返回上一页
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    })
  },

  // 获取订单状态文本
  getOrderStatusText(status) {
    const statusMap = {
      1: '待支付',
      2: '已完成',
      3: '已取消'
    }
    return statusMap[status] || '未知'
  },

  // 获取支付方式文本
  getPaymentMethodText(method) {
    const methodMap = {
      1: '在线支付',
      2: '线下支付'
    }
    return methodMap[method] || '-'
  },

  // 继续点餐
  continueShopping() {
    wx.switchTab({
      url: '/pages/index/index'
    })
  },

  // 去支付
  goToPay() {
    wx.navigateTo({
      url: `/pages/payment/payment?orderId=${this.data.orderId}`
    })
  },

  // 删除订单
  deleteOrder() {
    wx.showModal({
      title: '提示',
      content: '确定要删除该订单吗？',
      success: (res) => {
        if (res.confirm) {
          // TODO: 调用后端API删除订单
          // DELETE /api/order/{id}
          
          wx.showToast({
            title: '功能开发中',
            icon: 'none'
          })
        }
      }
    })
  },

  // 再来一单
  reorder() {
    wx.showModal({
      title: '再来一单',
      content: '将该订单的菜品加入购物车？',
      success: (res) => {
        if (res.confirm) {
          // TODO: 调用后端API将订单菜品加入购物车
          // POST /api/cart/batch-add
          
          wx.showToast({
            title: '功能开发中',
            icon: 'none'
          })
        }
      }
    })
  }
})
