package com.springbootstudy.bbs.domain;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 한 명의 회원 정보를 저장하는 DTO(Data Transfer Object) 클래스
// 회원 정보를 저장하고 있는 테이블의 컬럼과 1:1 맵핑되는 클래스
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {
	
	private String id, name, pass, email, mobile;
	private String zipcode, address1, address2, phone;
	private boolean eamilGet;
	private Timestamp regDate;

}
