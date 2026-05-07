// pages/order-detail/order-detail.js
Page({
  data: {
    orderId: null,
    // 订单信息 - 对应 orders 表
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
      orderStatus: 1,  // 1-待支付, 2-已完成, 3-已取消
      remark: '',
      cancelReason: '',
      createTime: ''
    },
    // 订单明细 - 对应 order_detail 表
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

    // TODO: 调用后端API获取订单详情
    // GET /api/order/{id}
    
    // 模拟数据
    setTimeout(() => {
      const mockOrder = {
        id: id,
        orderNumber: `ORD${id}`,
        customerId: 1,
        tableId: 1,
        tableNumber: 'A5',
        checkoutTime: null,
        totalAmount: 64.00,
        actualAmount: null,
        discountAmount: 0,
        pointsDeduction: 0,
        pointsUsed: 0,
        pointsEarned: 0,
        paymentMethod: null,
        orderStatus: 1,
        remark: '少放辣椒',
        cancelReason: '',
        createTime: this.formatTime(new Date())
      }

      const mockDetails = [
        {
          id: 1,
          orderId: id,
          dishId: 1,
          setmealId: null,
          name: '测试菜品',
          image: 'https://via.placeholder.com/100',
          quantity: 1,
          price: 58.00,
          amount: 58.00,
          flavor: '{"甜味":"少糖","忌口":"不要葱"}',
          flavorText: '少糖、不要葱'
        },
        {
          id: 2,
          orderId: id,
          dishId: 2,
          setmealId: null,
          name: '平菇豆腐汤',
          image: 'https://via.placeholder.com/100',
          quantity: 1,
          price: 6.00,
          amount: 6.00,
          flavor: '{"辣度":"不辣"}',
          flavorText: '不辣'
        }
      ]

      // 解析口味信息
      const details = mockDetails.map(item => {
        if (item.flavor) {
          try {
            const flavorObj = JSON.parse(item.flavor)
            item.flavorText = Object.values(flavorObj).join('、')
          } catch (e) {
            item.flavorText = ''
          }
        }
        return item
      })

      // 计算实付金额
      const actualAmount = mockOrder.actualAmount || 
        (mockOrder.totalAmount - mockOrder.discountAmount - mockOrder.pointsDeduction)

      this.setData({
        order: mockOrder,
        orderDetails: details,
        actualAmount: actualAmount.toFixed(2),
        orderStatusText: this.getOrderStatusText(mockOrder.orderStatus),
        paymentMethodText: this.getPaymentMethodText(mockOrder.paymentMethod)
      })

      wx.hideLoading()
    }, 500)
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
      1: '微信支付',
      null: '-'
    }
    return methodMap[method] || '-'
  },

  // 格式化时间
  formatTime(date) {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hour = String(date.getHours()).padStart(2, '0')
    const minute = String(date.getMinutes()).padStart(2, '0')
    const second = String(date.getSeconds()).padStart(2, '0')
    return `${year}-${month}-${day} ${hour}:${minute}:${second}`
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
  }
})
