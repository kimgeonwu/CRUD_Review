## DATABASE 생성 및 선택

DROP DATABASE IF EXISTS springboot;
CREATE DATABASE IF NOT EXITS springboot;
use springboot;

-- 게시글 번호, 제목, 이메일, 내용, 글쓴이, 날짜, 조회수, 비밀번호, 파일정보,
-- no, title, email, content, writer, reg_date, read_count, pass, file
DROP TABLE IF EXISTS springbbs;
CREATE TABLE IF NOT EXITS springbbs(
	no INTEGER AUTO_INCREMENT PRIMARY KEY,
	title VARCHAR(50) NOT NULL,
	writer VARCHAR(20) NOT NULL,
	contentVARCHAR(1000) NOT NULL,
	reg_date TIMESTAMP NOT NULL,
	read_count INTEGER(5) NOT NULL,
	pass VARCHAR(20) NOT NULL,
	file1 VARCHAR(100)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;