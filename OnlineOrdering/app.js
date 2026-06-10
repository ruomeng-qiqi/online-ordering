// app.js
const apiConfig = require('./config/api.js')

App({
  onLaunch(options) {
    // 展示本地存储能力
    const logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)

    // 检查登录状态
    this.checkLoginStatus()
    
    // 处理扫码进入小程序的场景
    this.handleScanScene(options)
  },
  
  onShow(options) {
    // 每次小程序显示时也处理扫码场景
    this.handleScanScene(options)
  },
  
  /**
   * 处理扫码场景
   */
  handleScanScene(options) {
    console.log('小程序启动参数：', options)
    
    // 场景值 1047 和 1048 表示扫描小程序码进入
    // 场景值 1011 表示扫描二维码进入
    if (options.scene === 1047 || options.scene === 1048 || options.scene === 1011) {
      // 获取场景值参数
      const scene = decodeURIComponent(options.query.scene || options.scene || '')
      console.log('扫码场景值：', scene)
      
      // 解析场景值参数：tableNumber=A01
      if (scene) {
        const params = this.parseSceneParams(scene)
        console.log('解析后的参数：', params)
        
        if (params.tableNumber) {
          // 保存餐台号到全局数据和本地存储
          this.globalData.tableNumber = params.tableNumber
          wx.setStorageSync('tableNumber', params.tableNumber)
          console.log('保存餐台号：', params.tableNumber)
        }
      }
    } else {
      // 非扫码进入，清除餐台信息，要求重新扫码
      console.log('非扫码进入，清除餐台信息')
      this.clearTableInfo()
    }
  },
  
  /**
   * 清除餐台信息
   */
  clearTableInfo() {
    this.globalData.tableNumber = null
    this.globalData.tableId = null
    wx.removeStorageSync('tableNumber')
    wx.removeStorageSync('tableId')
    wx.removeStorageSync('tableName')
  },
  
  /**
   * 解析场景值参数
   * 例如：tableNumber=A01 => { tableNumber: 'A01' }
   */
  parseSceneParams(scene) {
    const params = {}
    const pairs = scene.split('&')
    
    pairs.forEach(pair => {
      const [key, value] = pair.split('=')
      if (key && value) {
        params[key] = value
      }
    })
    
    return params
  },

  /**
   * 检查登录状态
   */
  checkLoginStatus() {
    const token = wx.getStorageSync('token')
    const userInfo = wx.getStorageSync('userInfo')
    
    if (token && userInfo) {
      // 检查账号状态
      if (userInfo.status === 0) {
        console.log('账号已禁用，清除登录信息')
        this.clearLoginInfo()
        wx.showModal({
          title: '账号已禁用',
          content: '您的账号已被禁用，无法使用小程序。如有疑问，请联系客服。',
          showCancel: false,
          confirmText: '我知道了'
        })
        return
      }
      
      this.globalData.userInfo = userInfo
      console.log('用户已登录', userInfo)
    } else {
      console.log('用户未登录')
    }
  },

  /**
   * 获取用户信息
   */
  getUserInfo() {
    return this.globalData.userInfo
  },

  /**
   * 设置用户信息
   */
  setUserInfo(userInfo) {
    this.globalData.userInfo = userInfo
    wx.setStorageSync('userInfo', userInfo)
  },

  /**
   * 清除登录信息
   */
  clearLoginInfo() {
    this.globalData.userInfo = null
    wx.removeStorageSync('token')
    wx.removeStorageSync('customerId')
    wx.removeStorageSync('userInfo')
  },

  globalData: {
    baseUrl: apiConfig.baseURL,  // API 基础地址
    userInfo: null,
    tableNumber: null,  // 当前餐台号
    tableId: null       // 当前餐台ID
  }
})
