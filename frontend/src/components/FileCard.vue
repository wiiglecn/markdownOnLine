<script setup lang="ts">
import { ref } from 'vue'
import type { FileItem } from '../api/files'

defineProps<{ file: FileItem }>()
const emit = defineEmits<{
  open: [id: number]
  rename: [id: number, name: string]
  delete: [id: number]
}>()

const isEditing = ref(false)
const editName = ref('')

function startEdit(name: string) {
  editName.value = name
  isEditing.value = true
}

function handleRename(id: number, originalName: string) {
  if (editName.value.trim() && editName.value !== originalName) {
    emit('rename', id, editName.value.trim())
  }
  isEditing.value = false
}

function confirmDelete(id: number) {
  if (window.confirm('Delete this file?')) emit('delete', id)
}
</script>

<template>
  <div class="card" style="cursor: pointer; position: relative" @click="!isEditing && emit('open', file.id)">
    <div style="display: flex; align-items: center; gap: 12px">
      <span style="font-size: 24px">
        {{ file.contentType === 'MARKDOWN' ? '.md' : '.txt' }}
      </span>
      <div style="flex: 1">
        <input
          v-if="isEditing"
          type="text"
          v-model="editName"
          @blur="handleRename(file.id, file.name)"
          @keydown.enter="handleRename(file.id, file.name)"
          autofocus
          @click.stop
          style="width: 100%"
        />
        <template v-else>
          <div style="font-weight: 500">{{ file.name }}</div>
          <div style="font-size: 12px; color: #666">
            {{ new Date(file.updatedAt).toLocaleDateString() }}
          </div>
        </template>
      </div>
      <div style="display: flex; gap: 4px">
        <button
          class="btn-secondary"
          style="padding: 4px 8px; font-size: 12px"
          @click.stop="startEdit(file.name)"
        >
          Rename
        </button>
        <button
          class="btn-danger"
          style="padding: 4px 8px; font-size: 12px"
          @click.stop="confirmDelete(file.id)"
        >
          Delete
        </button>
      </div>
    </div>
  </div>
</template>
