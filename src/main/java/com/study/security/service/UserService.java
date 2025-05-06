package com.study.security.service;

import com.study.security.entity.User;
import com.study.security.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 새 사용자를 생성합니다.
     * 
     * @param user 저장할 사용자 정보
     * @return 저장된 사용자 정보
     */
    @Transactional
    public User createUser(User user) {
        // 비밀번호 암호화
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * 모든 사용자 목록을 조회합니다.
     * 
     * @return 사용자 목록
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 사용자명으로 사용자를 조회합니다.
     * 
     * @param username 조회할 사용자명
     * @return 사용자 정보 (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 사용자를 삭제합니다.
     * 
     * @param id 삭제할 사용자 ID
     */
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    /**
     * 기본 사용자를 초기화합니다.
     * 애플리케이션 시작 시 기본 사용자가 없으면 생성합니다.
     */
    @Transactional
    public void initDefaultUsers() {
        // 관리자 사용자가 없으면 생성
        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin")) // 비밀번호 암호화
                    .email("admin@example.com")
                    .roles(Collections.singletonList("ADMIN"))
                    .enabled(true)
                    .build();
            userRepository.save(adminUser);
        }
        
        // 일반 사용자가 없으면 생성
        if (userRepository.findByUsername("user").isEmpty()) {
            User regularUser = User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user")) // 비밀번호 암호화
                    .email("user@example.com")
                    .roles(Collections.singletonList("USER"))
                    .enabled(true)
                    .build();
            userRepository.save(regularUser);
        }
    }
    
    /**
     * 사용자 자격 증명을 검증합니다.
     * 사용자명과 비밀번호가 일치하면 사용자 정보를 반환합니다.
     * 
     * @param username 사용자명
     * @param rawPassword 암호화되지 않은 비밀번호
     * @return 검증된 사용자 정보 (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<User> validateUserCredentials(String username, String rawPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        
        User user = userOpt.get();
        
        // 계정 비활성화 확인
        if (!user.isEnabled()) {
            return Optional.empty();
        }
        
        // 비밀번호 검증
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            return Optional.empty();
        }
        
        return Optional.of(user);
    }
}
