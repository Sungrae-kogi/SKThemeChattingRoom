package com.example.chattingroom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 비활성화 (개발 편의를 위해 일단 해제)
                .csrf(csrf -> csrf.disable())

                // 2. 경로 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 로그인, 회원가입 페이지 및 관련 API, 정적 리소스는 누구나 접근 허용
                        .requestMatchers("/login.html", "/login", "/signup.html", "/api/signup", "/css/**", "/js/**",
                                "/favicon.ico")
                        .permitAll()
                        // 그 외 모든 요청(채팅 포함)은 로그인 필요
                        .anyRequest().authenticated())

                // 3. 커스텀 로그인 폼 사용
                .formLogin(form -> form
                        .loginPage("/login.html") // 우리가 만든 커스텀 로그인 페이지
                        .loginProcessingUrl("/login") // 로그인 form action 경로
                        .defaultSuccessUrl("/", true) // 로그인 성공 시 이동할 경로
                        .failureUrl("/login.html?error") // 실패 시 파라미터와 함께 이동
                        .permitAll())

                // 4. 기본 로그아웃 설정
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());

        return http.build();
    }
}
