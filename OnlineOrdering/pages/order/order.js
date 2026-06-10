// pages/order/order.js
const orderApi = require('../../api/order.js')

Page({
  data: {
    orders: [],
    filteredOrders: [],
    currentFilter: 0, // 0-全部, 1-待支付, 2-已完成, 3-已取消
    orderStatusText: {
      1: '待支付',
      2: '已完成',
      3: '已取消'
    },
    orderStatusColor: {
      1: '#ff6b35',
      2: '#52c41a',
      3: '#999999'
    }
  },

  onLoad() {
    this.loadOrders()
  },

  onShow() {
    // 每次显示页面时刷新订单列表
    console.log('订单页面显示，自动刷新数据')
    this.loadOrders()
  },

  /**
   * 下拉刷新
   */
  onPullDownRefresh() {
    console.log('用户触发下拉刷新')
    this.loadOrders()
  },

  // 加载订单列表
  loadOrders() {
    // 获取当前登录用户的 customerId
    const customerId = wx.getStorageSync('customerId')
    
    if (!customerId) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      })
      setTimeout(() => {
        wx.switchTab({
          url: '/pages/mine/mine'
        })
      }, 1500)
      return
    }

    // 调用后端API获取订单列表
    orderApi.getOrderList(customerId).then(result => {
      // 停止下拉刷新动画
      wx.stopPullDownRefresh()
      
      // 处理订单数据
      const orders = result.data.map(order => {
        // 计算订单总件数
        const totalQuantity = order.details.reduce((sum, item) => sum + item.quantity, 0)
        
        // 处理时间格式，将 T 替换为空格
        if (order.createTime) {
          order.createTime = order.createTime.replace('T', ' ')
        }
        if (order.checkoutTime) {
          order.checkoutTime = order.checkoutTime.replace('T', ' ')
        }
        
        // 处理桌号显示：如果没有则显示 '-'
        if (!order.tableNumber) {
          order.tableNumber = '-'
        }
        
        return {
          ...order,
          totalQuantity
        }
      })
      
      this.setData({ orders }, () => {
        this.filterOrders()
      })
    }).catch(err => {
      // 停止下拉刷新动画
      wx.stopPullDownRefresh()
      console.error('加载订单列表失败', err)
      wx.showToast({
        title: err.message || '加载失败',
        icon: 'none'
      })
    })
  },

  // 切换筛选
  switchFilter(e) {
    const status = e.currentTarget.dataset.status
    
    // 切换筛选时重新加载订单数据，确保数据是最新的
    this.setData({
      currentFilter: status
    }, () => {
      this.loadOrders()
    })
  },

  // 筛选订单
  filterOrders() {
    const { orders, currentFilter } = this.data
    let filteredOrders = orders
    
    if (currentFilter !== 0) {
      filteredOrders = orders.filter(order => order.orderStatus === currentFilter)
    }
    
    this.setData({ filteredOrders })
  },

  // 查看订单详情
  viewOrderDetail(e) {
    const { id } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/order-detail/order-detail?id=${id}`
    })
  },

  // 继续支付
  continuePay(e) {
    const { id } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/payment/payment?orderId=${id}`
    })
  },

  // 继续点餐（待支付订单）
  continueOrder(e) {
    const { id } = e.currentTarget.dataset
    const order = this.data.orders.find(o => o.id === id)
    
    if (!order) return
    
    // 保存订单信息到本地存储
    wx.setStorageSync('pendingOrderId', id)
    wx.setStorageSync('pendingOrderTableId', order.tableId)
    wx.setStorageSync('continueOrderMode', true)
    
    console.log('继续点餐，订单ID：', id, '餐台ID：', order.tableId)
    
    // 跳转到首页继续点餐
    wx.switchTab({
      url: '/pages/index/index'
    })
  },

  // 删除订单
  deleteOrder(e) {
    const { id } = e.currentTarget.dataset
    
    wx.showModal({
      title: '提示',
      content: '确定要删除该订单吗？',
      success: (res) => {
        if (res.confirm) {
          wx.showLoading({
            title: '删除中...'
          })
          
          orderApi.deleteOrder(id).then(() => {
            wx.hideLoading()
            wx.showToast({
              title: '删除成功',
              icon: 'success'
            })
            // 刷新订单列表
            this.loadOrders()
          }).catch(err => {
            wx.hideLoading()
            console.error('删除订单失败', err)
            wx.showToast({
              title: err.message || '删除失败',
              icon: 'none'
            })
          })
        }
      }
    })
  },

  // 再来一单
  reorder(e) {
    const { id } = e.currentTarget.dataset
    const order = this.data.orders.find(order => order.id === id)
    
    if (!order || !order.details || order.details.length === 0) {
      wx.showToast({
        title: '订单信息不完整',
        icon: 'none'
      })
      return
    }
    
    wx.showModal({
      title: '再来一单',
      content: `将该订单的 ${order.details.length} 个菜品加入购物车？`,
      confirmText: '确定',
      cancelText: '取消',
      success: (res) => {
        if (res.confirm) {
          // 将订单明细转换为购物车格式
          const cartItems = order.details.map(detail => ({
            dishId: detail.dishId || null,
            setmealId: detail.setmealId || null,
            name: detail.name,
            image: detail.image,
            price: parseFloat(detail.price),
            quantity: detail.quantity,
            amount: parseFloat(detail.amount),
            flavorJson: detail.flavor || null
          }))
          
          // 保存到本地存储，供首页读取
          wx.setStorageSync('reorderCartItems', cartItems)
          
          wx.showToast({
            title: '已加入购物车',
            icon: 'success',
            duration: 1500
          })
          
          // 跳转到首页
          setTimeout(() => {
            wx.switchTab({
              url: '/pages/index/index'
            })
          }, 1500)
        }
      }
    })
  }
})
