package com.project.jticketing.domain.user.service;

import com.project.jticketing.config.security.UserDetailsImpl;
import com.project.jticketing.domain.user.dto.request.UserModifyRequestDto;
import com.project.jticketing.domain.user.dto.response.UserDeleteResponseDto;
import com.project.jticketing.domain.user.dto.response.UserInfoResponseDto;
import com.project.jticketing.domain.user.entity.User;
import com.project.jticketing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDeleteResponseDto deleteUser(Long userId, UserDetailsImpl authUser) {
        User user = userRepository.findByEmail(authUser.getUser().getEmail()).orElseThrow(
                () -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 유저 아이디 검증
        if(!user.getId().equals(userId) || !authUser.getUser().getId().equals(userId)) {
            throw new RuntimeException("유저를 찾을 수 없습니다.");
        }

        userRepository.delete(user);

        return new UserDeleteResponseDto(user.getId());
    }

    @Transactional
    public UserInfoResponseDto getUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("유저를 찾을 수 없습니다")
        );
        return new UserInfoResponseDto(user);
    }

    @Transactional
    public UserInfoResponseDto modifyUser(Long userId, UserModifyRequestDto userModifyRequestDto, UserDetailsImpl authUser) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("유저를 찾을 수 없습니다")
        );
        if(!user.getId().equals(userId) || !authUser.getUser().getId().equals(userId)) {
            throw new RuntimeException("유저를 찾을 수 없습니다.");
        }

        user.setPassword(userModifyRequestDto.getPassword());
        user.setNickname(userModifyRequestDto.getNickname());
        user.setPhoneNumber(userModifyRequestDto.getPhoneNumber());
        user.setAddress(userModifyRequestDto.getAddress());
        userRepository.save(user);

        return new UserInfoResponseDto(user);
    }


}
