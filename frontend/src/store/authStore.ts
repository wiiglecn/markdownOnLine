import { defineStore } from 'pinia'
import { ref } from 'vue'
import client from '../api/client'

interface User {
  id: number
  email: string
  nickname: string
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(null)
  const user = ref<User | null>(null)
  const isAuthenticated = ref(false)

  async function login(email: string, password: string) {
    const response = await client.post('/auth/login', { email, password })
    const { token: t, userId, email: userEmail, nickname } = response.data
    localStorage.setItem('token', t)
    localStorage.setItem('user', JSON.stringify({ id: userId, email: userEmail, nickname }))
    token.value = t
    user.value = { id: userId, email: userEmail, nickname }
    isAuthenticated.value = true
  }

  async function register(email: string, password: string, nickname: string) {
    const response = await client.post('/auth/register', { email, password, nickname })
    const { token: t, userId, email: userEmail, nickname: userNickname } = response.data
    localStorage.setItem('token', t)
    localStorage.setItem('user', JSON.stringify({ id: userId, email: userEmail, nickname: userNickname }))
    token.value = t
    user.value = { id: userId, email: userEmail, nickname: userNickname }
    isAuthenticated.value = true
  }

  function logout() {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    token.value = null
    user.value = null
    isAuthenticated.value = false
  }

  function loadFromStorage() {
    const storedToken = localStorage.getItem('token')
    const userStr = localStorage.getItem('user')
    if (storedToken && userStr) {
      try {
        const parsed = JSON.parse(userStr)
        token.value = storedToken
        user.value = parsed
        isAuthenticated.value = true
      } catch {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
      }
    }
  }

  return { token, user, isAuthenticated, login, register, logout, loadFromStorage }
})
