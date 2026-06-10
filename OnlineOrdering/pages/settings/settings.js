// pages/settings/settings.js
Page({
  data: {
    customerId: null,
    userInfo: {
      avatarUrl: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      nickName: '微信用户',
      gender: 0 // 0-未知，1-男，2-女
    },
    genderText: '未知',
    showNicknameModal: false,
    newNickname: '',
    showGenderModal: false,
    newGender: 0
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
        wx.hideLoading()
        
        if (res.data.code === 200) {
          const userData = res.data.data
          const genderMap = {
            0: '未知',
            1: '男',
            2: '女'
          }
          
          const userInfo = {
            avatarUrl: userData.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
            nickName: userData.nickname || '微信用户',
            gender: userData.gender || 0
          }
          
          this.setData({
            userInfo: userInfo,
            genderText: genderMap[userInfo.gender]
          })
          
          // 更新本地存储
          wx.setStorageSync('userInfo', {
            ...userData,
            avatar: userInfo.avatarUrl,
            nickname: userInfo.nickName
          })
        }
      },
      fail: (err) => {
        wx.hideLoading()
        console.error('加载用户信息失败', err)
      }
    })
  },

  // 修改头像
  changeAvatar() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      sizeType: ['compressed'],
      success: (res) => {
        const tempFilePath = res.tempFiles[0].tempFilePath
        
        wx.showLoading({
          title: '上传中...'
        })
        
        // 上传图片到服务器
        wx.uploadFile({
          url: getApp().globalData.baseUrl + '/common/upload',
          filePath: tempFilePath,
          name: 'file',
          header: {
            'Authorization': 'Bearer ' + wx.getStorageSync('token')
          },
          formData: {
            'type': 'avatar'
          },
          success: (uploadRes) => {
            const data = JSON.parse(uploadRes.data)
            
            if (data.code === 200) {
              const avatarUrl = data.data
              
              // 更新用户头像
              this.updateUserInfo(null, avatarUrl, null)
            } else {
              wx.hideLoading()
              wx.showToast({
                title: data.message || '上传失败',
                icon: 'none'
              })
            }
          },
          fail: (err) => {
            wx.hideLoading()
            console.error('上传头像失败', err)
            wx.showToast({
              title: '上传失败',
              icon: 'none'
            })
          }
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

    if (newNickname.length > 20) {
      wx.showToast({
        title: '昵称不能超过20个字符',
        icon: 'none'
      })
      return
    }

    this.setData({
      showNicknameModal: false
    })
    
    // 更新用户昵称
    this.updateUserInfo(newNickname, null, null)
  },

  // 打开修改性别弹窗
  changeGender() {
    this.setData({
      showGenderModal: true,
      newGender: this.data.userInfo.gender
    })
  },

  // 关闭性别弹窗
  closeGenderModal() {
    this.setData({
      showGenderModal: false
    })
  },

  // 选择性别
  selectGender(e) {
    const gender = parseInt(e.currentTarget.dataset.gender)
    this.setData({
      newGender: gender,
      'userInfo.gender': gender
    })
  },

  // 确认修改性别
  confirmGender() {
    const { newGender } = this.data
    
    this.setData({
      showGenderModal: false
    })
    
    // 更新用户性别
    this.updateUserInfo(null, null, newGender)
  },

  // 更新用户信息
  updateUserInfo(nickname, avatar, gender) {
    wx.showLoading({
      title: '更新中...'
    })
    
    const requestData = {}
    
    if (nickname) {
      requestData.nickname = nickname
    }
    if (avatar) {
      requestData.avatar = avatar
    }
    if (gender !== null && gender !== undefined) {
      requestData.gender = gender
    }
    
    wx.request({
      url: getApp().globalData.baseUrl + '/user/customer/update-info',
      method: 'PUT',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token'),
        'Content-Type': 'application/json'
      },
      data: requestData,
      success: (res) => {
        wx.hideLoading()
        
        if (res.data.code === 200) {
          wx.showToast({
            title: '更新成功',
            icon: 'success'
          })
          
          // 刷新用户信息
          this.loadUserInfo()
        } else {
          wx.showToast({
            title: res.data.message || '更新失败',
            icon: 'none'
          })
        }
      },
      fail: (err) => {
        wx.hideLoading()
        console.error('更新用户信息失败', err)
        wx.showToast({
          title: '更新失败',
          icon: 'none'
        })
      }
    })
  },

  // 退出登录
  logout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          // 清除登录相关的缓存
          wx.removeStorageSync('token')
          wx.removeStorageSync('customerId')
          wx.removeStorageSync('userInfo')
          wx.removeStorageSync('tableId')
          wx.removeStorageSync('tableNumber')
          wx.removeStorageSync('tableName')
          
          wx.showToast({
            title: '已退出登录',
            icon: 'success',
            duration: 1500
          })
          
          // 跳转到"我的"页面
          setTimeout(() => {
            wx.switchTab({
              url: '/pages/mine/mine'
            })
          }, 1500)
        }
      }
    })
  }
})
