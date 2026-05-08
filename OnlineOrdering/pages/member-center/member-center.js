// pages/member-center/member-center.js
Page({
  data: {
    memberInfo: {
      nickName: '张三',
      joinTime: '2026-04-01',
      currentPoints: 1580,
      totalPoints: 3200
    }
  },

  onLoad() {
    this.loadMemberInfo()
  },

  loadMemberInfo() {
    // TODO: 调用后端API获取会员信息
    // GET /api/customer/member-info
    console.log('加载会员信息')
  },

  // 跳转到积分记录
  goToPointsRecord() {
    wx.navigateTo({
      url: '/pages/points-record/points-record'
    })
  }
})
