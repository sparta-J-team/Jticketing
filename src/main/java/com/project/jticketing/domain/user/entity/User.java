package com.project.jticketing.domain.user.entity;

import com.project.jticketing.domain.common.entity.Timestamped;
import com.project.jticketing.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends Timestamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    private String nickname;

    private String address;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    public User(String email, String password, String nickname, String address, String phoneNumber, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.userRole = userRole;
    }
}