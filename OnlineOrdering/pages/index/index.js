// index.js
import { mockCategories, mockDishes, mockShopInfo } from '../../data/mockData.js'

Page({
  data: {
    shopInfo: mockShopInfo,
    tableNumber: 'A5',
    // 分类列表 - 对应 category 表
    categories: [],
    currentCategory: null,
    // 菜品列表 - 对应 dish 表和 dish_flavor 表
    dishes: [],
    // 购物车数据：存储不同口味的菜品
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

  onLoad() {
    // 加载分类和菜品数据
    this.loadCategories()
    this.loadDishes()
  },

  // 加载分类列表
  loadCategories() {
    // TODO: 调用后端API获取分类数据
    // GET /api/category/list
    
    // 临时使用模拟数据
    const categories = mockCategories
    
    this.setData({
      categories,
      currentCategory: categories.length > 0 ? categories[0].id : null
    })
  },

  // 加载菜品列表
  loadDishes() {
    // TODO: 调用后端API获取菜品数据
    // GET /api/dish/list
    
    // 临时使用模拟数据，字段对应数据库结构
    const dishes = mockDishes
    
    this.setData({ dishes })
  },

  selectCategory(e) {
    const { id } = e.currentTarget.dataset
    this.setData({
      currentCategory: id
    })
  },

  // 获取当前分类的菜品
  getCurrentDishes() {
    if (!this.data.currentCategory) return []
    return this.data.dishes.filter(dish => 
      dish.categoryId === this.data.currentCategory && dish.status === 1
    )
  },

  // 生成口味key
  generateFlavorKey(dishId, flavors) {
    if (!flavors || Object.keys(flavors).length === 0) {
      return `${dishId}`
    }
    const flavorStr = Object.keys(flavors).sort().map(key => `${key}:${flavors[key]}`).join('|')
    return `${dishId}_${flavorStr}`
  },

  // 添加菜品
  addDish(e) {
    const { id } = e.currentTarget.dataset
    
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
        dishId,
        name: dish.name,
        price: dish.price,
        image: dish.image,
        quantity: 1,
        amount: dish.price,
        flavors: flavors ? { ...flavors } : null,
        flavorText,
        flavorJson,
        flavorKey
      })
    }
    
    this.setData({ cartItems })
    this.calculateCart()
    this.updateDishCount()
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

  // 减少菜品（列表中的减号）
  decreaseDish(e) {
    const { id } = e.currentTarget.dataset
    
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
    
    // 添加页面跳转动画，使过渡更平滑
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
