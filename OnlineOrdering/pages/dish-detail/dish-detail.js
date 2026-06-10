// pages/dish-detail/dish-detail.js
const { getDishById } = require('../../api/dish.js')

Page({
  data: {
    dishId: null,
    // 菜品信息 - 对应 dish 表
    dish: {
      id: null,
      name: '',
      categoryId: null,
      price: 0,
      image: '',
      description: '',
      status: 1
    },
    // 口味列表
    flavors: [],
    // 该菜品在购物车中的总数量
    dishCount: 0,
    // 口味选择弹窗
    showFlavorDialog: false,
    currentDish: null,
    selectedFlavors: {},
    currentCartItem: null
  },

  onLoad(options) {
    const dishId = parseInt(options.id)
    if (dishId) {
      this.setData({ dishId })
      this.loadDishDetail(dishId)
    }
  },

  onShow() {
    // 每次显示页面时更新菜品数量
    this.updateDishCount()
  },

  // 加载菜品详情
  loadDishDetail(id) {
    getDishById(id).then(res => {
      const dish = res.data
      this.setData({
        dish: {
          id: dish.id,
          name: dish.name,
          categoryId: dish.categoryId,
          price: dish.price,
          image: dish.image,
          description: dish.description,
          status: dish.status
        },
        flavors: dish.flavors || []
      })
      this.updateDishCount()
    }).catch(err => {
      console.error('加载菜品详情失败', err)
      wx.showToast({
        title: '菜品不存在',
        icon: 'none'
      })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    })
  },

  // 添加菜品
  addDish() {
    if (this.data.dish.status !== 1) {
      wx.showToast({
        title: '该菜品已售罄',
        icon: 'none'
      })
      return
    }

    // 如果菜品有口味选项，显示口味选择弹窗
    if (this.data.flavors && this.data.flavors.length > 0) {
      const pages = getCurrentPages()
      const indexPage = pages.find(page => page.route === 'pages/index/index')
      const cartItems = indexPage ? indexPage.data.cartItems : []
      
      // 查找该菜品最后一次添加的购物车项
      const dishCartItems = cartItems.filter(item => item.dishId === this.data.dish.id)
      
      let selectedFlavors = {}
      let currentCartItem = null
      
      if (dishCartItems.length > 0) {
        // 使用最后一次添加的口味
        const lastItem = dishCartItems[dishCartItems.length - 1]
        selectedFlavors = lastItem.flavors ? { ...lastItem.flavors } : {}
        currentCartItem = lastItem
      } else {
        // 使用默认口味（第一个选项）
        this.data.flavors.forEach(flavor => {
          if (flavor.options && flavor.options.length > 0) {
            selectedFlavors[flavor.name] = flavor.options[0]
          }
        })
      }
      
      // 准备弹窗数据
      const currentDish = {
        ...this.data.dish,
        flavors: this.data.flavors
      }
      
      this.setData({
        showFlavorDialog: true,
        currentDish: currentDish,
        selectedFlavors: selectedFlavors,
        currentCartItem: currentCartItem
      })
    } else {
      // 没有口味选项，直接添加
      this.addDishToCart(this.data.dish.id, null)
    }
  },

  // 减少菜品
  decreaseDish() {
    const pages = getCurrentPages()
    const indexPage = pages.find(page => page.route === 'pages/index/index')
    
    if (!indexPage) return
    
    const cartItems = indexPage.data.cartItems || []
    const dishCartItems = cartItems.filter(item => item.dishId === this.data.dish.id)
    
    if (dishCartItems.length > 0) {
      // 减少最后一个添加的
      const lastItem = dishCartItems[dishCartItems.length - 1]
      this.removeDishFromCart(this.data.dish.id, lastItem.flavors)
    }
  },

  // 选择口味
  selectFlavor(e) {
    const { name, option } = e.currentTarget.dataset
    const selectedFlavors = { ...this.data.selectedFlavors }
    selectedFlavors[name] = option
    
    // 查找是否已有相同口味的购物车项
    const flavorKey = this.generateFlavorKey(this.data.currentDish.id, selectedFlavors)
    const pages = getCurrentPages()
    const indexPage = pages.find(page => page.route === 'pages/index/index')
    const cartItems = indexPage ? indexPage.data.cartItems : []
    const cartItem = cartItems.find(item => item.flavorKey === flavorKey)
    
    this.setData({ 
      selectedFlavors,
      currentCartItem: cartItem || null
    })
  },

  // 确认口味选择
  confirmFlavor() {
    if (this.data.currentDish) {
      this.addDishToCart(this.data.currentDish.id, this.data.selectedFlavors)
      
      // 更新当前购物车项
      const flavorKey = this.generateFlavorKey(this.data.currentDish.id, this.data.selectedFlavors)
      const pages = getCurrentPages()
      const indexPage = pages.find(page => page.route === 'pages/index/index')
      const cartItems = indexPage ? indexPage.data.cartItems : []
      const cartItem = cartItems.find(item => item.flavorKey === flavorKey)
      
      this.setData({
        currentCartItem: cartItem
      })
      
      this.updateDishCount()
    }
  },

  // 弹窗中增加数量
  increaseInDialog() {
    if (this.data.currentDish) {
      this.addDishToCart(this.data.currentDish.id, this.data.selectedFlavors)
      
      // 更新当前购物车项
      const flavorKey = this.generateFlavorKey(this.data.currentDish.id, this.data.selectedFlavors)
      const pages = getCurrentPages()
      const indexPage = pages.find(page => page.route === 'pages/index/index')
      const cartItems = indexPage ? indexPage.data.cartItems : []
      const cartItem = cartItems.find(item => item.flavorKey === flavorKey)
      
      this.setData({
        currentCartItem: cartItem
      })
      
      this.updateDishCount()
    }
  },

  // 弹窗中减少数量
  decreaseInDialog() {
    if (this.data.currentDish) {
      this.removeDishFromCart(this.data.currentDish.id, this.data.selectedFlavors)
      
      // 更新当前购物车项
      const flavorKey = this.generateFlavorKey(this.data.currentDish.id, this.data.selectedFlavors)
      const pages = getCurrentPages()
      const indexPage = pages.find(page => page.route === 'pages/index/index')
      const cartItems = indexPage ? indexPage.data.cartItems : []
      const cartItem = cartItems.find(item => item.flavorKey === flavorKey)
      
      this.setData({
        currentCartItem: cartItem || null
      })
      
      this.updateDishCount()
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

  // 生成口味唯一标识
  generateFlavorKey(dishId, flavors) {
    if (!flavors || Object.keys(flavors).length === 0) {
      return `${dishId}`
    }
    const flavorStr = Object.keys(flavors).sort().map(key => `${key}:${flavors[key]}`).join('|')
    return `${dishId}_${flavorStr}`
  },

  // 加入购物车
  addDishToCart(dishId, flavors) {
    const pages = getCurrentPages()
    const indexPage = pages.find(page => page.route === 'pages/index/index')
    
    if (!indexPage) return

    const flavorKey = this.generateFlavorKey(dishId, flavors)
    const flavorText = flavors ? Object.values(flavors).join('、') : ''
    const flavorJson = flavors ? JSON.stringify(flavors) : null
    
    const cartItems = indexPage.data.cartItems || []
    const existingItem = cartItems.find(item => item.flavorKey === flavorKey)
    
    if (existingItem) {
      existingItem.quantity += 1
      existingItem.amount = (existingItem.quantity * existingItem.price).toFixed(2)
    } else {
      cartItems.push({
        dishId: this.data.dish.id,
        setmealId: null,
        name: this.data.dish.name,
        image: this.data.dish.image,
        price: this.data.dish.price,
        quantity: 1,
        amount: this.data.dish.price.toFixed(2),
        flavors: flavors ? { ...flavors } : null,
        flavorText: flavorText,
        flavorJson: flavorJson,
        flavorKey: flavorKey
      })
    }
    
    indexPage.setData({ cartItems })
    indexPage.calculateCart()
    indexPage.updateDishCount()
  },

  // 从购物车移除
  removeDishFromCart(dishId, flavors) {
    const pages = getCurrentPages()
    const indexPage = pages.find(page => page.route === 'pages/index/index')
    
    if (!indexPage) return

    const flavorKey = this.generateFlavorKey(dishId, flavors)
    const cartItems = indexPage.data.cartItems || []
    const itemIndex = cartItems.findIndex(item => item.flavorKey === flavorKey)
    
    if (itemIndex !== -1) {
      const item = cartItems[itemIndex]
      item.quantity -= 1
      
      if (item.quantity <= 0) {
        cartItems.splice(itemIndex, 1)
      } else {
        item.amount = (item.quantity * item.price).toFixed(2)
      }
      
      indexPage.setData({ cartItems })
      indexPage.calculateCart()
      indexPage.updateDishCount()
      
      this.updateDishCount()
    }
  },

  // 更新该菜品在购物车中的总数量
  updateDishCount() {
    const pages = getCurrentPages()
    const indexPage = pages.find(page => page.route === 'pages/index/index')
    
    if (indexPage) {
      const cartItems = indexPage.data.cartItems || []
      const dishCount = cartItems
        .filter(item => item.dishId === this.data.dishId)
        .reduce((sum, item) => sum + item.quantity, 0)
      
      this.setData({ dishCount })
    }
  }
})
