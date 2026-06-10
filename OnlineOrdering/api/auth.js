const { request } = require('../utils/request.js')

/**
 * 微信小程序登录
 */
function login(data) {
  return request({
    url: '/user/auth/login',
    method: 'POST',
    data: data
  })
}

module.exports = {
  login
}
