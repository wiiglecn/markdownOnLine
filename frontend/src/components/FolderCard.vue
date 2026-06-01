<script setup lang="ts">
import type { Folder } from '../api/files'

defineProps<{ folder: Folder }>()
const emit = defineEmits<{
  open: [id: number]
  delete: [id: number]
}>()

function confirmDelete(id: number) {
  if (window.confirm('Delete this folder?')) emit('delete', id)
}
</script>

<template>
  <div class="card" style="cursor: pointer" @click="emit('open', folder.id)">
    <div style="display: flex; align-items: center; gap: 12px">
      <span style="font-size: 24px">&#128193;</span>
      <div style="flex: 1">
        <div style="font-weight: 500">{{ folder.name }}</div>
        <div style="font-size: 12px; color: #666">
          {{ new Date(folder.createdAt).toLocaleDateString() }}
        </div>
      </div>
      <button
        class="btn-danger"
        style="padding: 4px 8px; font-size: 12px"
        @click.stop="confirmDelete(folder.id)"
      >
        Delete
      </button>
    </div>
  </div>
</template>
