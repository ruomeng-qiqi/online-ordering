// 模拟数据文件

// 分类数据 - 对应 category 表
export const mockCategories = [
  { id: 1, name: '腊味拼盘', type: 1, sort: 4, status: 1 },
  { id: 2, name: '腊味牛蛙', type: 1, sort: 5, status: 1 },
  { id: 3, name: '特色菜蛋', type: 1, sort: 6, status: 1 },
  { id: 4, name: '新鲜时蔬', type: 1, sort: 7, status: 1 },
  { id: 5, name: '水煮鱼', type: 1, sort: 8, status: 1 },
  { id: 6, name: '佛跳土豆', type: 1, sort: 9, status: 1 },
  { id: 7, name: '酒水饮料', type: 1, sort: 10, status: 1 },
  { id: 8, name: '汤类', type: 1, sort: 11, status: 1 }
]

// 菜品数据 - 对应 dish 表和 dish_flavor 表
export const mockDishes = [
  {
    id: 1,
    name: '测试菜品',
    categoryId: 1,
    price: 58.00,
    image: 'https://via.placeholder.com/100',
    description: '美味可口',
    status: 1,
    count: 0,
    flavors: [
      { name: '甜味', options: ['无糖', '少糖', '半糖', '多糖', '全糖'] },
      { name: '忌口', options: ['不要葱', '不要蒜', '不要香菜', '不要辣'] }
    ]
  },
  {
    id: 2,
    name: '平菇豆腐汤',
    categoryId: 8,
    price: 6.00,
    image: 'https://via.placeholder.com/100',
    description: '营养健康',
    status: 1,
    count: 0,
    flavors: [
      { name: '辣度', options: ['不辣', '微辣', '中辣'] }
    ]
  },
  {
    id: 3,
    name: '肉茄子',
    categoryId: 8,
    price: 4.00,
    image: 'https://via.placeholder.com/100',
    description: '家常美味',
    status: 1,
    count: 0,
    flavors: []
  },
  {
    id: 4,
    name: '鲍鱼2斤',
    categoryId: 1,
    price: 72.00,
    image: 'https://via.placeholder.com/100',
    description: '新鲜鲍鱼',
    status: 1,
    count: 0,
    flavors: []
  }
]

// 店铺信息
export const mockShopInfo = {
  name: '味美小馆（中山路店）',
  description: '欢迎光临，祝您用餐愉快！',
  image: '/assets/shop.jpg',
  tag1: '营业中',
  tag2: '新鲜食材',
  tag3: '干净卫生'
}
