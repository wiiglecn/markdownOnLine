<script setup lang="ts">
defineProps<{ isConnected: boolean }>()
</script>

<template>
  <div style="display: flex; align-items: center; gap: 6px; font-size: 12px">
    <div
      style="width: 8px; height: 8px; border-radius: 50%"
      :style="{ backgroundColor: isConnected ? '#4caf50' : '#f44336' }"
    />
    <span style="color: #666">
      {{ isConnected ? 'Connected' : 'Disconnected' }}
    </span>
  </div>
</template>
