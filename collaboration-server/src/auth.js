const axios = require('axios');

const SPRING_BOOT_URL = 'http://localhost:8080';

async function validateToken(token) {
  try {
    const response = await axios.post(`${SPRING_BOOT_URL}/internal/auth/check-token`, {
      token: token
    });
    return response.data;
  } catch (error) {
    console.error('[Auth] Token validation failed:', error.message);
    return { valid: false };
  }
}

module.exports = { validateToken };
