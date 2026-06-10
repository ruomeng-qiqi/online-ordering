// index.js
const { getCategoryList } = require('../../api/category.js')
const { getDishListByCategory } = require('../../api/dish.js')
const { getSetmealListByCategory } = require('../../api/setmeal.js')
const { getTableByNumber } = require('../../api/table.js')

Page({
  data: {
    shopInfo: {
      name: '在线点餐',
      description: '欢迎光临，祝您用餐愉快！',
      image: '/images/shop.png',
      tag1: '营业中',
      tag2: '新鲜食材',
      tag3: '干净卫生'
    },
    tableNumber: '',  // 餐台号
    tableId: null,    // 餐台ID
    tableName: '',    // 餐台名称
    isLoadingTable: false, // 是否正在加载餐台信息
    // 分类列表 - 对应 category 表
    categories: [],
    currentCategory: null,
    // 菜品列表 - 对应 dish 表和 dish_flavor 表
    dishes: [],
    // 套餐列表 - 对应 setmeal 表
    setmeals: [],
    // 购物车数据：存储不同口味的菜品和套餐
    cartItems: [],
    cartCount: 0,
    cartTotal: 0,
    // 口味选择弹窗
    showFlavorDialog: false,
    currentDish: null,
    selectedFlavors: {},
    // 当前弹窗中选择的口味对应的购物车项
    currentCartItem: null,
    // 购物车弹窗
    showCartDialog: false
  },

  onLoad(options) {
    // 处理扫码进入的场景参数
    if (options.scene) {
      const scene = decodeURIComponent(options.scene)
      console.log('页面接收到scene参数：', scene)
      
      // 解析场景值参数：tableNumber=A01
      const params = this.parseSceneParams(scene)
      if (params.tableNumber) {
        // 保存餐台号
        wx.setStorageSync('tableNumber', params.tableNumber)
        getApp().globalData.tableNumber = params.tableNumber
        console.log('从scene参数保存餐台号：', params.tableNumber)
        
        // 加载餐台信息
        this.loadTableInfo()
      }
    } else {
      // 非扫码进入，检查是否有餐台号
      const tableNumber = getApp().globalData.tableNumber || wx.getStorageSync('tableNumber')
      if (tableNumber && tableNumber !== '点击扫码') {
        // 有餐台号，加载餐台信息
        this.loadTableInfo()
      } else {
        // 没有餐台号，显示提示但不弹窗
        this.setData({
          tableNumber: '点击扫码'
        })
      }
    }
    
    // 加载分类数据
    this.loadCategories()
  },
  
  // 加载餐台信息
  loadTableInfo() {
    // 从全局数据或本地存储获取餐台号
    const app = getApp()
    let tableNumber = app.globalData.tableNumber || wx.getStorageSync('tableNumber')
    
    console.log('加载餐台信息，餐台号：', tableNumber)
    
    if (!tableNumber) {
      console.log('没有餐台号')
      this.setData({
        tableNumber: '点击扫码'
      })
      return
    }
    
    // 设置加载状态
    this.setData({
      isLoadingTable: true
    })
    
    // 根据餐台号查询餐台信息
    wx.showLoading({
      title: '加载中...'
    })
    
    getTableByNumber(tableNumber).then(result => {
      wx.hideLoading()
      
      const table = result.data
      
      console.log('餐台信息加载成功：', table)
      
      // 保存餐台信息
      this.setData({
        tableNumber: table.tableNumber,
        tableId: table.id,
        tableName: table.tableName,
        isLoadingTable: false
      })
      
      // 保存到本地存储
      wx.setStorageSync('tableId', table.id)
      wx.setStorageSync('tableNumber', table.tableNumber)
      wx.setStorageSync('tableName', table.tableName)
      
      // 保存到全局数据
      app.globalData.tableId = table.id
      app.globalData.tableNumber = table.tableNumber
      
    }).catch(err => {
      wx.hideLoading()
      console.error('加载餐台信息失败', err)
      
      this.setData({
        isLoadingTable: false,
        tableNumber: '点击扫码'
      })
      
      // 清除无效的餐台信息
      wx.removeStorageSync('tableNumber')
      wx.removeStorageSync('tableId')
      wx.removeStorageSync('tableName')
      app.globalData.tableNumber = null
      app.globalData.tableId = null
      
      wx.showToast({
        title: err.message || '餐台信息加载失败',
        icon: 'none'
      })
    })
  },
  
  // 扫描餐台二维码
  scanTableCode() {
    wx.scanCode({
      onlyFromCamera: true, // 只允许从相机扫码
      scanType: ['qrCode', 'barCode'], // 支持二维码和条形码
      success: (res) => {
        console.log('扫码成功：', res)
        this.handleScanResult(res)
      },
      fail: (err) => {
        console.error('扫码失败：', err)
        wx.showToast({
          title: '扫码失败',
          icon: 'none'
        })
      }
    })
  },
  
  // 处理扫码结果
  handleScanResult(scanRes) {
    console.log('扫码结果：', scanRes)
    
    let tableNumber = null
    
    // 扫码结果可能是小程序码或普通二维码
    // 小程序码：scanRes.path 包含页面路径和参数
    // 普通二维码：scanRes.result 包含二维码内容
    
    if (scanRes.path) {
      // 小程序码，解析路径参数
      // 例如：pages/index/index?scene=tableNumber=A01
      const url = scanRes.path
      const queryIndex = url.indexOf('?')
      if (queryIndex > -1) {
        const queryString = url.substring(queryIndex + 1)
        console.log('查询字符串：', queryString)
        
        const params = this.parseQueryString(queryString)
        console.log('解析后的参数：', params)
        
        if (params.scene) {
          // scene 参数的值就是 tableNumber=A01
          const scene = params.scene
          console.log('scene值：', scene)
          
          // 解析 scene 参数
          const sceneParams = this.parseSceneParams(scene)
          console.log('解析后的scene参数：', sceneParams)
          
          if (sceneParams.tableNumber) {
            tableNumber = sceneParams.tableNumber
          }
        }
      }
    } else if (scanRes.result) {
      // 普通二维码，尝试解析内容
      // 假设二维码内容格式为：tableNumber=A01
      console.log('普通二维码内容：', scanRes.result)
      const params = this.parseSceneParams(scanRes.result)
      if (params.tableNumber) {
        tableNumber = params.tableNumber
      }
    }
    
    console.log('最终获取的餐台号：', tableNumber)
    
    if (tableNumber) {
      // 保存餐台号
      wx.setStorageSync('tableNumber', tableNumber)
      getApp().globalData.tableNumber = tableNumber
      console.log('扫码获取餐台号：', tableNumber)
      
      // 重新加载餐台信息
      this.loadTableInfo()
    } else {
      wx.showToast({
        title: '无效的二维码',
        icon: 'none'
      })
    }
  },
  
  // 解析URL查询字符串
  parseQueryString(queryString) {
    const params = {}
    const pairs = queryString.split('&')
    
    pairs.forEach(pair => {
      const firstEqualIndex = pair.indexOf('=')
      if (firstEqualIndex > -1) {
        const key = pair.substring(0, firstEqualIndex)
        const value = pair.substring(firstEqualIndex + 1) // 取第一个=后面的所有内容
        if (key && value) {
          params[key] = value
        }
      }
    })
    
    return params
  },
  
  // 解析场景值参数
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

  onShow() {
    // 处理"再来一单"功能
    const reorderCartItems = wx.getStorageSync('reorderCartItems')
    if (reorderCartItems && reorderCartItems.length > 0) {
      // 合并到当前购物车
      this.mergeReorderItems(reorderCartItems)
      // 清除缓存
      wx.removeStorageSync('reorderCartItems')
    }
    
    // 更新菜品数量和购物车
    this.calculateCart()
    this.updateDishCount()
  },
  
  // 合并再来一单的菜品到购物车
  mergeReorderItems(reorderItems) {
    const cartItems = this.data.cartItems
    
    reorderItems.forEach(newItem => {
      // 解析口味JSON为对象
      let flavors = null
      let flavorText = ''
      if (newItem.flavorJson) {
        try {
          flavors = JSON.parse(newItem.flavorJson)
          flavorText = Object.values(flavors).join('、')
        } catch (e) {
          console.error('解析口味JSON失败', e)
        }
      }
      
      // 生成flavorKey
      let flavorKey
      if (newItem.dishId) {
        // 菜品
        flavorKey = this.generateFlavorKey(newItem.dishId, flavors)
      } else if (newItem.setmealId) {
        // 套餐
        flavorKey = this.generateSetmealKey(newItem.setmealId)
      }
      
      // 查找购物车中是否已有相同的菜品（使用flavorKey匹配）
      const existingIndex = cartItems.findIndex(item => item.flavorKey === flavorKey)
      
      if (existingIndex > -1) {
        // 已存在，数量相加
        cartItems[existingIndex].quantity += newItem.quantity
        cartItems[existingIndex].amount = cartItems[existingIndex].quantity * cartItems[existingIndex].price
      } else {
        // 不存在，添加完整的购物车项
        cartItems.push({
          dishId: newItem.dishId || null,
          setmealId: newItem.setmealId || null,
          name: newItem.name,
          price: parseFloat(newItem.price),
          image: newItem.image,
          quantity: newItem.quantity,
          amount: parseFloat(newItem.amount),
          flavors: flavors,
          flavorText: flavorText,
          flavorJson: newItem.flavorJson,
          flavorKey: flavorKey,
          type: newItem.dishId ? 'dish' : 'setmeal'
        })
      }
    })
    
    this.setData({ cartItems })
  },
  
  // 点击桌号区域
  onTableNumberTap() {
    const currentTableNumber = this.data.tableNumber
    
    // 如果当前没有桌号或显示"点击扫码"，直接扫码
    if (!currentTableNumber || currentTableNumber === '点击扫码') {
      this.scanTableCode()
      return
    }
    
    // 如果已有桌号，询问是否重新扫码
    wx.showModal({
      title: '提示',
      content: `当前桌号：${currentTableNumber}\n是否重新扫码？`,
      confirmText: '重新扫码',
      cancelText: '取消',
      success: (res) => {
        if (res.confirm) {
          this.scanTableCode()
        }
      }
    })
  },

  // 加载分类列表
  loadCategories() {
    getCategoryList().then(res => {
      const categories = res.data
      this.setData({
        categories,
        currentCategory: categories.length > 0 ? categories[0].id : null
      }, () => {
        // 加载第一个分类的菜品
        if (this.data.currentCategory) {
          this.loadDishesByCategory(this.data.currentCategory)
        }
      })
    }).catch(err => {
      console.error('加载分类失败', err)
    })
  },

  // 根据分类加载菜品列表
  loadDishesByCategory(categoryId) {
    getDishListByCategory(categoryId).then(res => {
      // 处理后端返回的菜品数据
      const dishes = res.data.map(dish => ({
        id: dish.id,
        name: dish.name,
        categoryId: dish.categoryId,
        price: dish.price,
        image: dish.image,
        description: dish.description,
        status: dish.status,
        count: 0, // 前端用于显示数量
        flavors: dish.flavors || [], // 口味列表
        type: 'dish' // 标记为菜品
      }))
      
      this.setData({ dishes })
      this.updateDishCount()
    }).catch(err => {
      console.error('加载菜品失败', err)
    })
  },

  // 根据分类加载套餐列表
  loadSetmealsByCategory(categoryId) {
    getSetmealListByCategory(categoryId).then(res => {
      // 处理后端返回的套餐数据
      const setmeals = res.data.map(setmeal => ({
        id: setmeal.id,
        name: setmeal.name,
        categoryId: setmeal.categoryId,
        price: setmeal.price,
        image: setmeal.image,
        description: setmeal.description,
        status: setmeal.status,
        count: 0, // 前端用于显示数量
        dishes: setmeal.dishes || [], // 套餐包含的菜品
        type: 'setmeal' // 标记为套餐
      }))
      
      this.setData({ setmeals })
      this.updateSetmealCount()
    }).catch(err => {
      console.error('加载套餐失败', err)
    })
  },

  selectCategory(e) {
    const { id } = e.currentTarget.dataset
    this.setData({
      currentCategory: id
    })
    // 切换分类时加载该分类的菜品和套餐
    this.loadDishesByCategory(id)
    this.loadSetmealsByCategory(id)
  },

  // 生成口味key
  generateFlavorKey(dishId, flavors) {
    if (!flavors || Object.keys(flavors).length === 0) {
      return `${dishId}`
    }
    const flavorStr = Object.keys(flavors).sort().map(key => `${key}:${flavors[key]}`).join('|')
    return `${dishId}_${flavorStr}`
  },

  // 生成套餐key
  generateSetmealKey(setmealId) {
    return `setmeal_${setmealId}`
  },

  // 添加菜品
  addDish(e) {
    const { id, type } = e.currentTarget.dataset
    
    // 如果是套餐，直接添加
    if (type === 'setmeal') {
      this.addSetmealToCart(id)
      return
    }
    
    // 查找菜品
    const dish = this.data.dishes.find(d => d.id === id)
    
    // 如果菜品有口味选项，显示口味选择弹窗
    if (dish && dish.flavors && dish.flavors.length > 0) {
      // 查找该菜品最后一次添加的购物车项
      const dishCartItems = this.data.cartItems.filter(item => item.dishId === id)
      
      let selectedFlavors = {}
      let currentCartItem = null
      
      if (dishCartItems.length > 0) {
        // 如果已经添加过，使用最后一次添加的口味
        const lastItem = dishCartItems[dishCartItems.length - 1]
        selectedFlavors = lastItem.flavors ? { ...lastItem.flavors } : {}
        currentCartItem = lastItem
      } else {
        // 如果没有添加过，使用默认口味（第一个选项）
        dish.flavors.forEach(flavor => {
          selectedFlavors[flavor.name] = flavor.options[0]
        })
      }
      
      this.setData({
        showFlavorDialog: true,
        currentDish: dish,
        selectedFlavors,
        currentCartItem
      })
    } else {
      // 没有口味选项，直接添加
      this.addDishToCart(id, null)
    }
  },

  // 添加菜品到购物车
  addDishToCart(dishId, flavors = null) {
    const flavorKey = this.generateFlavorKey(dishId, flavors)
    const cartItems = [...this.data.cartItems]
    
    // 查找是否已存在相同口味的菜品
    const existingIndex = cartItems.findIndex(item => item.flavorKey === flavorKey)
    
    if (existingIndex > -1) {
      // 已存在，数量+1，并更新金额
      cartItems[existingIndex].quantity += 1
      cartItems[existingIndex].amount = cartItems[existingIndex].quantity * cartItems[existingIndex].price
    } else {
      // 不存在，新增
      const dish = this.data.dishes.find(d => d.id === dishId)
      
      // 将口味对象转换为字符串（用于显示）
      let flavorText = ''
      if (flavors) {
        flavorText = Object.values(flavors).join('、')
      }
      
      // 将口味对象转换为JSON格式（对应数据库 order_detail.flavor 字段）
      let flavorJson = null
      if (flavors) {
        flavorJson = JSON.stringify(flavors)
      }
      
      cartItems.push({
        dishId: dishId,  // 菜品ID
        setmealId: null, // 套餐ID（当前是菜品，所以为null）
        name: dish.name,
        price: dish.price,
        image: dish.image,
        quantity: 1,
        amount: dish.price,
        flavors: flavors ? { ...flavors } : null,
        flavorText,
        flavorJson,
        flavorKey,
        type: 'dish'
      })
    }
    
    this.setData({ cartItems })
    this.calculateCart()
    this.updateDishCount()
  },

  // 添加套餐到购物车
  addSetmealToCart(setmealId) {
    const flavorKey = this.generateSetmealKey(setmealId)
    const cartItems = [...this.data.cartItems]
    
    // 查找是否已存在该套餐
    const existingIndex = cartItems.findIndex(item => item.flavorKey === flavorKey)
    
    if (existingIndex > -1) {
      // 已存在，数量+1，并更新金额
      cartItems[existingIndex].quantity += 1
      cartItems[existingIndex].amount = cartItems[existingIndex].quantity * cartItems[existingIndex].price
    } else {
      // 不存在，新增
      const setmeal = this.data.setmeals.find(s => s.id === setmealId)
      
      cartItems.push({
        dishId: null,  // 菜品ID（套餐为null）
        setmealId: setmealId, // 套餐ID
        name: setmeal.name,
        price: setmeal.price,
        image: setmeal.image,
        quantity: 1,
        amount: setmeal.price,
        flavors: null,
        flavorText: '',
        flavorJson: null,
        flavorKey,
        type: 'setmeal'
      })
    }
    
    this.setData({ cartItems })
    this.calculateCart()
    this.updateSetmealCount()
  },

  // 从购物车减少菜品
  removeDishFromCart(dishId, flavors = null) {
    const flavorKey = this.generateFlavorKey(dishId, flavors)
    const cartItems = [...this.data.cartItems]
    
    const existingIndex = cartItems.findIndex(item => item.flavorKey === flavorKey)
    
    if (existingIndex > -1) {
      if (cartItems[existingIndex].quantity > 1) {
        cartItems[existingIndex].quantity -= 1
        cartItems[existingIndex].amount = cartItems[existingIndex].quantity * cartItems[existingIndex].price
      } else {
        cartItems.splice(existingIndex, 1)
      }
    }
    
    this.setData({ cartItems })
    this.calculateCart()
    this.updateDishCount()
  },

  // 从购物车减少套餐
  removeSetmealFromCart(setmealId) {
    const flavorKey = this.generateSetmealKey(setmealId)
    const cartItems = [...this.data.cartItems]
    
    const existingIndex = cartItems.findIndex(item => item.flavorKey === flavorKey)
    
    if (existingIndex > -1) {
      if (cartItems[existingIndex].quantity > 1) {
        cartItems[existingIndex].quantity -= 1
        cartItems[existingIndex].amount = cartItems[existingIndex].quantity * cartItems[existingIndex].price
      } else {
        cartItems.splice(existingIndex, 1)
      }
    }
    
    this.setData({ cartItems })
    this.calculateCart()
    this.updateSetmealCount()
  },

  // 减少菜品（列表中的减号）
  decreaseDish(e) {
    const { id, type } = e.currentTarget.dataset
    
    // 如果是套餐
    if (type === 'setmeal') {
      this.removeSetmealFromCart(id)
      return
    }
    
    // 查找该菜品的所有购物车项
    const dishCartItems = this.data.cartItems.filter(item => item.dishId === id)
    
    if (dishCartItems.length > 0) {
      // 减少最后一个添加的
      const lastItem = dishCartItems[dishCartItems.length - 1]
      this.removeDishFromCart(id, lastItem.flavors)
    }
  },

  // 选择口味
  selectFlavor(e) {
    const { name, option } = e.currentTarget.dataset
    const selectedFlavors = { ...this.data.selectedFlavors }
    selectedFlavors[name] = option
    
    // 查找是否已有相同口味的购物车项
    const flavorKey = this.generateFlavorKey(this.data.currentDish.id, selectedFlavors)
    const cartItem = this.data.cartItems.find(item => item.flavorKey === flavorKey)
    
    this.setData({ 
      selectedFlavors,
      currentCartItem: cartItem || null
    })
  },

  // 确认口味选择（加入购物车）
  confirmFlavor() {
    if (this.data.currentDish) {
      this.addDishToCart(this.data.currentDish.id, this.data.selectedFlavors)
      
      // 更新当前购物车项
      const flavorKey = this.generateFlavorKey(this.data.currentDish.id, this.data.selectedFlavors)
      const cartItem = this.data.cartItems.find(item => item.flavorKey === flavorKey)
      
      this.setData({
        currentCartItem: cartItem
      })
    }
  },

  // 弹窗中增加数量
  increaseInDialog() {
    if (this.data.currentDish) {
      this.addDishToCart(this.data.currentDish.id, this.data.selectedFlavors)
      
      // 更新当前购物车项
      const flavorKey = this.generateFlavorKey(this.data.currentDish.id, this.data.selectedFlavors)
      const cartItem = this.data.cartItems.find(item => item.flavorKey === flavorKey)
      
      this.setData({
        currentCartItem: cartItem
      })
    }
  },

  // 弹窗中减少数量
  decreaseInDialog() {
    if (this.data.currentDish) {
      this.removeDishFromCart(this.data.currentDish.id, this.data.selectedFlavors)
      
      // 更新当前购物车项
      const flavorKey = this.generateFlavorKey(this.data.currentDish.id, this.data.selectedFlavors)
      const cartItem = this.data.cartItems.find(item => item.flavorKey === flavorKey)
      
      this.setData({
        currentCartItem: cartItem || null
      })
    }
  },

  // 关闭口味弹窗
  closeFlavorDialog() {
    this.setData({
      showFlavorDialog: false,
      currentDish: null,
      selectedFlavors: {},
      currentCartItem: null
    })
  },

  // 阻止事件冒泡
  stopPropagation() {
    // 空函数，用于阻止事件冒泡
  },

  // 更新菜品显示的数量（所有口味的总和）
  updateDishCount() {
    const dishes = this.data.dishes.map(dish => {
      const count = this.data.cartItems
        .filter(item => item.dishId === dish.id)
        .reduce((sum, item) => sum + item.quantity, 0)
      return { ...dish, count }
    })
    
    this.setData({ dishes })
  },

  // 更新套餐显示的数量
  updateSetmealCount() {
    const setmeals = this.data.setmeals.map(setmeal => {
      const count = this.data.cartItems
        .filter(item => item.setmealId === setmeal.id)
        .reduce((sum, item) => sum + item.quantity, 0)
      return { ...setmeal, count }
    })
    
    this.setData({ setmeals })
  },

  // 计算购物车
  calculateCart() {
    let totalCount = 0
    let totalPrice = 0
    
    this.data.cartItems.forEach(item => {
      totalCount += item.quantity
      totalPrice += item.quantity * item.price
    })
    
    this.setData({
      cartCount: totalCount,
      cartTotal: totalPrice.toFixed(2)
    })
  },

  showCart() {
    this.setData({
      showCartDialog: !this.data.showCartDialog
    })
  },

  closeCartDialog() {
    this.setData({
      showCartDialog: false
    })
  },

  // 购物车中增加数量
  increaseInCart(e) {
    const { key } = e.currentTarget.dataset
    const cartItems = [...this.data.cartItems]
    const item = cartItems.find(item => item.flavorKey === key)
    if (item) {
      item.quantity += 1
      item.amount = item.quantity * item.price
    }
    this.setData({ cartItems })
    this.calculateCart()
    this.updateDishCount()
  },

  // 购物车中减少数量
  decreaseInCart(e) {
    const { key } = e.currentTarget.dataset
    const cartItems = [...this.data.cartItems]
    const index = cartItems.findIndex(item => item.flavorKey === key)
    if (index > -1) {
      if (cartItems[index].quantity > 1) {
        cartItems[index].quantity -= 1
        cartItems[index].amount = cartItems[index].quantity * cartItems[index].price
      } else {
        cartItems.splice(index, 1)
      }
    }
    this.setData({ cartItems })
    this.calculateCart()
    this.updateDishCount()
    
    // 如果购物车空了，关闭弹窗
    if (cartItems.length === 0) {
      this.closeCartDialog()
    }
  },

  // 清空购物车
  clearCart() {
    wx.showModal({
      title: '提示',
      content: '确定要清空购物车吗？',
      success: (res) => {
        if (res.confirm) {
          this.setData({
            cartItems: [],
            cartCount: 0,
            cartTotal: 0
          })
          this.updateDishCount()
          this.closeCartDialog()
        }
      }
    })
  },

  checkout() {
    if (this.data.cartItems.length === 0) {
      wx.showToast({
        title: '购物车为空',
        icon: 'none'
      })
      return
    }
    
    // 检查是否有餐台号
    const tableNumber = this.data.tableNumber
    const tableId = this.data.tableId
    
    if (!tableNumber || tableNumber === '点击扫码' || !tableId) {
      // 没有餐台号，提示扫码
      wx.showModal({
        title: '温馨提示',
        content: '请先扫描餐台二维码',
        confirmText: '扫码',
        cancelText: '取消',
        success: (res) => {
          if (res.confirm) {
            this.scanTableCode()
          }
        }
      })
      return
    }
    
    // 有餐台号，跳转到结算页面
    wx.navigateTo({
      url: '/pages/checkout/checkout',
      success: () => {
        // 跳转成功后的回调
      }
    })
  },

  // 跳转到菜品详情页
  goToDishDetail(e) {
    const dishId = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/dish-detail/dish-detail?id=${dishId}`
    })
  },

  // 阻止事件冒泡
  stopPropagation() {
    // 空函数，用于阻止事件冒泡
  }
})
