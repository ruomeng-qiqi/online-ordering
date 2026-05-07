// pages/payment/payment.js
Page({
  data: {
    orderId: null,
    // 订单信息
    order: {
      id: null,
      orderNumber: '',
      totalAmount: 0,
      discountAmount: 0
    },
    // 积分相关
    availablePoints: 0,  // 可用积分
    pointsUsed: 0,       // 使用的积分
    pointsDeduction: 0,  // 积分抵扣金额
    maxPointsUsed: 0,    // 最多可使用积分
    // 支付方式：固定为1-微信支付
    paymentMethod: 1,
    // 实付金额
    actualAmount: 0
  },

  onLoad(options) {
    const orderId = options.orderId
    if (orderId) {
      this.setData({ 
        orderId,
        paymentMethod: 1  // 固定为微信支付
      })
      this.loadOrderInfo(orderId)
      this.loadCustomerPoints()
    }
  },

  // 加载订单信息
  loadOrderInfo(orderId) {
    // TODO: 调用后端API获取订单信息
    // GET /api/order/{id}
    
    // 模拟数据
    const mockOrder = {
      id: orderId,
      orderNumber: `ORD${orderId}`,
      totalAmount: 64.00,
      discountAmount: 0
    }

    // 不限制最多可使用积分
    const maxPointsUsed = 999999

    this.setData({
      order: mockOrder,
      maxPointsUsed,
      actualAmount: (mockOrder.totalAmount - mockOrder.discountAmount).toFixed(2)
    })
  },

  // 加载顾客积分
  loadCustomerPoints() {
    // TODO: 调用后端API获取顾客积分
    // GET /api/customer/points
    
    // 模拟数据
    this.setData({
      availablePoints: 1580
    })
  },

  // 积分输入
  onPointsInput(e) {
    let value = parseInt(e.detail.value) || 0
    
    // 只限制不超过可用积分
    if (value > this.data.availablePoints) {
      value = this.data.availablePoints
    }
    
    // 限制不超过订单总额（避免负数）
    const maxDeduction = this.data.order.totalAmount - this.data.order.discountAmount
    const maxPoints = Math.floor(maxDeduction * 100)
    if (value > maxPoints) {
      value = maxPoints
    }
    
    this.updatePoints(value)
  },

  // 减少积分
  decreasePoints() {
    let value = this.data.pointsUsed - 100
    if (value < 0) value = 0
    this.updatePoints(value)
  },

  // 增加积分
  increasePoints() {
    let value = this.data.pointsUsed + 100
    if (value > this.data.availablePoints) {
      value = this.data.availablePoints
    }
    
    // 限制不超过订单总额
    const maxDeduction = this.data.order.totalAmount - this.data.order.discountAmount
    const maxPoints = Math.floor(maxDeduction * 100)
    if (value > maxPoints) {
      value = maxPoints
    }
    
    this.updatePoints(value)
  },

  // 更新积分和金额
  updatePoints(points) {
    const deduction = (points / 100).toFixed(2)
    const actualAmount = (
      this.data.order.totalAmount - 
      this.data.order.discountAmount - 
      parseFloat(deduction)
    ).toFixed(2)

    this.setData({
      pointsUsed: points,
      pointsDeduction: deduction,
      actualAmount
    })
  },

  // 确认支付
  confirmPay() {
    // 构建支付数据 - 对应数据库字段
    const paymentData = {
      orderId: this.data.orderId,
      paymentMethod: 1,  // 固定为微信支付
      pointsUsed: this.data.pointsUsed,
      pointsDeduction: parseFloat(this.data.pointsDeduction),
      actualAmount: parseFloat(this.data.actualAmount),
      // 计算获得积分（实付金额的10%）
      pointsEarned: Math.floor(parseFloat(this.data.actualAmount) * 0.1)
    }

    // 调起微信支付
    this.wxPay(paymentData)
  },

  // 微信支付
  wxPay(paymentData) {
    wx.showLoading({
      title: '支付中...'
    })

    // TODO: 调用后端API获取支付参数
    // POST /api/order/pay/wechat
    
    // 模拟微信支付
    setTimeout(() => {
      wx.hideLoading()
      
      // TODO: 调起微信支付
      // wx.requestPayment({
      //   timeStamp: '',
      //   nonceStr: '',
      //   package: '',
      //   signType: 'MD5',
      //   paySign: '',
      //   success: (res) => {
      //     this.paymentSuccess(paymentData)
      //   },
      //   fail: (err) => {
      //     wx.showToast({
      //       title: '支付失败',
      //       icon: 'none'
      //     })
      //   }
      // })

      // 模拟支付成功
      this.paymentSuccess(paymentData)
    }, 1000)
  },

  // 支付成功
  paymentSuccess(paymentData) {
    wx.showToast({
      title: '支付成功',
      icon: 'success',
      duration: 1500
    })

    // 跳转到支付成功页面
    setTimeout(() => {
      wx.redirectTo({
        url: `/pages/payment-success/payment-success?orderId=${this.data.orderId}&amount=${paymentData.actualAmount}&points=${paymentData.pointsEarned}`
      })
    }, 1500)
  }
})
