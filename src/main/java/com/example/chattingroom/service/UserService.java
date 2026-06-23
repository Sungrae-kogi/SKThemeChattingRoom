package com.example.chattingroom.service;

import com.example.chattingroom.dto.UserDTO;
import com.example.chattingroom.entity.UserEntity;
import com.example.chattingroom.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(UserDTO userDTO) {
        // 1. 중복 가입 방지 체크
        if (userMapper.selectByUsername(userDTO.getUsername()) != null) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // 2. 패스워드 암호화
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

        // 3. DTO -> Entity 변환
        UserEntity userEntity = UserEntity.builder()
                .username(userDTO.getUsername())
                .password(encodedPassword)
                .build();

        // 4. 데이터 저장
        userMapper.insertUser(userEntity);
    }

    // Spring Security의 로그인 흐름과 연동되는 메서드
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        // 사용자가 요청한 역할(Role) 구분을 생략하기 위해 비어있는 권한 리스트 제공
        return new User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList()
        );
    }
}
