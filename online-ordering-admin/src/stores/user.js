import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')

  // 设置用户信息
  const setUserInfo = (userInfo) => {
    token.value = userInfo.token
    username.value = userInfo.username
    localStorage.setItem('token', userInfo.token)
    localStorage.setItem('username', userInfo.username)
  }

  // 清除用户信息
  const clearUserInfo = () => {
    token.value = ''
    username.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('username')
  }

  // 登出
  const logout = () => {
    clearUserInfo()
  }

  return {
    token,
    username,
    setUserInfo,
    clearUserInfo,
    logout
  }
})
