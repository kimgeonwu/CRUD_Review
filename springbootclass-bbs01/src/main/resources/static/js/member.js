// DOM이 준비되면 실행될 콜백 함수 
$(function() {
	
	// 회원 로그인 폼이 submit 될 때 폼 유효성 검사를 위한 이벤트 처리
	$("loginForm").submit(function() {
		var id = $("#userId").val();
		var pass = $("#userPass").val();
		
		if(id.lentgh <= 0) {
			alert("아이디가 입력되지 않았습니다\n아이디를 입력해주세요");
			$("#userId").focus();
			return false;
		}
		if(pass.lentgh <= 0) {
			alert("비밀번호가 입력되지 않았습니다\n비밀번호를 입력해주세요");
			$("#userPass").focus();
			return false;
		}
	});
	
	// 모달 로그인 폼이 submit 될 때 폼 유효성 검사를 위한 이벤트 처리
	$("#modalLoginForm").submit(function() {
		var id = $("#modalUserId").val();
		var pass = $("#modalUserPass").val();
		
		if(id.lentgh <= 0) {
			alert("아이디가 입력되지 않았습니다\n아이디를 입력해주세요");
			$("#modalUserId").focus();
			return false;
		}
		if(pass.lentgh <= 0) {
			alert("비밀번호가 입력되지 않았습니다\n비밀번호를 입력해주세요");
			$("#modalUserPass").focus();
			return false;
		}
	});
});