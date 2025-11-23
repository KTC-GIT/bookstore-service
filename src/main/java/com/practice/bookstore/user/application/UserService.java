package com.practice.bookstore.user.application;

import com.practice.bookstore.user.domain.User;
import com.practice.bookstore.user.domain.UserRepository;
import com.practice.bookstore.user.presentation.dto.UserJoinRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long addUser(UserJoinRequest request){
        // 1. email 중복 확인
        Optional<User> user = userRepository.findByEmail(request.email());
        log.info("user : {}",user.isPresent());

        if(user.isPresent()){
            throw new IllegalArgumentException("email 중복.");
        }

        log.info("request :: {}",request.email());

        User newUser = User.builder()
                .email(request.email())
                .username(request.username())
                .address(request.address())
                .phoneNumber(request.phoneNumber())
                .password(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(newUser);

        return newUser.getId();
    }
}
