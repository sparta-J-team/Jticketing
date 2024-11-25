package com.project.jticketing.domain.auth.controller;

import com.project.jticketing.domain.auth.dto.request.SignupRequestDto;
import com.project.jticketing.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup/user")
    public String signupUser(@Valid @RequestBody SignupRequestDto requestDto) {
        return authService.signupUser(requestDto);
    }

    @PostMapping("/signup/admin")
    public String signupAdmin(@Valid @RequestBody SignupRequestDto requestDto) {
        return authService.signupAdmin(requestDto);
    }
}
