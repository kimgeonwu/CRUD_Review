package com.springbootstudy.bbs.ajax;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springbootstudy.bbs.service.MemberService;

// @RestController 애노테이션은 @Controller에 @ResponseBody가 추가된 것과 동일
//  RestController의 주용도는 JSON으로 응답하는 것이다.
@RestController
public class AjaxProcessController {
	
	@Autowired
	private MemberService memberService;
	
	// 비밀번호 확인 요청 처리 메서드
	@GetMapping("/passCheck.ajax")
	public Map<String, Boolean> memberPassCheck(
			@RequestParam("id") String id, @RequestParam("pass") String pass) {
		
		/* MemberService를 사용해 요청 파라미터로 받은 id와 pass를 입력해
		* 비밀번호가 일치하는지 여부를 받아와 Map 객체에 담아서 반환하면
		* 이 객체를 JSON 형식으로 변환해 응답 본문에 추가해 준다.
		**/
		boolean result = memberService.memberPassCheck(id, pass);
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		map.put("result", result);
		/* MappingJackson2HttpMessageConverter에 의해서
		* Map 객체가 아래와 같이 json 형식으로 변환된다.
		* {"result": true} 또는 {"result": false}
		**/
		return map;
	}	
}
