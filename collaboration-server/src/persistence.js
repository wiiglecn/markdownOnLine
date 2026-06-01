const axios = require('axios');

const SPRING_BOOT_URL = 'http://localhost:8080';

function formatError(error) {
  if (!error.response) return error.message;
  const { status, data } = error.response;
  let body;
  if (Buffer.isBuffer(data)) {
    body = data.toString('utf-8');
  } else if (data instanceof ArrayBuffer || ArrayBuffer.isView(data)) {
    body = Buffer.from(data).toString('utf-8');
  } else if (typeof data === 'object') {
    body = JSON.stringify(data);
  } else {
    body = String(data);
  }
  return `status=${status} body=${body}`;
}

async function loadDocument(fileId) {
  try {
    const response = await axios.get(`${SPRING_BOOT_URL}/internal/docs/${fileId}/snapshot`, {
      responseType: 'arraybuffer'
    });
    if (response.data && response.data.byteLength > 0) {
      return response.data;
    }
    return null;
  } catch (error) {
    if (error.response && error.response.status === 404) {
      return null;
    }
    console.error(`[Persistence] Failed to load document ${fileId}: ${formatError(error)}`);
    return null;
  }
}

async function saveDocument(fileId, state) {
  try {
    await axios.post(`${SPRING_BOOT_URL}/internal/docs/${fileId}/snapshot`, state, {
      headers: { 'Content-Type': 'application/octet-stream' },
      maxContentLength: Infinity,
      maxBodyLength: Infinity,
    });
  } catch (error) {
    console.error(`[Persistence] Failed to save document ${fileId}: ${formatError(error)}`);
  }
}

module.exports = { loadDocument, saveDocument };
