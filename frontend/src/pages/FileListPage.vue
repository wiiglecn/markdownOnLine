<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/authStore'
import { useFileStore } from '../store/fileStore'
import FileCard from '../components/FileCard.vue'
import FolderCard from '../components/FolderCard.vue'
import CreateDialog from '../components/CreateDialog.vue'

const router = useRouter()
const auth = useAuthStore()
const fileStore = useFileStore()

const showCreateFolder = ref(false)
const showCreateFile = ref(false)

onMounted(() => {
  fileStore.fetchFolders(null)
  fileStore.fetchFiles(null)
})
</script>

<template>
  <div class="container">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px">
      <div style="display: flex; align-items: center; gap: 12px">
        <button class="btn-secondary" @click="fileStore.navigateToFolder(null)">Root</button>
        <h1>File Online</h1>
      </div>
      <div style="display: flex; align-items: center; gap: 12px">
        <span>Welcome, {{ auth.user?.nickname || auth.user?.email }}</span>
        <button class="btn-secondary" @click="auth.logout()">Logout</button>
      </div>
    </div>

    <div style="display: flex; gap: 8px; margin-bottom: 24px">
      <button class="btn-primary" @click="showCreateFolder = true">New Folder</button>
      <button class="btn-primary" @click="showCreateFile = true">New File</button>
    </div>

    <div v-if="fileStore.loading" style="text-align: center; padding: 40px">Loading...</div>

    <template v-else>
      <div v-if="fileStore.folders.length > 0" style="margin-bottom: 24px">
        <h2 style="margin-bottom: 12px">Folders</h2>
        <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 12px">
          <FolderCard
            v-for="folder in fileStore.folders"
            :key="folder.id"
            :folder="folder"
            @open="(id: number) => fileStore.navigateToFolder(id)"
            @delete="(id: number) => fileStore.deleteFolder(id)"
          />
        </div>
      </div>

      <div v-if="fileStore.files.length > 0">
        <h2 style="margin-bottom: 12px">Files</h2>
        <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 12px">
          <FileCard
            v-for="file in fileStore.files"
            :key="file.id"
            :file="file"
            @open="(id: number) => router.push(`/editor/${id}`)"
            @rename="(id: number, name: string) => fileStore.renameFile(id, name)"
            @delete="(id: number) => fileStore.deleteFile(id)"
          />
        </div>
      </div>

      <div v-if="fileStore.folders.length === 0 && fileStore.files.length === 0" style="text-align: center; padding: 60px; color: #666">
        <p>No files or folders yet. Create one!</p>
      </div>
    </template>

    <CreateDialog
      type="folder"
      :open="showCreateFolder"
      @close="showCreateFolder = false"
      @create="(name: string) => fileStore.createFolder(name)"
    />
    <CreateDialog
      type="file"
      :open="showCreateFile"
      @close="showCreateFile = false"
      @create="(name: string, contentType?: string) => fileStore.createFile(name, contentType as any)"
    />
  </div>
</template>
