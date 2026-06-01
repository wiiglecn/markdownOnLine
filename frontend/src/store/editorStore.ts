import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as Y from 'yjs'
import { WebsocketProvider } from 'y-websocket'

export const useEditorStore = defineStore('editor', () => {
  const doc = ref<Y.Doc | null>(null)
  const provider = ref<WebsocketProvider | null>(null)
  const isConnected = ref(false)
  const connectedUsers = ref<Array<{ clientId: number; user: { name: string; color: string } }>>([])

  function connect(fileId: string, token: string) {
    // If already connected to the same file, skip
    if (provider.value && doc.value && provider.value.roomname === `doc:${fileId}`) return

    // Clean up old connection immediately
    if (provider.value) {
      provider.value.disconnect()
      provider.value = null
    }
    if (doc.value) {
      doc.value.destroy()
      doc.value = null
    }

    const newDoc = new Y.Doc()

    // Pre-register shared type so Yjs transactions don't fail
    newDoc.getText(`text:${fileId}`)

    const host = window.location.hostname
    const wsUrl = `ws://${host}:3001`

    const newProvider = new WebsocketProvider(wsUrl, `doc:${fileId}`, newDoc, {
      connect: true,
      params: { token, fileId },
    })

    newProvider.on('status', (status: { status: string }) => {
      isConnected.value = status.status === 'connected'
    })

    newProvider.awareness.on('change', () => {
      const states = Array.from(newProvider.awareness.getStates().entries())
      connectedUsers.value = states
        .filter(([clientId, state]) => clientId !== newDoc.clientID && state.user)
        .map(([clientId, state]) => ({
          clientId,
          user: state.user,
        }))
    })

    // Set local user awareness
    newProvider.awareness.setLocalStateField('user', {
      name: `User ${Math.floor(Math.random() * 1000)}`,
      color: `#${Math.floor(Math.random() * 16777215).toString(16).padStart(6, '0')}`,
    })

    doc.value = newDoc
    provider.value = newProvider
  }

  function disconnect() {
    // Immediate disconnect - no deferred cleanup
    if (provider.value) {
      provider.value.disconnect()
      provider.value = null
    }
    if (doc.value) {
      doc.value.destroy()
      doc.value = null
    }
    isConnected.value = false
    connectedUsers.value = []
  }

  return { doc, provider, isConnected, connectedUsers, connect, disconnect }
})
