// pages/settings/settings.js
Page({
  data: {
    userInfo: {
      avatarUrl: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      nickName: '张三',
      gender: 1 // 0-未知，1-男，2-女
    },
    genderText: '男',
    showNicknameModal: false,
    newNickname: ''
  },

  onLoad() {
    this.loadUserInfo()
  },

  onShow() {
    this.loadUserInfo()
  },

  // 加载用户信息
  loadUserInfo() {
    // 从本地存储获取用户信息
    const userInfo = wx.getStorageSync('userInfo')
    
    if (userInfo) {
      const genderMap = {
        0: '未知',
        1: '男',
        2: '女'
      }
      
      this.setData({
        userInfo: userInfo,
        genderText: genderMap[userInfo.gender || 0]
      })
    }
  },

  // 修改头像
  changeAvatar() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFilePath = res.tempFiles[0].tempFilePath
        
        // TODO: 上传图片到服务器
        // POST /api/upload/image
        // 然后更新用户头像
        // PUT /api/customer/avatar
        
        const updatedUserInfo = {
          ...this.data.userInfo,
          avatarUrl: tempFilePath
        }
        
        // 更新本地存储
        wx.setStorageSync('userInfo', updatedUserInfo)
        
        this.setData({
          userInfo: updatedUserInfo
        })
        
        wx.showToast({
          title: '头像已更新',
          icon: 'success'
        })
      }
    })
  },

  // 打开修改昵称弹窗
  changeNickname() {
    this.setData({
      showNicknameModal: true,
      newNickname: this.data.userInfo.nickName
    })
  },

  // 关闭昵称弹窗
  closeNicknameModal() {
    this.setData({
      showNicknameModal: false
    })
  },

  // 阻止事件冒泡
  stopPropagation() {},

  // 昵称输入
  onNicknameInput(e) {
    this.setData({
      newNickname: e.detail.value
    })
  },

  // 确认修改昵称
  confirmNickname() {
    const { newNickname } = this.data
    
    if (!newNickname || !newNickname.trim()) {
      wx.showToast({
        title: '昵称不能为空',
        icon: 'none'
      })
      return
    }

    // TODO: 调用后端API更新昵称
    // PUT /api/customer/nickname
    
    const updatedUserInfo = {
      ...this.data.userInfo,
      nickName: newNickname
    }
    
    // 更新本地存储
    wx.setStorageSync('userInfo', updatedUserInfo)
    
    this.setData({
      userInfo: updatedUserInfo,
      showNicknameModal: false
    })
    
    wx.showToast({
      title: '昵称已更新',
      icon: 'success'
    })
  },

  // 退出登录
  logout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          // TODO: 清除登录状态
          wx.clearStorage()
          
          wx.showToast({
            title: '已退出登录',
            icon: 'success',
            duration: 1500
          })
          
          // 跳转到登录页或首页
          setTimeout(() => {
            wx.reLaunch({
              url: '/pages/index/index'
            })
          }, 1500)
        }
      }
    })
  }
})
