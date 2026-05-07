// pages/checkout/checkout.js
Page({
  data: {
    tableNumber: '',
    tableId: null,
    customerId: null,
    peopleCount: 2,
    cartItems: [],
    totalAmount: 0,
    remark: '',
    remarkLength: 0
  },

  onLoad(options) {
    // 从首页获取购物车数据
    const pages = getCurrentPages()
    const prevPage = pages[pages.length - 2]
    
    if (prevPage) {
      const cartItems = prevPage.data.cartItems || []
      const tableNumber = prevPage.data.tableNumber || ''
      const cartTotal = prevPage.data.cartTotal || 0
      
      // TODO: 获取tableId和customerId
      // 临时使用固定值，实际应该从登录信息和扫码信息获取
      const tableId = 1  // 应该从扫码获取
      const customerId = 1  // 应该从登录信息获取
      
      this.setData({
        cartItems,
        tableNumber,
        totalAmount: cartTotal,
        peopleCount: 2,
        tableId,
        customerId
      })
    }
  },

  // 备注输入
  onRemarkInput(e) {
    const value = e.detail.value
    this.setData({
      remark: value,
      remarkLength: value.length
    })
  },

  // 提交订单
  submitOrder() {
    if (this.data.cartItems.length === 0) {
      wx.showToast({
        title: '购物车为空',
        icon: 'none'
      })
      return
    }

    // 构建订单数据 - 字段对应数据库
    const orderData = {
      // 对应 orders 表字段
      customerId: this.data.customerId,
      tableId: this.data.tableId,
      totalAmount: this.data.totalAmount,
      remark: this.data.remark || '',
      // order_status 默认为 1（待支付）
      // checkout_time, actual_amount, payment_method 等支付时填写
      
      // 订单明细 - 对应 order_detail 表字段
      details: this.data.cartItems.map(item => ({
        dishId: item.dishId || null,
        setmealId: item.setmealId || null,
        name: item.name,
        image: item.image,
        quantity: item.quantity,
        price: item.price,
        amount: item.amount,
        flavor: item.flavorJson || null  // JSON格式的口味信息
      }))
    }

    // TODO: 调用后端API提交订单
    wx.showLoading({
      title: '提交中...'
    })

    // 模拟提交
    setTimeout(() => {
      wx.hideLoading()
      
      // 模拟返回的订单ID - 使用模拟数据中待支付订单的ID
      const orderId = 4  // 对应 mockOrderData.js 中 orderStatus === 1 的订单
      
      wx.showToast({
        title: '订单提交成功',
        icon: 'success',
        duration: 1500
      })

      // 清空购物车
      const pages = getCurrentPages()
      const indexPage = pages.find(page => page.route === 'pages/index/index')
      if (indexPage) {
        indexPage.setData({
          cartItems: [],
          cartCount: 0,
          cartTotal: 0
        })
        indexPage.updateDishCount()
      }

      // 跳转到订单详情页（待支付状态）
      setTimeout(() => {
        wx.redirectTo({
          url: `/pages/order-detail/order-detail?id=${orderId}`
        })
      }, 1500)
    }, 1000)
  }
})
