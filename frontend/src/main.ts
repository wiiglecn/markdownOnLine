import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './styles/global.css'

const app = createApp(App)
app.use(createPinia())
app.use(router)

// Suppress known non-critical errors
app.config.errorHandler = (err) => {
  const message = err instanceof Error ? err.message : String(err)

  // Yjs "Unexpected case" - non-fatal sync warning, happens during WebSocket sync
  if (message.includes('Unexpected case')) return

  console.error('Global error:', err)
}

// Suppress Yjs internal "Caught error while handling a Yjs update" warnings
const originalConsoleError = console.error
console.error = (...args: any[]) => {
  const msg = args[0]
  if (typeof msg === 'string' && msg.includes('Caught error while handling a Yjs update')) return
  originalConsoleError.apply(console, args)
}

app.mount('#root')
