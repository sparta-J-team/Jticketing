package com.project.jticketing.domain.user.dto.request;

import com.project.jticketing.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class UserModifyRequestDto {
    String password;
    String nickname;
    String phoneNumber;
    String address;

    public UserModifyRequestDto(User user) {
        this.password = user.getPassword();
        this.nickname = user.getNickname();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
    }

}
