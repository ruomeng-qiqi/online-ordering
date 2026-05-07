// pages/mine/mine.js
Page({
  data: {
    userInfo: null,
    hasUserInfo: false,
    menuList: [
      { icon: '📋', title: '我的订单', url: '/pages/order/order' },
      { icon: '⭐', title: '我的收藏', url: '' },
      { icon: '📍', title: '收货地址', url: '' },
      { icon: '⚙️', title: '设置', url: '' }
    ]
  },

  onLoad() {
    this.getUserInfo()
  },

  onShow() {
    this.getUserInfo()
  },

  getUserInfo() {
    // TODO: 获取用户信息
    const userInfo = wx.getStorageSync('userInfo')
    if (userInfo) {
      this.setData({
        userInfo,
        hasUserInfo: true
      })
    }
  },

  handleMenuClick(e) {
    const { url } = e.currentTarget.dataset
    if (url) {
      wx.navigateTo({ url })
    } else {
      wx.showToast({
        title: '功能开发中',
        icon: 'none'
      })
    }
  }
})
