<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../store/authStore'
import { useEditorStore } from '../store/editorStore'
import { filesApi, type FileItem } from '../api/files'
import MonacoEditor from '../components/MonacoEditor.vue'
import CollaborationCursors from '../components/CollaborationCursors.vue'
import SaveStatus from '../components/SaveStatus.vue'
import MarkdownPreview from '../components/MarkdownPreview.vue'
import VersionPanel from '../components/VersionPanel.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const editorStore = useEditorStore()

const file = ref<FileItem | null>(null)
const showPreview = ref(false)
const showVersions = ref(false)
const previewContent = ref('')

const fileId = ref<string>('')

// Sync fileId from route params
watch(() => route.params.fileId, (id) => {
  if (id) fileId.value = id as string
}, { immediate: true })

// On mount: load file metadata and connect
onMounted(() => {
  const id = fileId.value
  const token = auth.token
  if (!id || !token) return

  filesApi.getFile(Number(id)).then(res => {
    file.value = res.data
  })

  editorStore.connect(id, token)
})

// Only observe Yjs doc for preview when preview is actually visible
let previewObserver: (() => void) | null = null

// Cleanup on unmount
onUnmounted(() => {
  editorStore.disconnect()
  previewObserver?.()
})

watch([() => editorStore.doc, fileId, showPreview], ([doc, id, visible]) => {
  // Clean up old observer
  if (previewObserver) {
    previewObserver()
    previewObserver = null
  }

  if (!doc || !id || !visible) return

  const ytext = doc.getText(`text:${id}`)
  const updatePreview = () => {
    previewContent.value = ytext.toString()
  }

  ytext.observe(updatePreview)
  updatePreview()

  previewObserver = () => ytext.unobserve(updatePreview)
})

function reloadPage() {
  window.location.reload()
}
</script>

<template>
  <div style="display: flex; flex-direction: column; height: 100vh">
    <!-- Header -->
    <div style="display: flex; justify-content: space-between; align-items: center; padding: 8px 16px; border-bottom: 1px solid #333; background-color: #1e1e1e">
      <div style="display: flex; align-items: center; gap: 12px">
        <button class="btn-secondary" @click="router.push('/files')" style="padding: 4px 12px">Back</button>
        <span style="color: #fff; font-weight: 500">{{ file?.name }}</span>
        <SaveStatus :isConnected="editorStore.isConnected" />
      </div>
      <div style="display: flex; align-items: center; gap: 12px">
        <CollaborationCursors :users="editorStore.connectedUsers" />
        <button class="btn-secondary" @click="showVersions = true" style="padding: 4px 12px">History</button>
        <button
          v-if="file?.contentType === 'MARKDOWN'"
          class="btn-secondary"
          @click="showPreview = !showPreview"
          style="padding: 4px 12px"
        >
          {{ showPreview ? 'Hide Preview' : 'Show Preview' }}
        </button>
      </div>
    </div>

    <!-- Editor Area -->
    <div style="flex: 1; display: flex; overflow: hidden">
      <div style="flex: 1; display: flex; flex-direction: column; overflow: hidden">
        <div v-if="!file || !editorStore.doc || !editorStore.provider" style="text-align: center; padding: 60px">
          Loading editor...
        </div>
        <div v-else style="flex: 1; overflow: hidden">
          <MonacoEditor
            :fileId="fileId"
            :contentType="file.contentType as 'TEXT' | 'MARKDOWN'"
            :doc="editorStore.doc"
            :provider="editorStore.provider"
          />
        </div>
      </div>
      <div v-if="showPreview && file?.contentType === 'MARKDOWN'" style="width: 50%; border-left: 1px solid #333">
        <MarkdownPreview :content="previewContent" />
      </div>
    </div>

    <VersionPanel
      :fileId="Number(fileId)"
      :open="showVersions"
      @close="showVersions = false"
      @restore="reloadPage"
    />
  </div>
</template>
