// pages/order-list/order-list.js
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
    console.log('订单列表页面显示，自动刷新数据')
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
    
    // 如果点击的是当前已选中的筛选项，也要刷新数据
    this.setData({
      currentFilter: status
    }, () => {
      // 切换筛选时重新加载订单数据，确保数据是最新的
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

  // 取消订单
  cancelOrder(e) {
    const { id } = e.currentTarget.dataset
    
    wx.showModal({
      title: '提示',
      content: '确定要取消该订单吗？',
      success: (res) => {
        if (res.confirm) {
          // TODO: 调用后端API取消订单
          // PUT /api/order/cancel/{id}
          
          wx.showToast({
            title: '功能开发中',
            icon: 'none'
          })
        }
      }
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
  reorder(e) {
    const { id } = e.currentTarget.dataset
    const order = this.data.orders.find(order => order.id === id)
    
    if (!order) return
    
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
