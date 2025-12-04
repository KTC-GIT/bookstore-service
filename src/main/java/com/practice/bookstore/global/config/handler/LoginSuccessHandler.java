package com.practice.bookstore.global.config.handler;

import com.practice.bookstore.user.application.dto.CustomUserDetails;
import com.practice.bookstore.user.application.dto.SessionUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException{

        // 1. 인증된 사용자 정보 가져오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 2. 세션용  DTO 생성 (Entity -> DTO 변환)
        SessionUser sessionUser = SessionUser.from(userDetails.getUser());

        // 3. 세션 가져오기 (없으면 생성)
        HttpSession session = request.getSession();

        // 4. 세션에 DTO 저장 (이제 Redis에 이 친구가 JSON으로 저장됨!)
        session.setAttribute("user", sessionUser);

        // 5. 로그인 후 이동할 페이지
        response.sendRedirect("/");
    }
}
