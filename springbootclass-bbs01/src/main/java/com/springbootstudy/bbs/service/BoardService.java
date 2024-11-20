package com.springbootstudy.bbs.service;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.springbootstudy.bbs.domain.Board;
import com.springbootstudy.bbs.mapper.BoardMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

//BoardService 클래스가 서비스 계층의 스프링 빈(Bean) 임을 정의
@Service
@Slf4j
public class BoardService {

	// DB 작업에 필요한 BoardMapper 객체를 의존성 주입 설정
	@Autowired
	private BoardMapper boardMapper;

	// 한 페이지에 출력할 게시글의 수를 상수로 선언
	private static final int PAGE_SIZE = 10;
	// 한 페이지에 출력할 페이지 그룹의 수를 상수로 선언
	private static final int PAGE_GROUP = 10;
	
	// no에 해당하는 게시글을 읽어와 반환하는 메서드
	// isCount == true : 게시 상세보기 요청, false : 그 외 요청
	public Board getBoard(int no, boolean isCount) {
		
		// 게시글 상세보기 요청만 게시글 읽은 횟수를 증가
		if(isCount) {
			boardMapper.incrementReadCount(no);
		}
		return boardMapper.getBoard(no);
	}

	// no에 해당하는 게시글을 삭제하는 메서드
	public void deleteBoard(int no) {
		boardMapper.deleteBoard(no);
	}

	// 게시글을 수정하는 메서드
	public void updateBoard(Board board) {
		boardMapper.updateBoard(board);
	}

	// 게시글 수정과 삭제 할 때 비밀번호가 맞는지 체크하는 메서드
	public boolean isPassCheck(int no, String pass) {
		boolean result = false;

		// BoardDao를 이용해 DB에서 NO에 해당하는 비밀번호를 읽어옴
		String dbPass = boardMapper.isPassCheck(no);
		if (dbPass.equals(pass)) {
			result = true;
		}
		// 비밀번호가 맞으면 true, 맞지 않으면 false가 반환
		return result;
	}

	// 게시글 정보를 추가하는 메서드
	public void addBoard(Board board) {
		boardMapper.insertBoard(board);
	}

	// 한 페이지에 출력할 게시글 리스트 또는 검색 리스트와
	// 페이징 처리에 필요한 데이터를 Map 객체로 반환하는 메서드
	public Map<String, Object> boardList(int pageNum, String type, String keyword) {

		// 요청 파라미터의 pageNum을 현재 페이지로 설정
		int currentPage = pageNum;

		// 현재 페이지에 해당하는 게시글 리스트의 첫 번째 행의 값을 계산
		int startRow = (currentPage - 1) * PAGE_SIZE;
		
		/* type과 keyword라는 두 개의 요청 파라미터 값이 "null"인지 확인하고,
		 * 이를 기반으로 searchOption이라는 불리언 값을 설정
		 * 검색 옵션이 있는지 여부를 판단하기 위해 사용 **/
		boolean searchOption = (type.equals("null") || keyword.equals("null")) ? false : true;

		// BoardMapper를 이용해 전체 게시글 수를 가져옴
		int listCount = boardMapper.getBoardCount(type, keyword);

		// 현재 페이지에 해당하는 게시글 리스트를 BoardMapper를 이용해 DB에서 읽어옴
		List<Board> boardList = boardMapper.boardList(startRow, PAGE_SIZE, type, keyword);

		// 페이지 그룹 이동 처리를 위해 전체 페이지 수를 계산
		int pageCount = listCount / PAGE_SIZE + (listCount % PAGE_SIZE == 0 ? 0 : 1);

		// 페이지 그룹 처리를 위해 페이지 그룹별 시작 페이지와 마지막 페이지를 계산
		int startPage = (currentPage / PAGE_GROUP) * PAGE_GROUP + 1
				- (currentPage % PAGE_GROUP == 0 ? PAGE_GROUP : 0);

		// 현재 페이지 그룹의 마지막 페이지
		int endPage = startPage + PAGE_GROUP - 1;

		// 현재 페이지 그룹이 마지막 페이지 그룹이면 endPage는 전체 페이지 수가 되도록 지정
		if (endPage > pageCount) {
			endPage = pageCount;
		}

		// View 페이지에서 필요한 데이터를 Map에 저장
		Map<String, Object> modelMap = new HashMap<String, Object>();
		
		modelMap.put("bList", boardList);
		modelMap.put("pageCount", pageCount);
		modelMap.put("startPage", startPage);
		modelMap.put("endPage", endPage);
		modelMap.put("currentPage", currentPage);
		modelMap.put("listCount", listCount);
		modelMap.put("pageGroup", PAGE_GROUP);
		modelMap.put("searchOption", searchOption);
		
		// 검색 요청이면 type과 keyword를 모델에 저장
		if(searchOption) {
			modelMap.put("type", type);
			modelMap.put("keyword", keyword);
		}	
		return modelMap;
	}

	// no에 해당하는 게시글을 읽어와 반환하는 메서드
	public Board getBoard(int no) {
		return boardMapper.getBoard(no);
	}

}
