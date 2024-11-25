package com.project.jticketing.domain.user.controller;

import com.project.jticketing.config.security.UserDetailsImpl;
import com.project.jticketing.domain.user.dto.request.UserModifyRequestDto;
import com.project.jticketing.domain.user.dto.response.UserDeleteResponseDto;
import com.project.jticketing.domain.user.dto.response.UserInfoResponseDto;
import com.project.jticketing.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<UserDeleteResponseDto> deleteUser(@PathVariable Long userId , @AuthenticationPrincipal UserDetailsImpl authUser) {
        return new ResponseEntity<>(
                    userService.deleteUser(userId,authUser),
                    HttpStatus.OK);

    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserInfoResponseDto> findUser(@PathVariable Long userId) {
        return new ResponseEntity<>(
                    userService.findUser(userId),
                    HttpStatus.OK);

    }


    @PatchMapping("/user/{userId}")
    public ResponseEntity<UserInfoResponseDto> modifyUser(@RequestBody UserModifyRequestDto userModifyRequestDto, @PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl authUser) {
        return new ResponseEntity<>(
                userService.modifyUser(userId,userModifyRequestDto,authUser),
                HttpStatus.OK);

    }

}
