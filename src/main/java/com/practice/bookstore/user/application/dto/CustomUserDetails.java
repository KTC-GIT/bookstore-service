package com.practice.bookstore.user.application.dto;

import com.practice.bookstore.user.domain.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails{

    private final User user;

    // 나중에 컨트롤러에서 user.getId() 꺼낼 때 씀
    public Long getId(){
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        // v0.1은 권한 구분 없이 무조건 USER로 퉁치기.
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword(){return user.getPassword();}

    @Override
    public String getUsername(){return user.getEmail();}    // 이메일로 로그인할거임.

    // 계정 만료/잠금 여부 (일단 다 true로 통과)
    @Override public boolean isAccountNonExpired(){return true;}
    @Override public boolean isAccountNonLocked(){return true;}
    @Override public boolean isCredentialsNonExpired(){return true;}
    @Override public boolean isEnabled(){return true;}

}
