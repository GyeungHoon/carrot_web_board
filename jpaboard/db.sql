# DB 생성
DROP DATABASE IF EXISTS MARKETING_BD;
CREATE DATABASE MARKETING_BD;
USE MARKETING_BD;



# 회원테이블 생성
CREATE TABLE `user` (
    `idx` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, -- 내부 식별용 인덱스
    user_id VARCHAR(50) NOT NULL UNIQUE,  -- 사용자 아이디 (로그인용 ID)
    reg_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    `password` VARCHAR(150) NOT NULL,
    `name` CHAR(50) NOT NULL,
    company VARCHAR(100) NOT NULL,
    balance BIGINT UNSIGNED NOT NULL DEFAULT 0,

    status ENUM('ACTIVE', 'INACTIVE', 'BANNED') NOT NULL DEFAULT 'ACTIVE',
    role_level TINYINT UNSIGNED NOT NULL DEFAULT 0 -- 0: 사용자, 10: 관리자
);



# 회원데이터 생성
INSERT INTO `user` (
	user_id,
    reg_date,
    update_date,
    `password`,
    `name`,
    company,
    role_level
) VALUES (
	'admin',
    NOW(),
    NOW(),
    'VMware1!',
    'IC',
    '에이치홀딩스',
    10
);



CREATE TABLE IF NOT EXISTS instances (
  idx INT AUTO_INCREMENT PRIMARY KEY,
  instance_index VARCHAR(100) NOT NULL,
  conferment VARCHAR(255) COMMENT '귀속',
  regional_certification VARCHAR(255) COMMENT '지역인증',
  temperature VARCHAR(255) COMMENT '온도',
  instance_name VARCHAR(255) NOT NULL,
  flow VARCHAR(50),
  status VARCHAR(50) DEFAULT 'available',
  standby_id VARCHAR(50) NULL,
  street_number VARCHAR(255) DEFAULT '',
  virtual_address VARCHAR(255) DEFAULT '',
  longitude VARCHAR(100) DEFAULT '',
  latitude VARCHAR(100) DEFAULT '',
  count int DEFAULT 0,
  aicount int DEFAULT 0,
  article_setup int DEFAULT 0,
  comment_setup int DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)



CREATE TABLE article (
    idx BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    group_id CHAR(36) NOT NULL COMMENT '묶음 식별자 (UUID)',
    reg_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    title VARCHAR(200) NOT NULL,
    body TEXT NOT NULL,
    main_image_urls TEXT COMMENT '다중 파일 선택',
    virtual_address VARCHAR(255) NOT NULL COMMENT '가상주소',
    longitude VARCHAR(255) COMMENT '경도',
    latitude VARCHAR(255) COMMENT '위도',
    place_zipcode VARCHAR(10) COMMENT '본문우편번호',
    place_address VARCHAR(255) COMMENT '본문장소주소',
    place_address_detail VARCHAR(255) COMMENT '상세주소',
    place_address_extra VARCHAR(255) COMMENT '참고항목',
    subject VARCHAR(100) NOT NULL COMMENT '주제 선택',
    article_url VARCHAR(255) COMMENT '게시글 주소',
    article_instance_name VARCHAR(255),
    user_idx BIGINT UNSIGNED NOT NULL,
    status VARCHAR(200)
);






CREATE TABLE comment (
    idx BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    group_id CHAR(36) NOT NULL COMMENT 'article과 같은 group_id',
    reg_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    comment_use_instance VARCHAR(255),
    comment_content TEXT COMMENT '댓글 내용',
    comment_zipcode VARCHAR(10),
    comment_address VARCHAR(255),
    comment_address_detail VARCHAR(255),
    comment_address_extra VARCHAR(255),
    image_url VARCHAR(500),
    comment_instance_name VARCHAR(255),

    user_idx BIGINT UNSIGNED NOT NULL,
    status VARCHAR(200)

);



CREATE TABLE reply (
    idx BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    group_id CHAR(36) NOT NULL COMMENT 'article과 같은 group_id',
    reg_date DATETIME NOT NULL,
    update_date DATETIME NOT NULL,
    reply_use_instance VARCHAR(255),
	comment_content TEXT COMMENT '대댓글 내용',
    reply_content TEXT COMMENT '대댓글 내용',
    user_idx BIGINT UNSIGNED NOT NULL,
    status VARCHAR(200)
    reply_instance_name VARCHAR(255),

);



CREATE TABLE article_crawling (
    idx BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    group_id CHAR(36) NOT NULL COMMENT 'article과 같은 group_id',
    article_idx BIGINT NOT NULL COMMENT 'article의 idx',
    reg_date DATETIME NOT NULL COMMENT '작성시간',
    update_date DATETIME NOT NULL COMMENT '업데이트시간',
    title VARCHAR(255) COMMENT '게시글 제목',
    body VARCHAR(255) COMMENT '게시글 본문',
    category VARCHAR(50) COMMENT '주제 subject',
    author VARCHAR(50) COMMENT '게시글 작성자',
    virtual_address VARCHAR(255) NOT NULL COMMENT '가상주소',
    place_address VARCHAR(255) COMMENT '본문장소주소',
    `like` VARCHAR(10) COMMENT '좋아요 수',
    content TEXT COMMENT '댓글 대댓글의 모든 정보 json형식',
    views VARCHAR(10) COMMENT '조회수',
    main_image_urls TEXT COMMENT 'artcle 이미지',
    user_idx BIGINT UNSIGNED NOT NULL COMMENT '게시글 작성자 idx',
    status VARCHAR(200) COMMENT '상태'

);


USE MARKETING_BD;

select * from user;
select * from article;
select * from comment;
select * from reply;

select * from article_crawling;
select * from instances;

