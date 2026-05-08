// pages/mine/mine.js
Page({
  data: {
    isLogin: false, // 登录状态，改为 true 可以看到已登录状态
    userInfo: {
      avatarUrl: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      nickName: '张三',
      isMember: false, // 改为 false 可以看到非会员状态
      points: 1580,
      greeting: '欢迎回来，今天想吃点什么呢～'
    },
    menuList: [
      { title: '我的订单', url: '/pages/order-list/order-list', needLogin: true },
      { title: '积分记录', url: '/pages/points-record/points-record', needLogin: true },
      { title: '关于我们', url: '/pages/about/about', needLogin: false },
      { title: '设置', url: '/pages/settings/settings', needLogin: false }
    ]
  },

  onLoad() {
    this.checkLoginStatus()
  },

  onShow() {
    this.checkLoginStatus()
  },

  // 检查登录状态
  checkLoginStatus() {
    // 从本地存储获取登录状态和用户信息
    const token = wx.getStorageSync('token')
    const userInfo = wx.getStorageSync('userInfo')
    
    if (token && userInfo) {
      this.setData({ 
        isLogin: true,
        userInfo: userInfo
      })
      // 可选：验证token是否有效
      // this.validateToken(token)
    } else {
      this.setData({ isLogin: false })
    }
  },

  loadUserData() {
    // TODO: 调用后端API获取用户信息
    // GET /api/customer/info
    console.log('加载用户数据')
  },

  // 处理登录
  handleLogin() {
    wx.showLoading({
      title: '登录中...'
    })

    // 调用微信登录
    wx.login({
      success: (res) => {
        if (res.code) {
          // TODO: 将 code 发送到后端
          // POST /api/customer/login
          // 参数: { code: res.code }
          // 后端返回 token 和用户信息
          
          console.log('登录code:', res.code)
          
          // 模拟登录成功，实际应该调用后端接口
          setTimeout(() => {
            wx.hideLoading()
            
            // 模拟后端返回的数据
            const mockToken = 'mock_token_' + Date.now()
            const mockUserInfo = {
              avatarUrl: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
              nickName: '张三',
              isMember: false,
              points: 1580,
              greeting: '欢迎回来，今天想吃点什么呢～'
            }
            
            // 保存到本地存储
            wx.setStorageSync('token', mockToken)
            wx.setStorageSync('userInfo', mockUserInfo)
            
            this.setData({ 
              isLogin: true,
              userInfo: mockUserInfo
            })
            
            wx.showToast({
              title: '登录成功',
              icon: 'success'
            })
            
            this.loadUserData()
          }, 1000)
        } else {
          wx.hideLoading()
          wx.showToast({
            title: '登录失败',
            icon: 'none'
          })
        }
      },
      fail: () => {
        wx.hideLoading()
        wx.showToast({
          title: '登录失败',
          icon: 'none'
        })
      }
    })
  },

  // 点击用户信息区域
  handleUserClick() {
    if (!this.data.isLogin) {
      this.handleLogin()
      return
    }
    
    // 跳转到个人信息页面（可选）
    wx.showToast({
      title: '功能开发中',
      icon: 'none'
    })
  },

  // 点击积分
  handlePointsClick() {
    // 跳转到积分记录页面
    wx.navigateTo({
      url: '/pages/points-record/points-record'
    })
  },

  // 点击加入会员
  handleJoinMember() {
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

  // 点击会员中心
  handleMemberCenter() {
    // 跳转到会员中心页面
    wx.navigateTo({
      url: '/pages/member-center/member-center'
    })
  },

  handleMenuClick(e) {
    const { url, needLogin } = e.currentTarget.dataset
    
    // 检查是否需要登录
    if (needLogin && !this.data.isLogin) {
      wx.showModal({
        title: '提示',
        content: '请先登录',
        confirmText: '去登录',
        success: (res) => {
          if (res.confirm) {
            this.handleLogin()
          }
        }
      })
      return
    }
    
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
