<script setup lang="ts">
import { ref } from 'vue'

type ContentTypeName = 'TEXT' | 'MARKDOWN'

const props = defineProps<{
  type: 'folder' | 'file'
  open: boolean
}>()

const emit = defineEmits<{
  close: []
  create: [name: string, contentType?: ContentTypeName]
}>()

const name = ref('')
const contentType = ref<ContentTypeName>('MARKDOWN')

function handleSubmit() {
  if (props.type === 'folder') {
    emit('create', name.value)
  } else {
    emit('create', name.value, contentType.value)
  }
  name.value = ''
  emit('close')
}
</script>

<template>
  <div
    v-if="open"
    style="position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 1000"
  >
    <div class="card" style="width: 400px; padding: 24px">
      <h3 style="margin-bottom: 16px">New {{ type === 'folder' ? 'Folder' : 'File' }}</h3>
      <form @submit.prevent="handleSubmit">
        <div style="margin-bottom: 16px">
          <label style="display: block; margin-bottom: 4px; font-size: 14px">Name</label>
          <input
            type="text"
            v-model="name"
            :placeholder="type === 'folder' ? 'My Folder' : 'document.md'"
            autofocus
            style="width: 100%"
          />
        </div>
        <div v-if="type === 'file'" style="margin-bottom: 16px">
          <label style="display: block; margin-bottom: 4px; font-size: 14px">Type</label>
          <select v-model="contentType" style="width: 100%; padding: 8px; border-radius: 4px; border: 1px solid #ddd">
            <option value="MARKDOWN">Markdown</option>
            <option value="TEXT">Plain Text</option>
          </select>
        </div>
        <div style="display: flex; gap: 8px; justify-content: flex-end">
          <button type="button" class="btn-secondary" @click="emit('close')">Cancel</button>
          <button type="submit" class="btn-primary" :disabled="!name.trim()">Create</button>
        </div>
      </form>
    </div>
  </div>
</template>
