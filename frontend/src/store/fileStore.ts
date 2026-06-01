import { defineStore } from 'pinia'
import { ref } from 'vue'
import { filesApi, type Folder, type FileItem } from '../api/files'

export const useFileStore = defineStore('file', () => {
  const folders = ref<Folder[]>([])
  const files = ref<FileItem[]>([])
  const currentFolderId = ref<number | null>(null)
  const breadcrumbs = ref<Folder[]>([])
  const loading = ref(false)

  async function fetchFolders(parentId?: number | null) {
    loading.value = true
    try {
      const response = await filesApi.listFolders(parentId)
      folders.value = response.data
    } finally {
      loading.value = false
    }
  }

  async function fetchFiles(folderId?: number | null) {
    loading.value = true
    try {
      const response = await filesApi.listFiles(folderId)
      files.value = response.data
    } finally {
      loading.value = false
    }
  }

  async function createFolder(name: string) {
    await filesApi.createFolder(name, currentFolderId.value)
    await fetchFolders(currentFolderId.value)
  }

  async function createFile(name: string, contentType: 'TEXT' | 'MARKDOWN') {
    await filesApi.createFile(name, contentType, currentFolderId.value)
    await fetchFiles(currentFolderId.value)
  }

  async function renameFile(id: number, name: string) {
    await filesApi.renameFile(id, name)
    await fetchFiles(currentFolderId.value)
  }

  async function deleteFile(id: number) {
    await filesApi.deleteFile(id)
    await fetchFiles(currentFolderId.value)
  }

  async function deleteFolder(id: number) {
    await filesApi.deleteFolder(id)
    await fetchFolders(currentFolderId.value)
  }

  function navigateToFolder(folderId: number | null) {
    currentFolderId.value = folderId
    fetchFolders(folderId)
    fetchFiles(folderId)
  }

  return {
    folders, files, currentFolderId, breadcrumbs, loading,
    fetchFolders, fetchFiles, createFolder, createFile,
    renameFile, deleteFile, deleteFolder, navigateToFolder,
  }
})
