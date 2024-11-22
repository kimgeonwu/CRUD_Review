package com.springbootstudy.bbs.controller;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.springbootstudy.bbs.domain.Member;
import com.springbootstudy.bbs.service.MemberService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// 스프링 MVC의 컨트롤러 클래스 임을 정의
@Controller
// 컨트롤러 클래스에서 관리하는 특정 속성을 세션 범위에 저장할 수 있도록 설정
@SessionAttributes("member")
public class MemberController {

	// 회원 관련 Business 로직을 담당하는 객체를 의존성 주입하도록 설정
	@Autowired
	public MemberService memberService;

	// "/login"으로 들어오는 POST 방식의 요청을 처리하는 메서드
	@PostMapping("/login")
	public String login(Model model, @RequestParam("userId") String id, @RequestParam("pass") String pass,
						HttpSession session, HttpServletResponse resp) throws ServletException, IOException {
		
		// MemberService 클래스를 사용해 로그인 성공여부 확인
		int result = memberService.login(id, pass);
		
		if(result == -1) { // 회원 아이디가 존재하지 않으면
			resp.setContentType("text/html; charset=utf-8");
			PrintWriter out = resp.getWriter();
			out.println("<script>");
			out.println("	alert('존재하지 않는 아이디 입니다.');");
			out.println("	history.back();");
			out.println("</script>");
			
			return null;
			
		} else if(result == 0) { // 비밀번호가 틀리면
			resp.setContentType("text/html; charset=utf-8");
			PrintWriter out = resp.getWriter();
			out.println("<script>");
			out.println("	alert('비밀번호가 다릅니다.');");
			out.println("	location.href='loginForm'");
			out.println("</script>");
			
		return null;
		}
		// 로그인을 성공하면 회원 정보를 DB에서 가져와 세션에 저장
		Member member = memberService.getMember(id);
		
		// 로그인이 성공하면 게시글 리스트로 리다이렉트
		return "redirect:/boardList";
	}
}
	
