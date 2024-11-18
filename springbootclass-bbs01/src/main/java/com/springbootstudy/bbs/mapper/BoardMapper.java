package com.springbootstudy.bbs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.springbootstudy.bbs.domain.Board;

@Mapper
public interface BoardMapper {
	
	// no에 해당하는 게시글의 읽은 횟수를 DB 테이블에서 증가시키는 메서드
	public void incrementReadCount(int no);
	
	// DB 테이블에 등록된 전체 게시글 수를 읽어와 반환하는 메서드
	public int getBoardCount();
	
	// 한 페이지에 해당하는 게시글 리스트를 DB 테이블에서 읽어와 반환하는 메서드
	public List<Board> boardList(@Param("startRow") int startRow, @Param("num") int num);
	
	// no에 해당 하는 게시글을 DB 테이블에서 삭제하는 메서드
	public void deleteBoard(int no);
	
	// 수정된 게시글을 Board 객체로 받아서 DB 테이블에서 수정하는 메서드
	public void updateBoard(Board board);
	
	// no에 해당하는 비밀번호를 DB 테이블에서 읽어와 반환하는 메서드
	public String isPassCheck(int no);
	
	// 게시글을 Board 객체로 받아서 DB 테이블에 추가하는 메서드
	public void insertBoard(Board board);
	
	// DB 테이블에서 NO에 해당하는 게시글을 읽어와 Board 객체로 반환하는 메서드
	public Board getBoard(int no);
	
}
