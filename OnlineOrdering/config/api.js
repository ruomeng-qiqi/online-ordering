// API 基础配置
// 开发环境：使用局域网 IP，手机和电脑在同一 WiFi 下可访问
// 将下面的 IP 地址改为你电脑的局域网 IP（通过 ipconfig 或 ifconfig 查看）
const baseURL = 'http://localhost:8080'  // 修改为你的电脑 IP

// 生产环境：使用线上域名
// const baseURL = 'https://你的域名'

module.exports = {
  baseURL
}
