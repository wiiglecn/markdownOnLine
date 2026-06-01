<script setup lang="ts">
import { onMounted, onUnmounted, shallowRef } from 'vue'
import loader from '@monaco-editor/loader'
import * as Y from 'yjs'
import { WebsocketProvider } from 'y-websocket'
import { MonacoBinding } from 'y-monaco'

const props = defineProps<{
  fileId: string
  contentType: 'TEXT' | 'MARKDOWN'
  doc: Y.Doc
  provider: WebsocketProvider
}>()

const containerRef = shallowRef<HTMLDivElement>()
const bindingRef = shallowRef<MonacoBinding>()

onMounted(async () => {
  if (!containerRef.value) return

  // Load Monaco via AMD loader (same as @monaco-editor/react does internally)
  // This loads Monaco asynchronously from CDN, not blocking the main thread
  const monaco = await loader.init()

  const language = props.contentType === 'MARKDOWN' ? 'markdown' : 'plaintext'

  const editor = monaco.editor.create(containerRef.value, {
    language,
    theme: 'vs-dark',
    minimap: { enabled: true },
    wordWrap: 'on',
    fontSize: 14,
    lineNumbers: 'on',
    scrollBeyondLastLine: false,
    automaticLayout: true,
  })

  // Bind Yjs to Monaco (same as React version)
  const ytext = props.doc.getText(`text:${props.fileId}`)
  const binding = new MonacoBinding(
    ytext,
    editor.getModel()!,
    new Set([editor]),
    props.provider.awareness
  )
  bindingRef.value = binding
})

onUnmounted(() => {
  bindingRef.value?.destroy()
})
</script>

<template>
  <div ref="containerRef" style="width: 100%; height: 100%" />
</template>
