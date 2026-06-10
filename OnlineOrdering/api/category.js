// 分类相关接口
const { request } = require('../utils/request.js')

/**
 * 查询分类列表
 */
const getCategoryList = () => {
  return request({
    url: '/user/category/list',
    method: 'GET'
  })
}

module.exports = {
  getCategoryList
}
