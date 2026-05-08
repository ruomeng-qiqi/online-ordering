// pages/about/about.js
Page({
  data: {

  },

  onLoad(options) {

  },

  // 拨打电话
  makePhoneCall(e) {
    const { phone } = e.currentTarget.dataset
    wx.makePhoneCall({
      phoneNumber: phone,
      fail: (err) => {
        console.error('拨打电话失败', err)
        wx.showToast({
          title: '拨打失败',
          icon: 'none'
        })
      }
    })
  }
})
