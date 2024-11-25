package com.project.jticketing.domain.auth.service;

import com.project.jticketing.config.JwtUtil;
import com.project.jticketing.domain.auth.dto.request.SignupRequestDto;
import com.project.jticketing.domain.user.entity.User;
import com.project.jticketing.domain.user.enums.UserRole;
import com.project.jticketing.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    public String signupUser(@Valid SignupRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 email 입니다");
        }

        String password = passwordEncoder.encode(requestDto.getPassword());
        UserRole userRole = UserRole.USER;

        User user = new User(
                requestDto.getEmail(), password, requestDto.getNickname(),
                requestDto.getAddress(), requestDto.getPhoneNumber(), userRole);

        User savedUser = userRepository.save(user);

        return jwtUtil.createToken(
                savedUser.getId(), savedUser.getEmail(), savedUser.getNickname(), savedUser.getUserRole());
    }

    public String signupAdmin(@Valid SignupRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 email 입니다");
        }

        String password = passwordEncoder.encode(requestDto.getPassword());
        UserRole adminRole = UserRole.ADMIN;

        User user = new User(
                requestDto.getEmail(), password, requestDto.getNickname(),
                requestDto.getAddress(), requestDto.getPhoneNumber(), adminRole);

        User savedUser = userRepository.save(user);
        return jwtUtil.createToken(
                savedUser.getId(), savedUser.getEmail(), savedUser.getNickname(), savedUser.getUserRole());
    }
}
