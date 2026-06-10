// 菜品相关接口
const { request } = require('../utils/request.js')

/**
 * 根据分类查询菜品列表
 */
const getDishListByCategory = (categoryId) => {
  return request({
    url: '/user/dish/list',
    method: 'GET',
    data: { categoryId }
  })
}

/**
 * 根据ID查询菜品详情
 */
const getDishById = (id) => {
  return request({
    url: `/user/dish/${id}`,
    method: 'GET'
  })
}

module.exports = {
  getDishListByCategory,
  getDishById
}
