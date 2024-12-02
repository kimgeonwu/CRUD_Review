package com.springbootstudy.bbs.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/writeForm").setViewName("views/writeForm");
		registry.addViewController("/writeBoard").setViewName("views/writeForm");
		// 로그인 폼 뷰 전용 컨트롤러 설정 추가
		registry.addViewController("/loginForm").setViewName("member/loginForm");
		// 회원가입 폼 뷰 전용 컨트롤러 설정 추가
		registry.addViewController("/joinForm").setViewName("memberJoinForm");
	}
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// /resources/** 로 요청되는 리소스 요청 설정
		registry.addResourceHandler("/resources/files/**");
		
		registry.addResourceHandler("/resources/files/**")	
		// file: 프로토콜을 사용하면 업로드한 이미지가 바로 보임
				.addResourceLocations("file:./src/main/resources/static/files/")
				.setCachePeriod(1); // 캐쉬 지속시간 (초) 	
	}
}
