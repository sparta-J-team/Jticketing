package com.project.jticketing.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.jticketing.config.security.UserDetailsImpl;
import com.project.jticketing.domain.auth.dto.request.LoginRequestDto;
import com.project.jticketing.domain.auth.dto.request.SignupRequestDto;
import com.project.jticketing.domain.user.enums.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "인증 및 Jwt 발급")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequestDto loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword(), null)
            );
        } catch (IOException e) {
            log.error("로그인 에러:{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공, Jwt 발급");
        Long userId = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getId();
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        String nickname = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getNickname();
        UserRole role =  ((UserDetailsImpl) authResult.getPrincipal()).getUser().getUserRole();

        String jwt = jwtUtil.createToken(userId, username, nickname, role);
        response.addHeader("Authorization", jwt);
    }
}
