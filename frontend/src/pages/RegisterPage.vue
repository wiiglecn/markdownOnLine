<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/authStore'

const email = ref('')
const password = ref('')
const nickname = ref('')
const error = ref('')
const loading = ref(false)
const router = useRouter()
const auth = useAuthStore()

async function handleSubmit() {
  error.value = ''
  loading.value = true
  try {
    await auth.register(email.value, password.value, nickname.value)
    router.push('/files')
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Registration failed'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div style="display: flex; justify-content: center; align-items: center; min-height: 100vh">
    <div class="card" style="width: 400px; padding: 32px">
      <h1 style="text-align: center; margin-bottom: 24px">Register</h1>
      <div v-if="error" style="color: #d94a4a; margin-bottom: 16px; text-align: center">{{ error }}</div>
      <form @submit.prevent="handleSubmit">
        <div style="margin-bottom: 16px">
          <label style="display: block; margin-bottom: 4px; font-size: 14px">Email</label>
          <input type="email" v-model="email" required style="width: 100%" />
        </div>
        <div style="margin-bottom: 16px">
          <label style="display: block; margin-bottom: 4px; font-size: 14px">Nickname</label>
          <input type="text" v-model="nickname" required style="width: 100%" />
        </div>
        <div style="margin-bottom: 24px">
          <label style="display: block; margin-bottom: 4px; font-size: 14px">Password</label>
          <input type="password" v-model="password" required minlength="6" style="width: 100%" />
        </div>
        <button type="submit" class="btn-primary" :disabled="loading" style="width: 100%; padding: 10px">
          {{ loading ? 'Registering...' : 'Register' }}
        </button>
      </form>
      <p style="text-align: center; margin-top: 16px; font-size: 14px">
        Already have an account? <router-link to="/login">Login</router-link>
      </p>
    </div>
  </div>
</template>
