package com.springbootstudy.bbs.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springbootstudy.bbs.domain.Board;
import com.springbootstudy.bbs.service.BoardService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class BoardController {

	@Autowired
	private BoardService boardService;

	// 업로드한 파일을 저장할 폴더 위치를 상수로 선언
	private static final String DEFAULT_PATH = "src/main/resources/static/files/";
	
	// 게시글 상세보기에서 들어오는 파일 다운로드 요청을 처리하는 메서드
	public void download(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		String fileName = req.getParameter("fileName");
		File parent = new File(DEFAULT_PATH);
		File file = new File(parent.getAbsolutePath(), fileName);
		
		// 응답 데이터에 파일 다운로드 관련 컨텐츠 타입 설정 필요
		resp.setContentType("application/download; charset=utf-8");
		resp.setContentLength((int) file.length());
		
		// 한글 파일명을 클라이언트로 바로 내려보내기 때문에 URLEncoding이 필요
		fileName = URLEncoder.encode(file.getName(), "UTF-8");
		
		// 전송되는 파일 이름을 한글 그대(원본 파일 이름 그대로)로 보내주기 위한 설정
		resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\";");
		
		// 파일로 전송되야 하므로 전송되는 데이터 인코딩은 바이너리로 설정
		resp.setHeader("Content-Transfer-Encoding","binary");
		
		// 파일을 클라이언트로 보내기 위해 응답 스트림을 구함
		OutputStream out = resp.getOutputStream();
		FileInputStream fis = null;
		
		// 클라이언트로 보낼 파일을 읽고 응답 스트림을 통해 클라이언트로 출력
		fis = new FileInputStream(file);
		
		// 응답 스트림에 파일을 복사
		FileCopyUtils.copy(fis, out);
		
		if(fis != null) {
			fis.close();
		}
		
		// 파일 데이터를 클라이언트로 출력
		out.flush();
	}

	// 게시글 삭제 요청을 처리 메서드
	@PostMapping("/delete")
	public String deleteBoard(RedirectAttributes reAttrs, HttpServletResponse response, PrintWriter out,
			@RequestParam("no") int no, @RequestParam("pass") String pass,
			@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
			@RequestParam(value = "type", defaultValue = "null") String type,
			@RequestParam(value = "keyword", defaultValue = "null") String keyword) {

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

		// 현재 요청이 검색 요청인지 여부를 판단하는 searchOption 설정
		boolean searchOption = (type.equals("null") || keyword.equals("null")) ? false : true;

		// RedirectAttributes를 이용해 리다이렉트 할 때 필요한 파라미터를 지정
		reAttrs.addAttribute("pageNum", pageNum);
		reAttrs.addAttribute("searchOption", searchOption);

		// 검색 요청이면 type과 keyword를 모델에 저장한다.
		if (searchOption) {
			reAttrs.addAttribute("type", type);
			reAttrs.addAttribute("keyword", keyword);
		}

		// 게시글 삭제가 완료되면 게시글 리스트로 리다이렉트
		return "redirect:boardList";
	}

	// 게시글 수정 폼에서 들어오는 게시글 수정 요청을 처리하는 메서드
	@PostMapping("/update")
	public String updateBoard(Board board, RedirectAttributes reAttrs, HttpServletResponse response, PrintWriter out,
			@RequestParam("no") int no, @RequestParam("pass") String pass,
			@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
			@RequestParam(value = "type", defaultValue = "null") String type,
			@RequestParam(value = "keyword", defaultValue = "null") String keyword) {

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

		// 비밀번호가 맞으면 DB 테이블에서 no에 해당하는 게시글 정보를 수정
		boardService.updateBoard(board);

		// 현재 요청이 검색 요청인지 여부를 판단하는 searchOption 설정
		boolean searchOption = (type.equals("null") || keyword.equals("null")) ? false : true;

		// RedirectAttributs의 addAttribute() 메서드를 사용해 파라미터 설정
		reAttrs.addAttribute("searchOption", searchOption);
		reAttrs.addFlashAttribute("pageNum", pageNum);

		// 검색 요청이면 type과 keyword를 모델에 저장한다.
		if (searchOption) {
			reAttrs.addAttribute("type", type);
			reAttrs.addAttribute("keyword", keyword);
		}

		// 게시글 수정이 완료되면 게시글 리스트로 리다이렉트 시킨다.
		return "redirect:boardList";
	}

	// 게시글 수정 폼 요청을 처리하는 메서드
	@PostMapping("/updateForm")
	public String updateBoard(Model model, HttpServletResponse response, PrintWriter out, @RequestParam("no") int no,
			@RequestParam("pass") String pass, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
			@RequestParam(value = "type", defaultValue = "null") String type,
			@RequestParam(value = "keyword", defaultValue = "null") String keyword) {

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

		// 게시글 수정 폼 요청은 읽은 횟수를 증가시키지 않음
		Board board = boardService.getBoard(no, false);

		// 현재 요청이 검색 요청인지 여부를 판단하는 searchOption 설정
		boolean searchOption = (type.equals("null") || keyword.equals("null")) ? false : true;

		model.addAttribute("board", board);
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("searchOption", searchOption);

		// 검색 요청이면 type과 keyword를 모델에 저장한다.
		if (searchOption) {
			model.addAttribute("type", type);
			model.addAttribute("keyword", keyword);
		}

		return "views/updateForm";
	}

	// 게시글 쓰기 폼에서 들어오는 게시글 쓰기 요청을 처리하는 메서드
	@PostMapping("/addBoard")
	public String addBoard(Board board, @RequestParam(value = "addFile", required = false) MultipartFile multipartFile)
			throws IOException {

		// 업로된 파일이 있으면
		if (multipartFile != null && !multipartFile.isEmpty()) {

			// File 클래스는 파일과 디렉터리를 다루기 위한 클래스
			File parent = new File(DEFAULT_PATH);

			// 파일 업로드 위치에 폴더가 존재하지 않으면 폴더 생성
			if (!parent.isDirectory() && !parent.exists()) {
				parent.mkdirs();
			}

			// 중복되지 않는 ID를 생성하기 위해 사용
			UUID uid = UUID.randomUUID();

			// 파일 이름에서 확장자 분리
			String extension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());

			// 앞에서 생성한 UUID를 문자열로 변환해서 저장
			String saveName = uid.toString() + "." + extension;

			// 저장할 경로의 부모 디렉토리의 절대 경로 설정
			File file = new File(parent.getAbsolutePath(), saveName);

			// 업로드 되는 파일을 static/files 폴더에 복사한다.
			multipartFile.transferTo(file);
			
			// 업로드된 파일 이름을 게시글의 첨부 파일로 설정한다.
			board.setFile1(saveName);			
		} else {
			
			// 파일이 업로드 되지 않으면 콘솔에 로그 출력
			log.info("No file uploaded - 파일이 업로드 되지 않음");
		}
		boardService.addBoard(board);
		
		// 게시글 쓰기가 완료되면 게시글 리스트로 리다이렉트
		return "redirect:boardList";
	}

	// 게시글 쓰기 폼 요청 처리 메서드
	@GetMapping("/addBoard")
	public String addBoard() {
		return "views/writeForm";
	}

	// 게시글 상세보기 요청 처리 메서드
	@GetMapping("/boardDetail")
	public String getBoard(Model model, @RequestParam("no") int no,
			@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
			@RequestParam(value = "type", defaultValue = "null") String type,
			@RequestParam(value = "keyword", defaultValue = "null") String keyword) {

		/*
		 * type과 keyword라는 두 개의 요청 파라미터 값이 "null"인지 확인하고, 이를 기반으로 searchOption이라는 불리언 값을
		 * 설정 검색 옵션이 있는지 여부를 판단하기 위해 사용
		 **/
		boolean searchOption = (type.equals("null") || keyword.equals("null")) ? false : true;

		/*
		 * 게시글 상세보기는 게시글 조회에 해당하므로 no에 해당하는 게시글 정보를 읽어오면서 두 번째 인수에 true를 지정해 게시글 읽은 횟수를
		 * 1 증가시킴
		 **/
		Board board = boardService.getBoard(no, true);

		// no에 해당하는 게시글 정보와 pageNum을 모델에 저장
		model.addAttribute("board", board);
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("searchOption", searchOption);

		// 검색 요청이면 type과 keyword를 모델에 저장
		if (searchOption) {
			model.addAttribute("type", type);
			model.addAttribute("keyword", keyword);
		}
		return "views/boardDetail";
	}

	// 게시글 리스트 요청을 처리하는 메서드
	@GetMapping({ "/", "/boardList" })
	public String boardList(Model model,
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
			@RequestParam(value = "type", required = false, defaultValue = "null") String type,
			@RequestParam(value = "keyword", required = false, defaultValue = "null") String keyword) {

		// Service 클래스를 이용해 게시글 리스트를 가져온다.
		Map<String, Object> modelMap = boardService.boardList(pageNum, type, keyword);

		// 파라미터로 받은 모델 객체에 뷰로 보낼 모델을 저장
		model.addAllAttributes(modelMap);

		// 페이지 모듈화로 content 페이지가 "/templates/views" 폴더로 이동
		return "views/boardList";
	}
}
