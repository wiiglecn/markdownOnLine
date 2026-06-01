<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'

const props = withDefaults(defineProps<{
  message: string
  type: 'success' | 'error' | 'info'
  duration?: number
}>(), {
  duration: 3000,
})

const emit = defineEmits<{ close: [] }>()

let timer: ReturnType<typeof setTimeout>

onMounted(() => {
  timer = setTimeout(() => emit('close'), props.duration)
})

onUnmounted(() => {
  clearTimeout(timer)
})

const bgColor = {
  success: '#4caf50',
  error: '#f44336',
  info: '#2196f3',
}[props.type]
</script>

<template>
  <div style="position: fixed; bottom: 20px; right: 20px; padding: 12px 20px; color: white; border-radius: 4px; box-shadow: 0 2px 8px rgba(0,0,0,0.2); z-index: 10000; display: flex; align-items: center; gap: 8px"
    :style="{ backgroundColor: bgColor }"
  >
    <span>{{ message }}</span>
    <button
      @click="emit('close')"
      style="background: none; border: none; color: white; cursor: pointer; padding: 0; font-size: 16px"
    >
      &times;
    </button>
  </div>
</template>
