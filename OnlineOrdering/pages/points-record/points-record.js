// pages/points-record/points-record.js
Page({
  data: {
    isMember: false,
    customerId: null,
    currentPoints: 0,
    totalPoints: 0,
    records: []
  },

  onLoad() {
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
    
    this.setData({ customerId })
    this.loadUserInfo()
  },

  onShow() {
    if (this.data.customerId) {
      this.loadUserInfo()
    }
  },

  // 加载用户信息
  loadUserInfo() {
    wx.showLoading({
      title: '加载中...'
    })
    
    wx.request({
      url: getApp().globalData.baseUrl + '/user/customer/info',
      method: 'GET',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      success: (res) => {
        if (res.data.code === 200) {
          const userData = res.data.data
          
          this.setData({
            isMember: userData.isMember === 1,
            currentPoints: userData.points || 0,
            totalPoints: userData.totalPoints || 0
          })
          
          if (userData.isMember === 1) {
            this.loadPointsRecords()
          } else {
            wx.hideLoading()
          }
        } else {
          wx.hideLoading()
          wx.showToast({
            title: res.data.message || '加载失败',
            icon: 'none'
          })
        }
      },
      fail: (err) => {
        wx.hideLoading()
        console.error('加载用户信息失败', err)
        wx.showToast({
          title: '加载失败',
          icon: 'none'
        })
      }
    })
  },

  // 加载积分记录
  loadPointsRecords() {
    wx.request({
      url: getApp().globalData.baseUrl + '/user/customer/points-records',
      method: 'GET',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      success: (res) => {
        wx.hideLoading()
        
        if (res.data.code === 200) {
          const records = res.data.data || []
          
          // 添加类型文本
          const typeTextMap = {
            1: '订单获得',
            2: '积分抵扣',
            3: '手动调整'
          }
          
          const processedRecords = records.map(record => {
            // 处理时间格式
            let createTime = record.createTime
            if (createTime) {
              createTime = createTime.replace('T', ' ')
              // 只保留到秒
              if (createTime.includes('.')) {
                createTime = createTime.split('.')[0]
              }
            }
            
            return {
              ...record,
              typeText: typeTextMap[record.type],
              createTime: createTime
            }
          })
          
          this.setData({ records: processedRecords })
        } else {
          wx.showToast({
            title: res.data.message || '加载失败',
            icon: 'none'
          })
        }
      },
      fail: (err) => {
        wx.hideLoading()
        console.error('加载积分记录失败', err)
        wx.showToast({
          title: '加载失败',
          icon: 'none'
        })
      }
    })
  },

  // 跳转到加入会员
  goToJoinMember() {
    wx.showModal({
      title: '加入会员',
      content: '成为会员即可享受消费积分、积分抵扣等权益',
      confirmText: '立即加入',
      cancelText: '取消',
      success: (res) => {
        if (res.confirm) {
          this.joinMember()
        }
      }
    })
  },

  /**
   * 调用后端加入会员接口
   */
  joinMember() {
    wx.showLoading({
      title: '加入中...'
    })
    
    wx.request({
      url: getApp().globalData.baseUrl + '/user/customer/join-member',
      method: 'POST',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token'),
        'Content-Type': 'application/json'
      },
      data: {},
      success: (res) => {
        wx.hideLoading()
        
        if (res.data.code === 200) {
          wx.showModal({
            title: '恭喜您',
            content: '成功加入会员！快去消费获取积分吧～',
            showCancel: false,
            confirmText: '好的',
            success: () => {
              // 刷新页面
              this.loadUserInfo()
            }
          })
        } else {
          wx.showModal({
            title: '提示',
            content: res.data.message || '加入会员失败',
            showCancel: false
          })
        }
      },
      fail: (err) => {
        wx.hideLoading()
        console.error('加入会员失败', err)
        wx.showToast({
          title: '加入失败',
          icon: 'none'
        })
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
