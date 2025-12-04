package com.practice.bookstore.user.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.practice.bookstore.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // 모르는 필드는 무시!
public class CustomUserDetails implements UserDetails{
    private Long id;
    private String email;
    private String password;
    private String userName;

    public CustomUserDetails(User user){
        this.id=user.getId();
        this.email=user.getEmail();
        this.password=user.getPassword();
        this.userName=user.getUsername();
    }

    @Override
    @JsonIgnore // 이건 JSON에 저장하지마. 읽을 때도 찾지마.
    public Collection<? extends GrantedAuthority> getAuthorities(){
        // v0.1은 권한 구분 없이 무조건 USER로 퉁치기.
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    @JsonIgnore
    public String getPassword(){return this.password;}

    @Override
    public String getUsername(){return this.email;}    // 이메일로 로그인할거임.

    // 계정 만료/잠금 여부 (일단 다 true로 통과)
    @Override public boolean isAccountNonExpired(){return true;}
    @Override public boolean isAccountNonLocked(){return true;}
    @Override public boolean isCredentialsNonExpired(){return true;}
    @Override public boolean isEnabled(){return true;}

}
