package com.project.jticketing.config.security;

import com.project.jticketing.domain.user.entity.User;
import com.project.jticketing.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRole role = user.getUserRole();
        String authority = role.toString();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 사용자는 email 을 id로 하여 로그인 합니다.
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    public User getUser() {
        return user;
    }
}