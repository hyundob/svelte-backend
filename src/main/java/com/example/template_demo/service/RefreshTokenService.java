package com.example.template_demo.service;

import com.example.template_demo.entity.RefreshToken;
import com.example.template_demo.repository.RefreshTokenRepository;
import com.example.template_demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import lombok.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final JwtUtil jwtUtil;
    @Value("${jwt.refreshExpirationMs}")
    private Long refreshExpiryMs;

    public RefreshTokenService(RefreshTokenRepository repo, JwtUtil jwtUtil) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
    }

    public String createRefreshToken(String username) {
        String token = jwtUtil.generateRefreshToken(username);
        Instant expiry = Instant.now().plusMillis(refreshExpiryMs);

        RefreshToken rt = repo.findByUsername(username).orElse(new RefreshToken());
        rt.setUsername(username);
        rt.setToken(token);
        rt.setExpiryDate(expiry);
        repo.save(rt);

        return token;
    }

    public boolean validateRefreshToken(String token) {
        try {
            String username = jwtUtil.validateAndExtract(token);
            RefreshToken rt = repo.findByUsername(username).orElseThrow(() -> new RuntimeException("No matching token"));
            return rt.getToken().equals(token) && rt.getExpiryDate().isAfter(Instant.now());
        } catch (Exception e) {
            return false;
        }
    }

    public void deleteRefreshToken(String username) {
        repo.deleteByUsername(username);
    }
}
