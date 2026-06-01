const { WebSocketServer } = require('ws');
const http = require('http');
const Y = require('yjs');
const { setupWSConnection, setPersistence } = require('y-websocket/bin/utils');
const { validateToken } = require('./auth');
const { loadDocument, saveDocument } = require('./persistence');

const PORT = 3001;

// Register custom persistence adapter with y-websocket
// This ensures loadDocument/saveDocument are called on the SAME doc instances
// that setupWSConnection creates internally
setPersistence({
  bindState: async (docName, ydoc) => {
    const fileId = docName.replace('doc:', '');
    console.log(`[Persistence] Loading document ${fileId}`);
    const snapshot = await loadDocument(fileId);
    if (snapshot) {
      Y.applyUpdate(ydoc, new Uint8Array(snapshot));
      console.log(`[Persistence] Loaded ${snapshot.byteLength} bytes for document ${fileId}`);
    }

    // Auto-save on document changes with 3s debounce
    let saveTimeout = null;
    ydoc.on('update', (update, origin) => {
      if (saveTimeout) clearTimeout(saveTimeout);
      saveTimeout = setTimeout(async () => {
        const state = Y.encodeStateAsUpdate(ydoc);
        await saveDocument(fileId, state);
        console.log(`[Save] Document ${fileId} saved (${state.length} bytes)`);
      }, 3000);
    });
  },
  writeState: async (docName, ydoc) => {
    // Save final state when last client disconnects
    const fileId = docName.replace('doc:', '');
    const state = Y.encodeStateAsUpdate(ydoc);
    await saveDocument(fileId, state);
    console.log(`[Save] Document ${fileId} final save on disconnect (${state.length} bytes)`);
  }
});

const server = http.createServer((req, res) => {
  res.writeHead(200, { 'Content-Type': 'text/plain' });
  res.end('Collaboration server running');
});

const wss = new WebSocketServer({ noServer: true });

server.on('upgrade', (request, socket, head) => {
  const url = new URL(request.url, `http://localhost:${PORT}`);
  const token = url.searchParams.get('token');
  const fileId = url.searchParams.get('fileId');

  if (!token || !fileId) {
    socket.write('HTTP/1.1 401 Unauthorized\r\n\r\n');
    socket.destroy();
    return;
  }

  validateToken(token).then(result => {
    if (!result.valid) {
      socket.write('HTTP/1.1 401 Unauthorized\r\n\r\n');
      socket.destroy();
      return;
    }

    request.userId = result.userId;
    request.userEmail = result.email;
    request.fileId = fileId;

    wss.handleUpgrade(request, socket, head, (ws) => {
      wss.emit('connection', ws, request);
    });
  }).catch(() => {
    socket.write('HTTP/1.1 500 Internal Server Error\r\n\r\n');
    socket.destroy();
  });
});

wss.on('connection', async (ws, request) => {
  const fileId = request.fileId;
  const docName = `doc:${fileId}`;

  // Rewrite request.url so setupWSConnection extracts the correct docName from pathname.
  request.url = `/${docName}?fileId=${fileId}`;

  setupWSConnection(ws, request, {
    docName: docName,
    gc: true,
  });

  ws.on('close', () => {
    console.log(`[WS] User ${request.userEmail} disconnected from room ${docName}`);
  });
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`Collaboration server running on port ${PORT}, accessible from all network interfaces`);
});
