package com.fileonline.controller;

import com.fileonline.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalAuthController {

    private final JwtUtil jwtUtil;

    @PostMapping("/auth/check-token")
    public ResponseEntity<Map<String, Object>> checkToken(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("valid", false));
        }

        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            String email = jwtUtil.getEmailFromToken(token);
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "userId", userId,
                "email", email
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("valid", false));
        }
    }
}
