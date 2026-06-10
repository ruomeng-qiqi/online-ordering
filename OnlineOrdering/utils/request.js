// 请求封装
const { baseURL } = require('../config/api.js')

/**
 * 封装 wx.request
 */
const request = (options) => {
  return new Promise((resolve, reject) => {
    // 获取 token
    const token = wx.getStorageSync('token')
    
    wx.request({
      url: baseURL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.header
      },
      success: (res) => {
        // 后端返回的成功状态码是 200
        if (res.data.code === 200) {
          resolve(res.data)
        } else if (res.data.code === 401) {
          // token 失效，提示用户重新登录
          wx.showToast({
            title: '登录已过期，请重新登录',
            icon: 'none'
          })
          // 清除本地存储
          wx.removeStorageSync('token')
          wx.removeStorageSync('customerId')
          wx.removeStorageSync('userInfo')
          // 跳转到"我的"页面
          wx.switchTab({
            url: '/pages/mine/mine'
          })
          reject(res.data)
        } else if (res.data.code === 403) {
          // 账号被禁用
          wx.showModal({
            title: '账号已禁用',
            content: '您的账号已被禁用，无法使用小程序。如有疑问，请联系客服。',
            showCancel: false,
            confirmText: '我知道了',
            success: () => {
              // 清除本地存储
              wx.removeStorageSync('token')
              wx.removeStorageSync('customerId')
              wx.removeStorageSync('userInfo')
              // 跳转到"我的"页面
              wx.switchTab({
                url: '/pages/mine/mine'
              })
            }
          })
          reject(res.data)
        } else {
          // 请求失败
          wx.showToast({
            title: res.data.message || '请求失败',
            icon: 'none'
          })
          reject(res.data)
        }
      },
      fail: (err) => {
        // 网络错误
        wx.showToast({
          title: '网络错误',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

module.exports = {
  request
}

