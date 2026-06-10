// pages/member-center/member-center.js
Page({
  data: {
    customerId: null,
    memberInfo: {
      nickName: '',
      joinTime: '',
      currentPoints: 0,
      totalPoints: 0
    }
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
    this.loadMemberInfo()
  },

  onShow() {
    if (this.data.customerId) {
      this.loadMemberInfo()
    }
  },

  loadMemberInfo() {
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
        wx.hideLoading()
        
        if (res.data.code === 200) {
          const userData = res.data.data
          
          // 检查是否是会员
          if (userData.isMember !== 1) {
            wx.showModal({
              title: '提示',
              content: '您还不是会员，请先加入会员',
              showCancel: false,
              success: () => {
                wx.navigateBack()
              }
            })
            return
          }
          
          // 格式化加入时间
          let joinTime = userData.createTime
          if (joinTime) {
            joinTime = joinTime.replace('T', ' ').split(' ')[0]
          }
          
          this.setData({
            memberInfo: {
              nickName: userData.nickname || '会员',
              joinTime: joinTime || '-',
              currentPoints: userData.points || 0,
              totalPoints: userData.totalPoints || 0
            }
          })
        } else {
          wx.showToast({
            title: res.data.message || '加载失败',
            icon: 'none'
          })
        }
      },
      fail: (err) => {
        wx.hideLoading()
        console.error('加载会员信息失败', err)
        wx.showToast({
          title: '加载失败',
          icon: 'none'
        })
      }
    })
  },

  // 跳转到积分记录
  goToPointsRecord() {
    wx.navigateTo({
      url: '/pages/points-record/points-record'
    })
  }
})
