// pages/points-record/points-record.js
Page({
  data: {
    isMember: true, // 是否会员，从用户信息获取
    currentPoints: 1580,
    totalPoints: 3200,
    records: []
  },

  onLoad() {
    this.loadUserInfo()
  },

  onShow() {
    this.loadUserInfo()
  },

  // 加载用户信息
  loadUserInfo() {
    // TODO: 调用后端API获取用户信息
    // GET /api/customer/info
    
    // 模拟数据 - 从全局或缓存中获取用户信息
    const isMember = true // 实际应该从后端API获取
    
    this.setData({ isMember })
    
    if (isMember) {
      this.loadPointsRecords()
    }
  },

  // 加载积分记录
  loadPointsRecords() {
    // TODO: 调用后端API获取积分记录
    // GET /api/points-record/list?customerId={customerId}
    
    // 模拟数据
    const records = [
      {
        id: 1,
        customerId: 1,
        type: 1, // 1-订单获得，2-积分抵扣，3-手动调整
        points: 100,
        orderId: 1,
        remark: '订单消费获得',
        createTime: '2026-05-07 16:30:00'
      },
      {
        id: 2,
        customerId: 1,
        type: 2,
        points: -2000,
        orderId: 1,
        remark: '订单消费抵扣',
        createTime: '2026-05-07 15:20:00'
      },
      {
        id: 3,
        customerId: 1,
        type: 1,
        points: 80,
        orderId: 3,
        remark: '订单消费获得',
        createTime: '2026-05-05 20:00:00'
      },
      {
        id: 4,
        customerId: 1,
        type: 3,
        points: 500,
        orderId: null,
        remark: '管理员手动调整',
        createTime: '2026-05-01 10:00:00'
      },
      {
        id: 5,
        customerId: 1,
        type: 1,
        points: 150,
        orderId: 6,
        remark: '订单消费获得',
        createTime: '2026-05-03 17:45:00'
      },
      {
        id: 6,
        customerId: 1,
        type: 2,
        points: -5000,
        orderId: 2,
        remark: '订单消费抵扣',
        createTime: '2026-05-06 12:30:00'
      },
      {
        id: 7,
        customerId: 1,
        type: 2,
        points: -5000,
        orderId: 2,
        remark: '订单消费抵扣',
        createTime: '2026-05-06 12:30:00'
      }
    ]

    // 添加类型文本
    const typeTextMap = {
      1: '订单获得',
      2: '积分抵扣',
      3: '手动调整'
    }

    const processedRecords = records.map(record => ({
      ...record,
      typeText: typeTextMap[record.type]
    }))

    this.setData({ 
      records: processedRecords,
      currentPoints: 1580,
      totalPoints: 3200
    })
  },

  // 跳转到加入会员
  goToJoinMember() {
    wx.showModal({
      title: '加入会员',
      content: '成为会员即可享受消费积分、积分抵扣等权益',
      confirmText: '立即加入',
      success: (res) => {
        if (res.confirm) {
          // TODO: 跳转到会员开通页面或调用开通接口
          wx.showToast({
            title: '功能开发中',
            icon: 'none'
          })
        }
      }
    })
  },

  // 跳转到点餐页面
  goToOrder() {
    wx.switchTab({
      url: '/pages/index/index'
    })
  }
})
