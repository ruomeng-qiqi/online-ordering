// 订单模拟数据文件

// 订单列表数据 - 对应 orders 表
export const mockOrders = [
  {
    id: 1,
    orderNumber: '20260507152000123456',
    customerId: 1,
    tableId: 1,
    tableName: '大厅A01',
    checkoutTime: '2026-05-07 16:30:00',
    totalAmount: 120.00,
    actualAmount: 100.00,
    discountAmount: 0.00,
    pointsDeduction: 20.00,
    pointsUsed: 2000,
    pointsEarned: 100,
    paymentMethod: 1, // 1-在线支付，2-线下支付
    orderStatus: 2, // 1-待支付，2-已完成，3-已取消
    remark: '少放辣椒',
    cancelReason: null,
    createTime: '2026-05-07 15:20:00',
    updateTime: '2026-05-07 16:30:00',
    // 订单明细
    details: [
      {
        id: 1,
        dishId: 1,
        setmealId: null,
        name: '测试菜品',
        image: 'https://via.placeholder.com/100',
        quantity: 2,
        price: 58.00,
        amount: 116.00,
        flavor: '{"甜味":"少糖","忌口":"不要葱"}'
      },
      {
        id: 2,
        dishId: 2,
        setmealId: null,
        name: '平菇豆腐汤',
        image: 'https://via.placeholder.com/100',
        quantity: 1,
        price: 6.00,
        amount: 6.00,
        flavor: '{"辣度":"不辣"}'
      }
    ]
  },
  {
    id: 2,
    orderNumber: '20260506123000234567',
    customerId: 1,
    tableId: 2,
    tableName: '大厅A02',
    checkoutTime: '2026-05-06 13:45:00',
    totalAmount: 180.00,
    actualAmount: 130.00,
    discountAmount: 0.00,
    pointsDeduction: 50.00,
    pointsUsed: 5000,
    pointsEarned: 0,
    paymentMethod: 1,
    orderStatus: 2,
    remark: '',
    cancelReason: null,
    createTime: '2026-05-06 12:30:00',
    updateTime: '2026-05-06 13:45:00',
    details: [
      {
        id: 3,
        dishId: 4,
        setmealId: null,
        name: '鲍鱼2斤',
        image: 'https://via.placeholder.com/100',
        quantity: 2,
        price: 72.00,
        amount: 144.00,
        flavor: null
      },
      {
        id: 4,
        dishId: 3,
        setmealId: null,
        name: '肉茄子',
        image: 'https://via.placeholder.com/100',
        quantity: 3,
        price: 4.00,
        amount: 12.00,
        flavor: null
      },
      {
        id: 5,
        dishId: 2,
        setmealId: null,
        name: '平菇豆腐汤',
        image: 'https://via.placeholder.com/100',
        quantity: 4,
        price: 6.00,
        amount: 24.00,
        flavor: null
      }
    ]
  },
  {
    id: 3,
    orderNumber: '20260505184500345678',
    customerId: 1,
    tableId: 3,
    tableName: '大厅A03',
    checkoutTime: '2026-05-05 20:00:00',
    totalAmount: 80.00,
    actualAmount: 80.00,
    discountAmount: 0.00,
    pointsDeduction: 0.00,
    pointsUsed: 0,
    pointsEarned: 80,
    paymentMethod: 2,
    orderStatus: 2,
    remark: '',
    cancelReason: null,
    createTime: '2026-05-05 18:45:00',
    updateTime: '2026-05-05 20:00:00',
    details: [
      {
        id: 6,
        dishId: 1,
        setmealId: null,
        name: '测试菜品',
        image: 'https://via.placeholder.com/100',
        quantity: 1,
        price: 58.00,
        amount: 58.00,
        flavor: '{"甜味":"半糖"}'
      },
      {
        id: 7,
        dishId: 3,
        setmealId: null,
        name: '肉茄子',
        image: 'https://via.placeholder.com/100',
        quantity: 2,
        price: 4.00,
        amount: 8.00,
        flavor: null
      },
      {
        id: 8,
        dishId: 2,
        setmealId: null,
        name: '平菇豆腐汤',
        image: 'https://via.placeholder.com/100',
        quantity: 2,
        price: 6.00,
        amount: 12.00,
        flavor: null
      }
    ]
  },
  {
    id: 4,
    orderNumber: '20260507093000678901',
    customerId: 1,
    tableId: 2,
    tableName: '大厅A02',
    checkoutTime: null,
    totalAmount: 150.00,
    actualAmount: null,
    discountAmount: 0.00,
    pointsDeduction: 0.00,
    pointsUsed: 0,
    pointsEarned: 0,
    paymentMethod: null,
    orderStatus: 1, // 待支付
    remark: '',
    cancelReason: null,
    createTime: '2026-05-07 09:30:00',
    updateTime: '2026-05-07 09:35:00',
    details: [
      {
        id: 9,
        dishId: 4,
        setmealId: null,
        name: '鲍鱼2斤',
        image: 'https://via.placeholder.com/100',
        quantity: 2,
        price: 72.00,
        amount: 144.00,
        flavor: null
      },
      {
        id: 10,
        dishId: 2,
        setmealId: null,
        name: '平菇豆腐汤',
        image: 'https://via.placeholder.com/100',
        quantity: 1,
        price: 6.00,
        amount: 6.00,
        flavor: null
      }
    ]
  },
  {
    id: 5,
    orderNumber: '20260504100000789012',
    customerId: 1,
    tableId: 5,
    tableName: '包厢B02',
    checkoutTime: null,
    totalAmount: 220.00,
    actualAmount: null,
    discountAmount: 0.00,
    pointsDeduction: 0.00,
    pointsUsed: 0,
    pointsEarned: 0,
    paymentMethod: null,
    orderStatus: 3, // 已取消
    remark: '多加点肉',
    cancelReason: '客户要求取消',
    createTime: '2026-05-04 10:00:00',
    updateTime: '2026-05-04 10:30:00',
    details: [
      {
        id: 11,
        dishId: 1,
        setmealId: null,
        name: '测试菜品',
        image: 'https://via.placeholder.com/100',
        quantity: 3,
        price: 58.00,
        amount: 174.00,
        flavor: '{"甜味":"多糖"}'
      },
      {
        id: 12,
        dishId: null,
        setmealId: 1,
        name: '人气套餐A计划',
        image: 'https://via.placeholder.com/100',
        quantity: 1,
        price: 45.00,
        amount: 45.00,
        flavor: null
      }
    ]
  },
  {
    id: 6,
    orderNumber: '20260503163000456789',
    customerId: 1,
    tableId: 1,
    tableName: '大厅A01',
    checkoutTime: '2026-05-03 17:45:00',
    totalAmount: 250.00,
    actualAmount: 250.00,
    discountAmount: 0.00,
    pointsDeduction: 0.00,
    pointsUsed: 0,
    pointsEarned: 150,
    paymentMethod: 1,
    orderStatus: 2,
    remark: '不要香菜',
    cancelReason: null,
    createTime: '2026-05-03 16:30:00',
    updateTime: '2026-05-03 17:45:00',
    details: [
      {
        id: 13,
        dishId: null,
        setmealId: 1,
        name: '人气套餐A计划',
        image: 'https://via.placeholder.com/100',
        quantity: 2,
        price: 45.00,
        amount: 90.00,
        flavor: null
      },
      {
        id: 14,
        dishId: 4,
        setmealId: null,
        name: '鲍鱼2斤',
        image: 'https://via.placeholder.com/100',
        quantity: 2,
        price: 72.00,
        amount: 144.00,
        flavor: null
      },
      {
        id: 15,
        dishId: 3,
        setmealId: null,
        name: '肉茄子',
        image: 'https://via.placeholder.com/100',
        quantity: 4,
        price: 4.00,
        amount: 16.00,
        flavor: null
      }
    ]
  }
]

// 订单状态文本映射
export const orderStatusText = {
  1: '待支付',
  2: '已完成',
  3: '已取消'
}

// 订单状态颜色映射
export const orderStatusColor = {
  1: '#ff6b35',
  2: '#52c41a',
  3: '#999'
}

// 支付方式文本映射
export const paymentMethodText = {
  1: '在线支付',
  2: '线下支付'
}
