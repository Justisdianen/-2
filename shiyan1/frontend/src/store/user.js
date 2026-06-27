import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, logout } from '@/api/login'
import { useRouter } from 'vue-router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : null)
  const router = useRouter()

  const isLoggedIn = computed(() => !!token.value)

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUser = (newUser) => {
    user.value = newUser
    localStorage.setItem('user', JSON.stringify(newUser))
  }

  const clearToken = () => {
    token.value = ''
    localStorage.removeItem('token')
  }

  const clearUser = () => {
    user.value = null
    localStorage.removeItem('user')
  }

  const handleLogin = async (loginForm) => {
    console.log('Login form:', loginForm)
    const response = await login(loginForm)
    console.log('Login response:', response)
    const { token: newToken, user: newUser } = response.data
    console.log('Token extracted:', newToken ? 'exists (' + newToken.length + ' chars)' : 'null')
    console.log('User extracted:', newUser)
    setToken(newToken)
    setUser(newUser)
    console.log('Token in localStorage after set:', localStorage.getItem('token') ? 'exists' : 'null')
    return response
  }

  const handleLogout = async () => {
    await logout()
    clearToken()
    clearUser()
    router.push('/login')
  }

  return {
    token,
    user,
    isLoggedIn,
    setToken,
    setUser,
    clearToken,
    clearUser,
    handleLogin,
    handleLogout
  }
})