package com.project.jticketing.domain.user.dto.response;

import com.project.jticketing.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserInfoResponseDto {
    String email;
    String nickname;
    String phoneNumber;
    String address;

    public UserInfoResponseDto(User user){
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
    }
}
