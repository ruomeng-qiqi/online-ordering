// 套餐相关接口
const { request } = require('../utils/request.js')

/**
 * 根据分类查询套餐列表
 */
const getSetmealListByCategory = (categoryId) => {
  return request({
    url: '/user/setmeal/list',
    method: 'GET',
    data: { categoryId }
  })
}

/**
 * 根据ID查询套餐详情
 */
const getSetmealById = (id) => {
  return request({
    url: `/user/setmeal/${id}`,
    method: 'GET'
  })
}

module.exports = {
  getSetmealListByCategory,
  getSetmealById
}
