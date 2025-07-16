package com.example.template_demo.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/userinfo")
    public String getUserInfo() {
        return "인증된 사용자입니다.";
    }
}
