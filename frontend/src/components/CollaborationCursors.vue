<script setup lang="ts">
interface User {
  clientId: number
  user: {
    name: string
    color: string
  }
}

defineProps<{ users: User[] }>()
</script>

<template>
  <div v-if="users.length > 0" style="display: flex; gap: 8px; align-items: center">
    <span style="font-size: 12px; color: #666">Online:</span>
    <div
      v-for="u in users"
      :key="u.clientId"
      style="display: flex; align-items: center; gap: 4px; padding: 2px 8px; border-radius: 12px"
      :style="{ backgroundColor: u.user.color + '20', border: `1px solid ${u.user.color}` }"
    >
      <div
        style="width: 8px; height: 8px; border-radius: 50%"
        :style="{ backgroundColor: u.user.color }"
      />
      <span style="font-size: 12px">{{ u.user.name }}</span>
    </div>
  </div>
</template>
