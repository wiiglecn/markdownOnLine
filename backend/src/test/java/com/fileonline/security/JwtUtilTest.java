package com.fileonline.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdGVzdGluZy1wdXJwb3Nl");
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 86400000L);
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        String token = jwtUtil.generateToken(1L, "test@example.com");

        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = jwtUtil.validateToken(token);
        assertEquals("1", claims.getSubject());
        assertEquals("test@example.com", claims.get("email", String.class));
    }

    @Test
    void getUserIdFromToken_shouldExtractUserId() {
        String token = jwtUtil.generateToken(42L, "user@test.com");

        Long userId = jwtUtil.getUserIdFromToken(token);
        assertEquals(42L, userId);
    }

    @Test
    void getEmailFromToken_shouldExtractEmail() {
        String token = jwtUtil.generateToken(1L, "extract@test.com");

        String email = jwtUtil.getEmailFromToken(token);
        assertEquals("extract@test.com", email);
    }

    @Test
    void isTokenExpired_shouldReturnFalse_forValidToken() {
        String token = jwtUtil.generateToken(1L, "test@example.com");

        assertFalse(jwtUtil.isTokenExpired(token));
    }
}
