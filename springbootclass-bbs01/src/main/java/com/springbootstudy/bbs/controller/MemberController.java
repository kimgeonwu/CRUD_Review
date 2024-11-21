package com.springbootstudy.bbs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.springbootstudy.bbs.service.MemberService;

// 스프링 MVC의 컨트롤러 클래스 임을 정의
@Controller
// 컨트롤러 클래스에서 관리하는 특정 속성을 세션 범위에 저장할 수 있도록 설정
@SessionAttributes("member")
public class MemberController {

	// 회원 관련 Business 로직을 담당하는 객체를 의존성 주입하도록 설정
	@Autowired
	public MemberService memberService;
	
	// "/login"으로 들어오는 POST 방식의 요청을 처리하는 메서드
	
	
}
