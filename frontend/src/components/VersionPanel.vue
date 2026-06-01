<script setup lang="ts">
import { ref, watch } from 'vue'
import { versionsApi, type Version } from '../api/versions'

const props = defineProps<{
  fileId: number
  open: boolean
}>()

const emit = defineEmits<{
  close: []
  restore: []
}>()

const versions = ref<Version[]>([])
const loading = ref(false)

watch(() => props.open, (isOpen) => {
  if (isOpen) loadVersions()
})

async function loadVersions() {
  loading.value = true
  try {
    const response = await versionsApi.listVersions(props.fileId)
    versions.value = response.data
  } finally {
    loading.value = false
  }
}

async function handleCreateVersion() {
  await versionsApi.createVersion(props.fileId)
  await loadVersions()
}

async function handleRestore(versionId: number) {
  if (confirm('Restore to this version? Current changes will be saved as a new version.')) {
    await versionsApi.restoreVersion(props.fileId, versionId)
    emit('restore')
    emit('close')
  }
}
</script>

<template>
  <div
    v-if="open"
    style="position: fixed; top: 0; right: 0; width: 350px; height: 100vh; background-color: #1e1e1e; border-left: 1px solid #333; z-index: 1000; display: flex; flex-direction: column"
  >
    <div style="padding: 16px; border-bottom: 1px solid #333; display: flex; justify-content: space-between; align-items: center">
      <h3 style="color: #fff; margin: 0">Version History</h3>
      <button class="btn-secondary" @click="emit('close')" style="padding: 4px 8px">Close</button>
    </div>

    <div style="padding: 16px">
      <button class="btn-primary" @click="handleCreateVersion" style="width: 100%; margin-bottom: 16px">
        Save Snapshot
      </button>
    </div>

    <div style="flex: 1; overflow: auto; padding: 0 16px">
      <div v-if="loading" style="text-align: center; color: #666; padding: 20px">Loading...</div>
      <div v-else-if="versions.length === 0" style="text-align: center; color: #666; padding: 20px">No versions yet</div>
      <div
        v-for="version in versions"
        :key="version.id"
        style="padding: 12px; margin-bottom: 8px; background-color: #2d2d2d; border-radius: 4px; border: 1px solid #333"
      >
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px">
          <span style="color: #fff; font-weight: 500">Version {{ version.versionNumber }}</span>
          <button
            class="btn-secondary"
            @click="handleRestore(version.id)"
            style="padding: 2px 8px; font-size: 12px"
          >
            Restore
          </button>
        </div>
        <div style="font-size: 12px; color: #888">
          {{ version.createdBy }} - {{ new Date(version.createdAt).toLocaleString() }}
        </div>
      </div>
    </div>
  </div>
</template>
