package com.springbootstudy.bbs.controller;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.springbootstudy.bbs.domain.Member;
import com.springbootstudy.bbs.service.MemberService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

// 스프링 MVC의 컨트롤러 클래스 임을 정의
@Controller

/* 스프링은 데이터를 세션 영역에 저장할 수 있도록 @SessionAttributes("모델이름")
 * 애노테이션을 제공하고 있다. 클래스 레벨에 @SessionAttributes("모델이름")와
 * 같이 애노테이션과 모델 이름을 지정하고 아래 login() 메서드와 같이 그 컨트롤러 
 * 메서드에서 @SessionAttributes에 지정한 모델이름과 동일한 이름으로 모델에
 * 객체를 추가하면 이 객체를 세션 영역에 저장해 준다.
 **/
@SessionAttributes("member")
@Slf4j
public class MemberController {
	
	// 회원 관련 Business 로직을 담당하는 객체를 의존성 주입하도록 설정
	@Autowired
	private MemberService memberService;
	
	/* "/login"으로 들어오는 POST 방식의 요청을 처리하는 메서드
	 * 
	 * 요청을 처리한 결과를 뷰에 전달하기 위해 사용하는 것이 Model 객체이다.
	 * 컨트롤러는 요청을 처리한 결과 데이터를 모델에 담아 뷰로 전달하고 뷰는
	 * 모델로 부터 데이터를 읽어와 클라이언트로 보낼 결과 페이지를 만들게 된다.   
	 *   
	 * 스프링은 컨트롤러에서 모델에 데이터를 담을 수 있는 다양한 방법을 제공하는데
	 * 아래와 같이 파라미터에 Model을 지정하는 방식이 많이 사용된다. 
	 * @RequestMapping, @PostMapping, @GetMapping 애노테이션이 적용된 메서드의 
	 * 파라미터에 Model을 지정하면 스프링이 이 메서드를 호출하면서 Model 타입의
	 * 객체를 넘겨준다. 
	 * 우리는 Model을 받아 이 객체에 결과 데이터를 담기만 하면 뷰로 전달된다.
	 * 	
	 * @RequestMapping, @PostMapping, @GetMapping 애노테이션이 적용된 메서드의
	 * 파라미터에 @RequestParam 애노테이션을 사용해 파라미터 이름을 지정하면 
	 * 이 애노테이션이 앞에 붙은 매개변수에 파라미터 값을 바인딩 시켜준다.
	 * 
	 * @RequestParam 애노테이션에 사용할 수 있는 속성은 아래와 같다.
	 * value : HTTP 요청 파라미터의 이름을 지정한다.
	 * required : 요청 파라미터가 필수인지 설정하는 속성으로 기본값은 false 이다.
	 * 			이 값이 true인 상태에서 요청 파라미터의 값이 존재하지 않으면
	 * 			스프링은 Exception을 발생시킨다.
	 * defaultValue : 요청 파라미터가 없을 경우 사용할 기본 값을 문자열로 지정한다.
	 * 
	 * @RequestParam(value="id" required="false" defaultValue="")
	 * 
	 * @RequestParam 애노테이션은 요청 파라미터 값을 읽어와 메서드의 
	 * 파라미터 타입에 맞게 변환해 준다.
	 * 스프링은 요청 파라미터의 값으로 변환할 수 없는 경우 400 에러를 발생시킨다.	 
	 **/	 
	@PostMapping("/login")	
	public String login(Model model, @RequestParam("userId") String id, 
			@RequestParam("pass") String pass, 
			HttpSession session, HttpServletResponse response) 
					throws ServletException, IOException {
		log.info("MemberController.login()");
		
		// MemberService 클래스를 사용해 로그인 성공여부 확인
		int result = memberService.login(id, pass);
		
		if(result == -1) { // 회원 아이디가 존재하지 않으면
			response.setContentType("text/html; charset=utf-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("	alert('존재하지 않는 아이디 입니다.');");
			out.println("	history.back();");
			out.println("</script>");
			
			/* 컨트롤러에서 null을 반환하거나 메서드의 반환 타입이 void일 경우
			 * Writer나 OutputStream을 이용해 응답 결과를 직접 작성할 수 있다.
			 * DispatcherServlet을 경유해 리소스 자원에 접근하는 경우에
			 * 자바스크립트의 history.back()은 약간의 문제를 일으킬 수 있다.
			 * history 객체를 이용하는 경우 서버로 요청을 보내는 것이 아니라
			 * 브라우저의 접속 이력에서 이전 페이지로 이동되기 때문에 발생한다. 
			 **/
			return null;
			
		} else if(result == 0) { // 비밀번호가 틀리면
			response.setContentType("text/html; charset=utf-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("	alert('비밀번호가 다릅니다.');");
			out.println("	location.href='loginForm'");
			out.println("</script>");
			
			return null;
		}		
		
		// 로그인을 성공하면 회원 정보를 DB에서 가져와 세션에 저장한다.
		Member member = memberService.getMember(id);
		session.setAttribute("isLogin", true);
		
		/* 클래스 레벨에 @SessionAttributes("member") 애노테이션을
		 * 지정하고 그 컨트롤러의 메서드에서 아래와 같이 동일한 이름으로 모델에
		 * 추가하면 스프링이 세션 영역에 데이터를 저장해 준다.
		 **/ 
		model.addAttribute("member", member);
		System.out.println("member.name : " + member.getName());

		/* 클라이언트 요청을 처리한 후 리다이렉트 해야할 경우 아래와 같이 redirect:
		 * 접두어를 붙여 뷰 이름을 반환하면 된다. 뷰 이름에 redirect 접두어가 붙으면
		 * HttpServletResponse를 사용해서 지정한 경로로 Redirect 된다. 
		 * redirect 접두어 뒤에 경로를 지정할 때 "/"로 시작하면 ContextRoot를
		 * 기준으로 절대 경로 방식으로 Redirect 된다. "/"로 시작하지 않으면 현재 
		 * 경로를 기준으로 상대 경로로 Redirect 된다. 또한 다른 사이트로 Redirect
		 * 되기를 원한다면 redirect:http://사이트 주소를 지정한다.
		 * 
		 * 로그인이 성공하면 게시글 리스트로 리다이렉트 된다.
		 **/		
		return "redirect:/boardList";
	}
	
	/* "/membeLogout"으로 들어오는 GET 방식 요청 처리 메서드
	 * 스프링 시큐리티를 사용하면 스프링 시큐리티 설정이 우선 적용되어 POST 방식의 
	 * "/logout"이 기본 URL로 맵핑된다. GET 방식으로 "/logout" 요청을 보내면 405
	 * (Method Not Allowed)가 발생한다. 그래서 아래와 같이 맵핑 URL을 적용했다.
	 * 
	 * 이 컨트롤러 맵핑은 직접 로그아웃을 처리하는 방법에 대해서 설명하고 있지만
	 * com.springbootstudy.bbs.configurations.SecuurityConfig 클래스에는 스프링
	 * 시큐리티를 적용해 로그인과 로그아웃을 적용하는 설정이 주석으로 설명되어 있다.  
	 **/
	@GetMapping("/memberLogout")
	public String logout(HttpSession session) {	
		log.info("MemberController.logout(HttpSession session)");
		// 현재 세션을 종료하고 새로운 세션을 시작한다.
		session.invalidate();
		
		/* 클라이언트 요청을 처리한 후 리다이렉트 해야할 경우 아래와 같이 redirect:
		 * 접두어를 붙여 뷰 이름을 반환하면 된다. 뷰 이름에 redirect 접두어가 붙으면
		 * HttpServletResponse를  사용해서 지정한 경로로 Redirect 된다. 
		 * redirect 접두어 뒤에 경로를 지정할 때 "/"로 시작하면 ContextRoot를
		 * 기준으로 절대 경로 방식으로 Redirect 된다. "/"로 시작하지 않으면 현재 
		 * 경로를 기준으로 상대 경로로 Redirect 된다. 또한 다른 사이트로 Redirect
		 * 되기를 원한다면 redirect:http://사이트 주소를 지정한다.
		 * 
		 * 로그아웃 되면 로그인 폼으로 리다이렉트 된다.
		 **/
		return "redirect:/loginForm";
	}
	
	/* 회원가입 폼에서 들어오는 아이디 중복 체크 요청을 처리하는 메서드
	 * 새 창으로 요청할 때는 GET 방식 요청을 보내지만 아이디가 이미 사용중이라면
	 * 새 창에서 폼을 통해 POST 방식 요청을 보내기 때문에 두 가지 방식 모두를 이 
	 * 메서드에서 처리하기 위해서 @RequestMapping 애노테이션을 사용하였다.
	 * @RequestMapping에 method 속성을 지정하지 않으면 "/overlapIdCheck"로
	 * 들어오는 GET과 POST 방식 요청을 모두 이 메서드가 받을 수 있다.  
	 **/
	@RequestMapping("/overlapIdCheck")
	//@GetMapping("/overlapIdCheck")
	public String overlapIdCheck(Model model, @RequestParam("id") String id) {		
		
		// 회원 아이디 중복 여부를 받아 온다.
		boolean overlap = memberService.overlapIdCheck(id);
		
		// model에 회원 ID와 회원 ID 중복 여부를 저장한다. 
		model.addAttribute("id", id);
		model.addAttribute("overlap", overlap);
		
		// 뷰 페이지만 먼저 작성해 새 창으로 잘 표시되는지 확인해 보자. 
		return "member/overlapIdCheck.html";
	}
	
	// 회원가입 폼에서 들어오는 회원가입 요청을 처리하는 메서드	
	@PostMapping("/joinResult")
	public String joinResult(Model model, Member member,
			@RequestParam("pass1") String pass1, 
			@RequestParam("emailId") String emailId, 
			@RequestParam("emailDomain") String emailDomain,
			@RequestParam("mobile1") String mobile1, 
			@RequestParam("mobile2") String mobile2, 
			@RequestParam("mobile3") String mobile3,
			@RequestParam("phone1") String phone1, 
			@RequestParam("phone2") String phone2, 
			@RequestParam("phone3") String phone3,
			@RequestParam(value="emailGet", required=false, 
				defaultValue="false")boolean emailGet) {		
		
		member.setPass(pass1);
		member.setEmail(emailId + "@" + emailDomain);
		member.setMobile(mobile1 + "-" + mobile2 + "-" + mobile3);
				
		if(phone2.equals("") || phone3.equals("")) {			
			member.setPhone("");				
		} else {			
			member.setPhone(phone1 + "-" + phone2 + "-" + phone3);
		}				
		member.setEmailGet(Boolean.valueOf(emailGet));

		// MemberService를 통해서 회원 가입 폼에서 들어온 데이터를 DB에 저장한다.
		memberService.addMember(member);
		log.info("joinResult : " + member.getName());
		
		// 로그인 폼으로 리다이렉트 시킨다.
		return "redirect:loginForm";
	}

	// 회원 정보 수정 폼 요청을 처리하는 메서드
	@GetMapping("/memberUpdateForm")
	public String updateForm(Model model, HttpSession session) {		
				
		/* 로그인 처리를 할 때 세션 영역에 회원 정보를 저장했기 때문에 뷰의 정보만 반환한다.
		 * 이렇게 별도의 처리가 필요 없을 경우 뷰 전용 컨트롤러를 사용하는 것도 좋다.
		 **/
		return "member/memberUpdateForm";
	}	
	
	// 회원 수정 폼에서 들어오는 요청을 처리하는 메서드
	@PostMapping("/memberUpdateResult")
	public String memberUpdateInfo(Model model, Member member,
			@RequestParam("pass1") String pass1, 
			@RequestParam("emailId") String emailId, 
			@RequestParam("emailDomain") String emailDomain,
			@RequestParam("mobile1") String mobile1, 
			@RequestParam("mobile2") String mobile2, 
			@RequestParam("mobile3") String mobile3,
			@RequestParam("phone1") String phone1, 
			@RequestParam("phone2") String phone2, 
			@RequestParam("phone3") String phone3,
			@RequestParam(value="emailGet", required=false, 
				defaultValue="false")boolean emailGet) {
		
		member.setPass(pass1);
		member.setEmail(emailId + "@" + emailDomain);
		member.setMobile(mobile1 + "-" + mobile2 + "-" + mobile3);
				
		if(phone2.equals("") || phone3.equals("")) {			
			member.setPhone("");				
		} else {			
			member.setPhone(phone1 + "-" + phone2 + "-" + phone3);
		}				
		member.setEmailGet(Boolean.valueOf(emailGet));	
			
		// MemberService를 통해서 회원 수정 폼에서 들어온 데이터를 DB에서 수정한다.
		memberService.updateMember(member);		
		log.info("memberUpdateResult : " + member.getId());
	
		/* 클래스 레벨에 @SessionAttributes({"member"}) 
		 * 애노테이션을 지정하고 컨트롤러의 메서드에서 아래와 같이 동일한 
		 * 이름으로 모델에 추가하면 스프링이 세션 영역에 데이터를 저장해 준다.
		 **/ 
		model.addAttribute("member", member);
		
		// 게시글 리스트로 리다이렉트 시킨다.
		return "redirect:boardList";
	}
}
