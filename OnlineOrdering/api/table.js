const { request } = require('../utils/request.js')

/**
 * 根据餐台号查询餐台信息
 */
function getTableByNumber(tableNumber) {
  return request({
    url: '/user/table/number',
    method: 'GET',
    data: { tableNumber }
  })
}

module.exports = {
  getTableByNumber
}
