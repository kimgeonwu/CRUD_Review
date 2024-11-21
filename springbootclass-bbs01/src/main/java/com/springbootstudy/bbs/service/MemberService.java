package com.springbootstudy.bbs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.springbootstudy.bbs.domain.Member;
import com.springbootstudy.bbs.mapper.MemberMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MemberService {

	// 회원 관련 DB 작업에 필요한 MemberMapper 객체를 의존성 주입하도록 설정
	@Autowired
	private MemberMapper memberMapper;
	
	// 회원 로그인 처리에 필요한 PasswordEncoder 객체를 의존성 주입하도록 설정
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
		// 로그인 성공 : 1
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
}
