package com.study.security.controller;

import com.study.security.dto.LoginRequest;
import com.study.security.jwt.JwtTokenProvider;
import com.study.security.entity.User;
import com.study.security.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// TODO 코드리뷰
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    public AuthController(JwtTokenProvider tokenProvider, UserService userService) {
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }

    /**
     * 사용자 로그인 처리 및 JWT 토큰 발급
     * 
     * 1. UserService를 통해 사용자 검증
     * 2. 검증 성공 시 JWT 토큰 생성 및 반환
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            log.debug("로그인 시도: {}", loginRequest.getUsername());
            
            // UserService를 통해 사용자 검증
            Optional<User> userOpt = userService.validateUserCredentials(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            );
            
            if (userOpt.isEmpty()) {
                log.warn("로그인 실패 - 잘못된 자격 증명: {}", loginRequest.getUsername());
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid username or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            User user = userOpt.get();
            
            // JWT 토큰 생성
            String jwt = tokenProvider.createTokenForUser(user);
            log.debug("로그인 성공: {}, 토큰 발급됨", loginRequest.getUsername());
            
            // 응답 생성
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("username", user.getUsername());
            response.put("roles", user.getRoles());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("로그인 처리 중 오류 발생: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

}
