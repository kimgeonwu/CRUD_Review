package com.springbootstudy.bbs.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

// 스프링 DI 컨테이너가 초기화 될 때 빈으로 등록된다.
@Configuration
// 요청 URL이 스프링 시큐리티의 제어를 받도록 지정하는 애노테이션
@EnableWebSecurity
public class SecurityConfig {

	// 이 메서드 안에서 반환하는 객체는 스프링 DI 컨테이너에 의해서 스프링 Bean으로 관리
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// @EnableWebSecurity을 사용하면 내부적으로 SpringSecurityFilterChain이 동작하여
	// URL 필터가 자동으로 적용
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
				.requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
				.csrf(csrf -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
				.csrf(csrf -> csrf.disable())

				.logout((logout) -> logout
						// .logoutUrl("/logout") // 기본 URL은 POST 방식의 /logout
						.logoutSuccessUrl("/loginForm") // 로그아웃 성공 리다이렉트 페이지
						.invalidateHttpSession(true)); // 기존 세션 삭제 여부

		return http.build();
	}
}
