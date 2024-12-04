package com.springbootstudy.bbs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.springbootstudy.bbs.domain.Member;
import com.springbootstudy.bbs.mapper.MemberMapper;

import lombok.extern.slf4j.Slf4j;

//MemberService 클래스가 서비스 계층의 스프링 빈(Bean) 임을 정의 
@Service
@Slf4j
public class MemberService {

	// 회원 관련 DB 작업에 필요한 MemberMapper 객체를 의존성 주입하도록 설정 
	@Autowired
	private MemberMapper memberMapper;
	
	/* 회원 로그인 처리에 필요한 PasswordEncoder 객체를 의존성 주입하도록 설정 
	 *  
	 * 회원 비밀번호 암호화 처리를 위해서 스프링 시큐리티의 PasswordEncoder를 사용
	 * 회원 로그인 요청시 DB 테이블에 암호화 되어 저장된 비밀번호와 사용자가
	 * 입력한 일반 문자열 비밀번호를 비교하는데 사용되고 회원 가입과 회원 정보 수정에서
	 * 사용자가 입력한 비밀번호를 암호화 인코딩하여 저장하는데도 사용된다.
	 * com.springbootstudy.bbs.configurations.SecurityConfig 클래스에서 스프링 빈을
	 * 생성해 반환하는 @Bean을 적용한 메서드를 정의했기 때문에 여기로 주입된다. 
	 **/
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	// 회원 로그인 요청을 처리하고 결과를 반환하는 메서드	
	public int login(String id, String pass) {
		int result = -1;
		Member m = memberMapper.getMember(id);
		
		// id가 존재하지 않으면 : -1
		if(m == null) {
			return result;
		}
		
		/* 로그인 성공 : 1
		 * PasswordEncoder 객체의 matches 메소드를 이용해 암호가 맞는지 확인
		 * matches() 메소드의 첫 번 인수로 인코딩이 않된 문자열, 두 번째 인수로 인코딩된 
		 * 문자열을 지정하면 두 문자열의 원본 데이터가 같을 경우 true를 반환해 준다.
		 **/		
		if(passwordEncoder.matches(pass, m.getPass())) {
			result = 1;
			
		} else { // 비밀번호가 틀리면 : 0
			result = 0;
		}
		
		return result;
	}
	
	// 회원 ID에 해당하는 회원 정보를 읽어와 반환하는 메서드	
	public Member getMember(String id) {
		return memberMapper.getMember(id);
	}
	
	// 회원 가입시 아이디 중복을 체크하는 메서드	
	public boolean overlapIdCheck(String id) {
		Member member = memberMapper.getMember(id);
		log.info("overlapIdCheck - member : " + member);
		if(member == null) {
			return false;
		}
		return true;
	}
	
	// 회원 정보를 MemberMapper를 이용해 회원 테이블에 저장하는 메서드	
	public void addMember(Member member) {
		
		// BCryptPasswordEncoder 객체를 이용해 비밀번호를 암호화한 후 저장
		member.setPass(passwordEncoder.encode(member.getPass()));
		
		/* 아래는 문자열 "1234"를 BCryptPasswordEncoder 객체를 사용해
		 * 암호화한 것으로 동일한 문자열을 암호화 하더라도 그 결과는 모두 다를 수 있다. 
		 **/ 
		// $2a$10$aWYm2BGI/0iMuemBeF4Y8.7WZeVKAoudv/VzgQx697lYlZgQxr/pe
		// $2a$10$b3t8sn6QZGHYaRx3OS5KUuPxzWZdY5yHPRxlSdAgByQ7v0BlCLzrO
		// $2a$10$.g6l.wyIFO1.j4u4gvVtKOnG9ACBUT1GRlDwlMZcjBxZPrCAURLaG
		// $2a$10$l37iiJWozST9.2EI11vjqOmSk9rus.5cawhTiPuQagCzVNTZcoFDa
		// $2a$10$qtxKvIgk.URhyqgx93KYFuw4e8Rh3IhIkR0CTZhd.HazLal7d3rqK
		log.info(member.getPass());
		memberMapper.addMember(member);
	}
	
	// 회원 정보 수정 시에 기존 비밀번호가 맞는지 체크하는 메서드
	public boolean memberPassCheck(String id, String pass) {		
		
		String dbPass = memberMapper.memberPassCheck(id);		
		boolean result = false;		

		/* 비밀번호가 맞으면 true를 반환하도록 작성한다.
		 * BCryptPasswordEncoder 객체의 matches 메소드를 이용해 암호가 맞는지 확인
		 * matches() 메소드의 첫 번 인수로 인코딩이 않된 문자열, 두 번째 인수로 인코딩된 
		 * 문자열을 지정하면 두 문자열의 원본 데이터가 같을 경우 true를 반환해 준다.
		 **/		
		if(passwordEncoder.matches(pass, dbPass)) {
			result = true;	
		}
		return result;	
	}
	
	// 회원 정보를 MemberMapper를 이용해 회원 테이블에서 수정하는 메서드
	public void updateMember(Member member) {
		
		// BCryptPasswordEncoder 객체를 이용해 비밀번호를 암호화한 후 저장
		member.setPass(passwordEncoder.encode(member.getPass()));
		log.info(member.getPass());
		
		memberMapper.updateMember(member);
	}	
}
