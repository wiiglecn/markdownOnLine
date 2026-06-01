<script setup lang="ts">
import { computed } from 'vue'
import { Marked } from 'marked'
import { markedHighlight } from 'marked-highlight'
import hljs from 'highlight.js'

const props = defineProps<{ content: string }>()

const marked = new Marked(
  markedHighlight({
    langPrefix: 'hljs language-',
    highlight(code: string, lang: string) {
      if (lang && hljs.getLanguage(lang)) {
        return hljs.highlight(code, { language: lang }).value
      }
      return hljs.highlightAuto(code).value
    },
  }),
)

const html = computed(() => {
  if (!props.content) return ''
  return marked.parse(props.content) as string
})
</script>

<template>
  <div class="markdown-preview" v-html="html" />
</template>

<style scoped>
.markdown-preview {
  padding: 20px;
  height: 100%;
  overflow: auto;
  background-color: #1e1e1e;
  color: #d4d4d4;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  line-height: 1.6;
}

.markdown-preview :deep(h1) {
  color: #fff;
  border-bottom: 1px solid #444;
  padding-bottom: 8px;
}

.markdown-preview :deep(h2) {
  color: #fff;
  border-bottom: 1px solid #333;
  padding-bottom: 6px;
}

.markdown-preview :deep(h3) {
  color: #fff;
}

.markdown-preview :deep(p) {
  margin-bottom: 12px;
}

.markdown-preview :deep(code) {
  background-color: #2d2d2d;
  padding: 2px 6px;
  border-radius: 3px;
}

.markdown-preview :deep(pre) {
  background-color: #2d2d2d;
  padding: 12px;
  border-radius: 4px;
  overflow: auto;
}

.markdown-preview :deep(pre code) {
  padding: 0;
  background: none;
}

.markdown-preview :deep(blockquote) {
  border-left: 4px solid #4a90d9;
  padding-left: 12px;
  color: #888;
  margin: 12px 0;
}

.markdown-preview :deep(ul),
.markdown-preview :deep(ol) {
  padding-left: 20px;
  margin-bottom: 12px;
}

.markdown-preview :deep(li) {
  margin-bottom: 4px;
}

.markdown-preview :deep(a) {
  color: #4a90d9;
}

.markdown-preview :deep(table) {
  border-collapse: collapse;
  width: 100%;
  margin: 12px 0;
}

.markdown-preview :deep(th) {
  border: 1px solid #444;
  padding: 8px;
  background-color: #2d2d2d;
}

.markdown-preview :deep(td) {
  border: 1px solid #444;
  padding: 8px;
}
</style>
