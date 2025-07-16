package com.example.template_demo.controller;
import com.example.template_demo.dto.LoginRequest;
import com.example.template_demo.dto.RegisterRequest;
import com.example.template_demo.entity.Member;
import com.example.template_demo.repository.MemberRepository;
import com.example.template_demo.security.JwtUtil;
import com.example.template_demo.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthControlller {
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService service;

    public AuthControlller(MemberRepository memberRepository, JwtUtil jwtUtil, RefreshTokenService service) {
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        return memberRepository.findByUsername(request.getUsername())
                .filter(user -> user.getPassword().equals(request.getPassword()))
                .map(user -> {
                    String token = jwtUtil.generateToken(user.getUsername());
                    return ResponseEntity.ok(Map.of("token", token));
                })
                .orElse(ResponseEntity.status(401).body(Map.of("error", "Unauthorized")));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (memberRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("이미 존재하는 사용자입니다.");
        }

        Member member = new Member();
        member.setUsername(request.getUsername());
        member.setPassword(request.getPassword());
        System.out.println("Save");
        memberRepository.save(member);
        System.out.println("Save success");

        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue("refreshToken") String rtCookie) {
        if(!service.validateRefreshToken(rtCookie)) {
            return ResponseEntity.status(401).body("Invalid or expired refresh token.");
        }
        String username = jwtUtil.validateRefreshToken(rtCookie);
        String access = jwtUtil.generateToken(username);

        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + access).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue("refreshToken") String rtCookie, HttpServletResponse res) {
        String username = jwtUtil.validateRefreshToken(rtCookie);
        service.deleteRefreshToken(username);
        res.setHeader("Set-Cookie", "refreshToken=; Max-Age=0; HttpOnly; Path=/");
        return ResponseEntity.ok("Logged Out");
    }
}
