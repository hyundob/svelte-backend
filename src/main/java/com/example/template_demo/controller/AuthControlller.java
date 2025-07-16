package com.example.template_demo.controller;
import com.example.template_demo.dto.LoginRequest;
import com.example.template_demo.dto.RegisterRequest;
import com.example.template_demo.entity.Member;
import com.example.template_demo.repository.MemberRepository;
import com.example.template_demo.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthControlller {
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public AuthControlller(MemberRepository memberRepository, JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
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
}
