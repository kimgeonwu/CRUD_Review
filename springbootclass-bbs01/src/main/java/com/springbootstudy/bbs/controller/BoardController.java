package com.springbootstudy.bbs.controller;

import java.io.PrintWriter;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springbootstudy.bbs.domain.Board;
import com.springbootstudy.bbs.service.BoardService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class BoardController {

	@Autowired
	private BoardService boardService;

	// 게시글 삭제 요청을 처리 메서드
	@PostMapping("/delete")
	public String deleteBoard(RedirectAttributes reAttrs, HttpServletResponse response, PrintWriter out,
			@RequestParam("no") int no, @RequestParam("pass") String pass,
			@RequestParam(value = "pageNum", defaultValue = "1") int pageNum) {

		// 사용자가 입력한 비밀번호가 틀리면 자바스크립트로 응답
		boolean isPassCheck = boardService.isPassCheck(no, pass);
		if (!isPassCheck) {
			response.setContentType("text/html; charset=utf-8");
			out.println("<script>");
			out.println(" 	alert('비밀번호가 맞지 않습니다.');");
			out.println(" 	history.back();");
			out.println("</script>");

			// null을 반환하면 위에서 스트림에 출력한 자바스크립트 코드가 응답
			return null;
		}
		// true면 DB 테이블에서 no에 해당하는 게시글 정보를 수정 후 리다이렉트
		boardService.deleteBoard(no);
		
		// RedirectAttributes를 이용해 리다이렉트 할 때 필요한 파라미터를 지정
		reAttrs.addAttribute("pageNum", pageNum);
		
		// 게시글 삭제가 완료되면 게시글 리스트로 리다이렉트
		return "redirect:boardList";
	}

	// 게시글 수정 폼에서 들어오는 게시글 수정 요청을 처리하는 메서드
	@PostMapping("/update")
	public String updateBoard(Board board, RedirectAttributes reAttrs,
			@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, HttpServletResponse response,
			PrintWriter out) {

		// 사용자가 입력한 비밀번호가 틀리면 자바스크립트로 응답
		boolean isPassCheck = boardService.isPassCheck(board.getNo(), board.getPass());
		if (!isPassCheck) {
			response.setContentType("text/html; charset=utf-8");
			out.println("<script>");
			out.println(" 	alert('비밀번호가 맞지 않습니다.');");
			out.println(" 	history.back();");
			out.println("</script>");

			// null을 반환하면 위에서 스트림에 출력한 자바스크립트 코드가 응답
			return null;
		}

		// true면 DB 테이블에서 no에 해당하는 게시글 정보를 수정 후 리다이렉트
		boardService.updateBoard(board);
		reAttrs.addAttribute("pageNum", pageNum);
		reAttrs.addFlashAttribute("test1", "1회성 파라미터");
		return "redirect:boardList";
	}

	// 게시글 수정 폼 요청을 처리하는 메서드
	@PostMapping("/updateForm")
	public String updateBoard(Model model, HttpServletResponse response, PrintWriter out, @RequestParam("no") int no,
			@RequestParam("pass") String pass, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum) {

		// 사용자가 입력한 비밀번호가 틀리면 자바스크립트로 응답
		boolean isPassCheck = boardService.isPassCheck(no, pass);
		if (!isPassCheck) {
			response.setContentType("text/html; charset=utf-8");
			out.println("<script>");
			out.println(" 	alert('비밀번호가 맞지 않습니다.');");
			out.println(" 	history.back();");
			out.println("</script>");

			// null을 반환하면 위에서 스트림에 출력한 자바스크립트 코드가 응답
			return null;
		}

		// 비밀번호가 맞으면 no에 해당하는 게시글 정보를 모델에 담아 수정 폼으로 이동
		Board board = boardService.getBoard(no, false);
		model.addAttribute("board", board);
		model.addAttribute("pageNum", pageNum);
		return "views/updateForm";
	}

	// 게시글 쓰기 폼에서 들어오는 게시글 쓰기 요청을 처리하는 메서드
	@PostMapping("/addBoard")
	public String addBoard(Board board) {
		boardService.addBoard(board);
		return "redirect:boardList";
	}

	// 게시글 쓰기 폼 요청 처리 메서드
	@GetMapping("/addBoard")
	public String addBoard() {
		return "views/writeForm";
	}

	@GetMapping("/boardDetail")
	public String getBoard(Model model, @RequestParam("no") int no,
			@RequestParam(value = "pageNum", defaultValue = "1") int pageNum) {

		/*
		 * 게시글 상세보기는 게시글 조회에 해당하므로 no에 해당하는 게시글 정보를 읽어오면서 두 번째 인수에 true를 지정해 게시글 읽은 횟수를
		 * 1 증가시킴
		 **/
		Board board = boardService.getBoard(no, true);

		// no에 해당하는 게시글 정보와 pageNum을 모델에 저장
		model.addAttribute("board", board);
		model.addAttribute("pageNum", pageNum);

		return "views/boardDetail";
	}

	// 게시글 리스트 요청을 처리하는 메서드
	@GetMapping({ "/", "/boardList" })
	public String boardList(Model model,
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum) {

		// Service 클래스를 이용해 게시글 리스트를 가져온다.
		Map<String, Object> modelMap = boardService.boardList(pageNum);

		// 파라미터로 받은 모델 객체에 뷰로 보낼 모델을 저장
		model.addAllAttributes(modelMap);

		// 페이지 모듈화로 content 페이지가 "/templates/views" 폴더로 이동
		return "views/boardList";
	}
}
