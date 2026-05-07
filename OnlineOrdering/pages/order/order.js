// pages/order/order.js
import { mockOrders, orderStatusText, orderStatusColor } from '../../data/mockOrderData.js'

Page({
  data: {
    orders: [],
    filteredOrders: [],
    currentFilter: 0, // 0-全部, 1-待支付, 2-已完成, 3-已取消
    orderStatusText,
    orderStatusColor
  },

  onLoad() {
    this.loadOrders()
  },

  onShow() {
    // 每次显示页面时刷新订单列表
    this.loadOrders()
  },

  // 加载订单列表
  loadOrders() {
    // TODO: 调用后端API获取订单列表
    // GET /api/order/list?customerId={customerId}
    
    // 使用模拟数据，按创建时间倒序排列
    const orders = mockOrders.sort((a, b) => {
      // 将日期格式转换为 iOS 兼容格式
      const dateA = new Date(a.createTime.replace(/ /g, 'T'))
      const dateB = new Date(b.createTime.replace(/ /g, 'T'))
      return dateB - dateA
    }).map(order => {
      // 计算订单总件数
      const totalQuantity = order.details.reduce((sum, item) => sum + item.quantity, 0)
      return {
        ...order,
        totalQuantity
      }
    })
    
    this.setData({ orders }, () => {
      this.filterOrders()
    })
  },

  // 切换筛选
  switchFilter(e) {
    const status = e.currentTarget.dataset.status
    this.setData({
      currentFilter: status
    }, () => {
      this.filterOrders()
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
          
          // 模拟取消订单
          const orders = this.data.orders.map(order => {
            if (order.id === id) {
              return {
                ...order,
                orderStatus: 3,
                cancelReason: '用户取消'
              }
            }
            return order
          })
          
          this.setData({ orders }, () => {
            this.filterOrders()
          })
          
          wx.showToast({
            title: '订单已取消',
            icon: 'success'
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
          
          // 模拟删除订单
          const orders = this.data.orders.filter(order => order.id !== id)
          
          this.setData({ orders }, () => {
            this.filterOrders()
          })
          
          wx.showToast({
            title: '订单已删除',
            icon: 'success'
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
          // 参数: order.details
          
          wx.showToast({
            title: '已加入购物车',
            icon: 'success',
            duration: 2000
          })
          
          // 跳转到首页
          setTimeout(() => {
            wx.switchTab({
              url: '/pages/index/index'
            })
          }, 2000)
        }
      }
    })
  }
})
