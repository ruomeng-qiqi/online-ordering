// pages/payment/payment.js
const orderApi = require('../../api/order.js')

Page({
  data: {
    orderId: null,
    customerId: null,
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
    const customerId = wx.getStorageSync('customerId')
    
    if (!customerId) {
      wx.showModal({
        title: '提示',
        content: '请先登录',
        showCancel: false,
        success: () => {
          wx.switchTab({
            url: '/pages/mine/mine'
          })
        }
      })
      return
    }
    
    if (orderId) {
      this.setData({ 
        orderId,
        customerId,
        paymentMethod: 1  // 固定为微信支付
      })
      this.loadOrderInfo(orderId)
      this.loadCustomerPoints(customerId)
    }
  },

  // 加载订单信息
  loadOrderInfo(orderId) {
    wx.showLoading({
      title: '加载中...'
    })
    
    orderApi.getOrderById(orderId).then(result => {
      wx.hideLoading()
      
      const order = result.data
      
      // 不限制最多可使用积分
      const maxPointsUsed = 999999

      this.setData({
        order: {
          id: order.id,
          orderNumber: order.orderNumber,
          totalAmount: parseFloat(order.totalAmount),
          discountAmount: parseFloat(order.discountAmount || 0)
        },
        maxPointsUsed,
        actualAmount: (parseFloat(order.totalAmount) - parseFloat(order.discountAmount || 0)).toFixed(2)
      })
    }).catch(err => {
      wx.hideLoading()
      console.error('加载订单信息失败', err)
      wx.showModal({
        title: '加载失败',
        content: err.message || '加载订单信息失败',
        showCancel: false,
        success: () => {
          wx.navigateBack()
        }
      })
    })
  },

  // 加载顾客积分
  loadCustomerPoints(customerId) {
    // 调用后端API获取顾客积分
    wx.request({
      url: getApp().globalData.baseUrl + '/user/customer/points',
      method: 'GET',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      success: (res) => {
        if (res.data.code === 200) {
          this.setData({
            availablePoints: res.data.data || 0
          })
        }
      },
      fail: (err) => {
        console.error('加载积分失败', err)
      }
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
      // 计算获得积分（1元=1积分，向下取整）
      pointsEarned: Math.floor(parseFloat(this.data.actualAmount))
    }

    // 调起微信支付
    this.wxPay(paymentData)
  },

  // 微信支付
  wxPay(paymentData) {
    wx.showLoading({
      title: '支付中...'
    })

    // TODO: 实际项目中需要调用后端API获取微信支付参数
    // 这里暂时模拟支付成功，直接调用支付接口
    
    // 模拟微信支付成功后，调用后端支付接口
    setTimeout(() => {
      this.submitPayment(paymentData)
    }, 1000)
  },

  // 提交支付结果到后端
  submitPayment(paymentData) {
    orderApi.payOrder(paymentData).then(() => {
      wx.hideLoading()
      
      wx.showToast({
        title: '支付成功',
        icon: 'success',
        duration: 1500
      })

      // 跳转到支付成功页面
      setTimeout(() => {
        wx.redirectTo({
          url: `/pages/payment-success/payment-success?orderId=${this.data.orderId}&orderNumber=${this.data.order.orderNumber}&amount=${paymentData.actualAmount}&points=${paymentData.pointsEarned}`
        })
      }, 1500)
    }).catch(err => {
      wx.hideLoading()
      console.error('支付失败', err)
      
      wx.showModal({
        title: '支付失败',
        content: err.message || '支付失败，请重试',
        showCancel: false
      })
    })
  }
})
