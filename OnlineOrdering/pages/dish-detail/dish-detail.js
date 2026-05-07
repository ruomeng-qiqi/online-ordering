// pages/dish-detail/dish-detail.js
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
      status: 1  // 0-停售，1-起售
    },
    // 当前在购物车中的数量
    currentQuantity: 0
  },

  onLoad(options) {
    const dishId = options.id
    if (dishId) {
      this.setData({ dishId })
      this.loadDishDetail(dishId)
    }
  },

  onShow() {
    // 每次显示页面时更新购物车数量
    if (this.data.dishId) {
      this.updateCurrentQuantity()
    }
  },

  // 加载菜品详情
  loadDishDetail(id) {
    // 不显示loading，让页面先显示骨架或占位
    // wx.showLoading({
    //   title: '加载中...'
    // })

    // TODO: 调用后端API获取菜品详情
    // GET /api/dish/{id}
    
    // 模拟数据
    setTimeout(() => {
      const mockDish = {
        id: id,
        name: '测试菜品',
        categoryId: 1,
        price: 58.00,
        image: 'https://via.placeholder.com/400',
        description: '精选食材，美味可口，营养丰富',
        status: 1
      }

      this.setData({
        dish: mockDish
      })

      this.updateCurrentQuantity()
    }, 300)  // 减少延迟时间，提升响应速度
  },

  // 更新当前在购物车中的数量
  updateCurrentQuantity() {
    const pages = getCurrentPages()
    const indexPage = pages.find(page => page.route === 'pages/index/index')
    
    if (!indexPage) {
      this.setData({ currentQuantity: 0 })
      return
    }

    const cartItems = indexPage.data.cartItems || []
    const itemKey = this.getItemKey()
    const cartItem = cartItems.find(item => item.flavorKey === itemKey)
    
    this.setData({
      currentQuantity: cartItem ? cartItem.quantity : 0
    })
  },

  // 生成唯一标识（不包含口味）
  getItemKey() {
    return `${this.data.dishId}_default`
  },

  // 加入购物车
  addToCart() {
    if (this.data.dish.status !== 1) {
      wx.showToast({
        title: '该菜品已售罄',
        icon: 'none'
      })
      return
    }

    // TODO: 调用后端API加入购物车
    // POST /api/cart/add
    
    // 临时方案：更新首页购物车
    const pages = getCurrentPages()
    const indexPage = pages.find(page => page.route === 'pages/index/index')
    
    if (indexPage) {
      const itemKey = this.getItemKey()
      
      const cartItems = indexPage.data.cartItems || []
      const existingItem = cartItems.find(item => item.flavorKey === itemKey)
      
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
          flavorText: '',
          flavorJson: null,
          flavorKey: itemKey
        })
      }
      
      indexPage.setData({ cartItems })
      indexPage.updateDishCount()
      
      this.updateCurrentQuantity()
      
      wx.showToast({
        title: '已加入购物车',
        icon: 'success'
      })
    }
  },

  // 增加数量
  increase() {
    this.addToCart()
  },

  // 减少数量
  decrease() {
    const pages = getCurrentPages()
    const indexPage = pages.find(page => page.route === 'pages/index/index')
    
    if (indexPage) {
      const itemKey = this.getItemKey()
      const cartItems = indexPage.data.cartItems || []
      const itemIndex = cartItems.findIndex(item => item.flavorKey === itemKey)
      
      if (itemIndex !== -1) {
        const item = cartItems[itemIndex]
        item.quantity -= 1
        
        if (item.quantity <= 0) {
          cartItems.splice(itemIndex, 1)
        } else {
          item.amount = (item.quantity * item.price).toFixed(2)
        }
        
        indexPage.setData({ cartItems })
        indexPage.updateDishCount()
        
        this.updateCurrentQuantity()
      }
    }
  }
})
