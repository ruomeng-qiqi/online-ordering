// pages/mine/mine.js
const authApi = require('../../api/auth.js')

Page({
  data: {
    isLogin: false,
    userInfo: {
      avatarUrl: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      nickName: '微信用户',
      isMember: 0,
      points: 0,
      greeting: '欢迎回来，今天想吃点什么呢～'
    },
    menuList: []
  },

  onLoad() {
    this.checkLoginStatus()
  },

  onShow() {
    this.checkLoginStatus()
  },

  /**
   * 检查登录状态
   */
  checkLoginStatus() {
    const token = wx.getStorageSync('token')
    const customerId = wx.getStorageSync('customerId')
    
    if (token && customerId) {
      // 已登录，从后端获取最新用户信息
      this.loadUserInfo(customerId)
    } else {
      // 未登录
      this.setData({ 
        isLogin: false,
        menuList: this.getMenuList(false)
      })
    }
  },

  /**
   * 从后端加载用户信息
   */
  loadUserInfo(customerId) {
    wx.request({
      url: getApp().globalData.baseUrl + '/user/customer/info',
      method: 'GET',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      success: (res) => {
        if (res.data.code === 200) {
          const userData = res.data.data
          
          // 检查账号状态
          if (userData.status === 0) {
            wx.showModal({
              title: '账号已禁用',
              content: '您的账号已被禁用，无法使用小程序。如有疑问，请联系客服。',
              showCancel: false,
              confirmText: '我知道了',
              success: () => {
                // 清除登录信息
                getApp().clearLoginInfo()
                this.setData({ 
                  isLogin: false,
                  menuList: this.getMenuList(false)
                })
              }
            })
            return
          }
          
          // 更新本地存储
          const userInfo = {
            customerId: userData.id,
            nickname: userData.nickname,
            avatar: userData.avatar,
            isMember: userData.isMember,
            points: userData.points,
            status: userData.status
          }
          wx.setStorageSync('userInfo', userInfo)
          
          // 更新页面数据
          this.setData({ 
            isLogin: true,
            userInfo: {
              avatarUrl: userData.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
              nickName: userData.nickname || '微信用户',
              isMember: userData.isMember,
              points: userData.points || 0,
              greeting: '欢迎回来，今天想吃点什么呢～'
            },
            menuList: this.getMenuList(userData.isMember === 1)
          })
        } else {
          // 获取失败，使用本地缓存
          this.loadFromCache()
        }
      },
      fail: (err) => {
        console.error('加载用户信息失败', err)
        // 获取失败，使用本地缓存
        this.loadFromCache()
      }
    })
  },

  /**
   * 从缓存加载用户信息
   */
  loadFromCache() {
    const userInfo = wx.getStorageSync('userInfo')
    if (userInfo) {
      this.setData({ 
        isLogin: true,
        userInfo: {
          avatarUrl: userInfo.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
          nickName: userInfo.nickname || '微信用户',
          isMember: userInfo.isMember,
          points: userInfo.points || 0,
          greeting: '欢迎回来，今天想吃点什么呢～'
        },
        menuList: this.getMenuList(userInfo.isMember === 1)
      })
    }
  },

  /**
   * 根据会员状态获取菜单列表
   */
  getMenuList(isMember) {
    const baseMenu = [
      { title: '我的订单', url: '/pages/order-list/order-list', needLogin: true },
      { title: '关于我们', url: '/pages/about/about', needLogin: false },
      { title: '设置', url: '/pages/settings/settings', needLogin: false }
    ]
    
    // 会员才显示积分记录
    if (isMember) {
      baseMenu.splice(1, 0, { title: '积分记录', url: '/pages/points-record/points-record', needLogin: true })
    }
    
    return baseMenu
  },

  /**
   * 处理登录
   */
  handleLogin() {
    wx.showLoading({
      title: '登录中...',
      mask: true
    })

    // 1. 调用微信登录获取 code
    wx.login({
      success: (res) => {
        if (res.code) {
          console.log('获取到 code:', res.code)
          
          // 2. 直接使用 code 登录（不获取用户信息）
          // 注意：wx.getUserProfile 在开发者工具中可能会卡住
          // 如需获取用户信息，请在真机上测试
          this.loginToBackend(res.code, null)
          
          // 如果需要获取用户信息，取消下面的注释，注释掉上面的代码
          /*
          wx.getUserProfile({
            desc: '用于完善用户资料',
            success: (userRes) => {
              console.log('获取用户信息成功', userRes.userInfo)
              this.loginToBackend(res.code, userRes.userInfo)
            },
            fail: (err) => {
              console.log('获取用户信息失败，使用默认信息登录', err)
              this.loginToBackend(res.code, null)
            }
          })
          */
        } else {
          wx.hideLoading()
          wx.showToast({
            title: '获取登录凭证失败',
            icon: 'none'
          })
        }
      },
      fail: (err) => {
        console.error('wx.login 失败', err)
        wx.hideLoading()
        wx.showToast({
          title: '登录失败',
          icon: 'none'
        })
      }
    })
  },

  /**
   * 调用后端登录接口
   */
  loginToBackend(code, userInfo) {
    const loginData = {
      code: code
    }

    authApi.login(loginData).then(result => {
      wx.hideLoading()
      
      // 检查账号状态
      if (result.data.status === 0) {
        wx.showModal({
          title: '账号已禁用',
          content: '您的账号已被禁用，无法登录。如有疑问，请联系客服。',
          showCancel: false,
          confirmText: '我知道了'
        })
        return
      }
      
      // 保存 token 和用户信息
      wx.setStorageSync('token', result.data.token)
      wx.setStorageSync('customerId', result.data.customerId)
      wx.setStorageSync('userInfo', result.data)
      
      console.log('登录成功', result.data)
      
      // 更新页面状态
      this.setData({ 
        isLogin: true,
        userInfo: {
          avatarUrl: result.data.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
          nickName: result.data.nickname || '微信用户',
          isMember: result.data.isMember,
          points: result.data.points || 0,
          greeting: '欢迎回来，今天想吃点什么呢～'
        },
        menuList: this.getMenuList(result.data.isMember === 1)
      })
      
      wx.showToast({
        title: '登录成功',
        icon: 'success'
      })
    }).catch(err => {
      wx.hideLoading()
      console.error('登录失败', err)
      wx.showToast({
        title: err.message || '登录失败',
        icon: 'none'
      })
    })
  },

  /**
   * 点击积分
   */
  handlePointsClick() {
    if (!this.data.userInfo.isMember) {
      wx.showModal({
        title: '提示',
        content: '请先加入会员',
        showCancel: false
      })
      return
    }
    
    wx.navigateTo({
      url: '/pages/points-record/points-record'
    })
  },

  /**
   * 点击加入会员
   */
  handleJoinMember() {
    // 检查是否登录
    if (!this.data.isLogin) {
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
    const customerId = wx.getStorageSync('customerId')
    
    if (!customerId) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      })
      return
    }
    
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
              // 刷新用户信息
              this.loadUserInfo(customerId)
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

  /**
   * 点击会员中心
   */
  handleMemberCenter() {
    wx.navigateTo({
      url: '/pages/member-center/member-center'
    })
  },

  /**
   * 点击菜单项
   */
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
