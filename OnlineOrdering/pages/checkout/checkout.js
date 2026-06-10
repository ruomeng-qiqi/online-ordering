// pages/checkout/checkout.js
const orderApi = require('../../api/order.js')

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
      
      // 从本地存储获取tableId和customerId
      const tableId = wx.getStorageSync('tableId') || null
      const customerId = wx.getStorageSync('customerId') || null
      
      console.log('结算页面加载 - customerId:', customerId, 'tableId:', tableId, 'tableNumber:', tableNumber)
      
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
      
      if (!tableId || !tableNumber || tableNumber === '点击扫码') {
        wx.showModal({
          title: '提示',
          content: '请先扫描餐台二维码',
          showCancel: false,
          success: () => {
            wx.switchTab({
              url: '/pages/index/index'
            })
          }
        })
        return
      }
      
      this.setData({
        cartItems,
        tableNumber,
        totalAmount: cartTotal,
        peopleCount: 2,
        tableId,
        customerId
      })
      
      console.log('结算页面数据：', this.data)
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

    // 检查是否是继续点餐模式
    const continueOrderMode = wx.getStorageSync('continueOrderMode')
    const pendingOrderId = wx.getStorageSync('pendingOrderId')
    
    if (continueOrderMode && pendingOrderId) {
      // 追加模式：更新订单
      this.updateExistingOrder(pendingOrderId)
    } else {
      // 新建模式：检查是否有待支付订单
      this.checkAndCreateOrder()
    }
  },

  // 检查并创建订单
  checkAndCreateOrder() {
    const tableId = this.data.tableId
    
    wx.showLoading({
      title: '检查中...'
    })
    
    // 检查该餐台是否有待支付订单
    orderApi.getPendingOrder(tableId).then(result => {
      wx.hideLoading()
      
      if (result.data) {
        // 有待支付订单，提示用户
        wx.showModal({
          title: '提示',
          content: `该桌有待支付订单（订单号：${result.data.orderNumber}），是否追加到该订单？`,
          confirmText: '追加',
          cancelText: '取消',
          success: (res) => {
            if (res.confirm) {
              // 追加到原订单
              this.updateExistingOrder(result.data.id)
            }
          }
        })
      } else {
        // 没有待支付订单，创建新订单
        this.createNewOrder()
      }
    }).catch(err => {
      wx.hideLoading()
      console.error('检查待支付订单失败', err)
      // 检查失败，直接创建新订单
      this.createNewOrder()
    })
  },

  // 创建新订单
  createNewOrder() {
    // 构建订单数据 - 字段对应后端接口
    const orderData = {
      customerId: this.data.customerId,
      tableId: this.data.tableId,
      totalAmount: parseFloat(this.data.totalAmount),
      remark: this.data.remark || '',
      
      // 订单明细列表
      details: this.data.cartItems.map(item => ({
        dishId: item.dishId || null,
        setmealId: item.setmealId || null,
        name: item.name,
        image: item.image,
        quantity: item.quantity,
        price: parseFloat(item.price),
        amount: parseFloat(item.amount),
        flavor: item.flavorJson || null
      }))
    }
    
    console.log('创建新订单数据：', orderData)

    // 调用后端API提交订单
    wx.showLoading({
      title: '提交中...'
    })

    orderApi.createOrder(orderData).then(result => {
      wx.hideLoading()
      
      console.log('订单创建成功：', result)
      
      const orderId = result.data.id
      
      wx.showToast({
        title: '订单提交成功',
        icon: 'success',
        duration: 1500
      })

      // 清空购物车
      this.clearCartAndNavigate()
    }).catch(err => {
      wx.hideLoading()
      console.error('提交订单失败', err)
      
      let errorMsg = '提交失败，请重试'
      if (err.message) {
        errorMsg = err.message
      }
      
      wx.showModal({
        title: '提交失败',
        content: errorMsg,
        showCancel: false
      })
    })
  },

  // 更新现有订单（追加菜品）
  updateExistingOrder(orderId) {
    // 先获取原订单信息
    wx.showLoading({
      title: '加载中...'
    })
    
    orderApi.getOrderById(orderId).then(result => {
      const existingOrder = result.data
      
      // 合并订单明细
      const existingDetails = existingOrder.details || []
      const newDetails = this.data.cartItems.map(item => ({
        dishId: item.dishId || null,
        setmealId: item.setmealId || null,
        name: item.name,
        image: item.image,
        quantity: item.quantity,
        price: parseFloat(item.price),
        amount: parseFloat(item.amount),
        flavor: item.flavorJson || null
      }))
      
      // 合并明细：相同菜品（相同dishId/setmealId和flavor）数量相加
      const mergedDetails = [...existingDetails]
      newDetails.forEach(newItem => {
        const existingIndex = mergedDetails.findIndex(item => 
          item.dishId === newItem.dishId && 
          item.setmealId === newItem.setmealId &&
          item.flavor === newItem.flavor
        )
        
        if (existingIndex > -1) {
          // 相同菜品，数量相加
          mergedDetails[existingIndex].quantity += newItem.quantity
          mergedDetails[existingIndex].amount = mergedDetails[existingIndex].quantity * mergedDetails[existingIndex].price
        } else {
          // 新菜品，直接添加
          mergedDetails.push(newItem)
        }
      })
      
      // 计算新的总金额
      const newTotalAmount = mergedDetails.reduce((sum, item) => sum + parseFloat(item.amount), 0)
      
      // 构建更新订单数据
      const updateData = {
        id: orderId,
        customerId: this.data.customerId,
        tableId: this.data.tableId,
        totalAmount: newTotalAmount,
        discountAmount: existingOrder.discountAmount || 0,
        remark: this.data.remark || existingOrder.remark || '',
        details: mergedDetails
      }
      
      console.log('更新订单数据：', updateData)
      
      wx.hideLoading()
      wx.showLoading({
        title: '追加中...'
      })
      
      // 调用更新订单接口
      return orderApi.updateOrder(updateData)
    }).then(() => {
      wx.hideLoading()
      
      wx.showToast({
        title: '追加成功',
        icon: 'success',
        duration: 1500
      })
      
      // 清空继续点餐标记
      wx.removeStorageSync('continueOrderMode')
      wx.removeStorageSync('pendingOrderId')
      wx.removeStorageSync('pendingOrderTableId')
      
      // 清空购物车
      this.clearCartAndNavigate()
    }).catch(err => {
      wx.hideLoading()
      console.error('追加订单失败', err)
      
      wx.showModal({
        title: '追加失败',
        content: err.message || '追加失败，请重试',
        showCancel: false
      })
    })
  },

  // 清空购物车并跳转
  clearCartAndNavigate() {
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

    // 跳转到订单列表页
    setTimeout(() => {
      wx.switchTab({
        url: '/pages/order/order'
      })
    }, 1500)
  }
})
