// pages/order-detail/order-detail.js
import { mockOrders } from '../../data/mockOrderData.js'

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
      orderStatus: null,  // null 避免闪烁
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
    // TODO: 调用后端API获取订单详情
    // GET /api/order/{id}
    
    // 使用模拟数据
    setTimeout(() => {
      // 从 mockOrderData 中查找对应的订单
      const mockOrder = mockOrders.find(order => order.id == id)
      
      if (!mockOrder) {
        wx.showToast({
          title: '订单不存在',
          icon: 'none'
        })
        return
      }

      // 解析口味信息
      const details = mockOrder.details.map(item => {
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
        order: {
          ...mockOrder,
          tableNumber: mockOrder.tableName
        },
        orderDetails: details,
        actualAmount: actualAmount.toFixed(2),
        orderStatusText: this.getOrderStatusText(mockOrder.orderStatus),
        paymentMethodText: this.getPaymentMethodText(mockOrder.paymentMethod)
      })
    }, 300)
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
            title: '订单已删除',
            icon: 'success',
            duration: 2000
          })
          
          // 返回订单列表
          setTimeout(() => {
            wx.navigateBack()
          }, 2000)
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
          // 参数: this.data.orderDetails
          
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
