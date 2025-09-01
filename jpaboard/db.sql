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






-- A는 용, B,T는 아스콧, C는 송도, D는 이천,
-- C, D는 생성 후 보 보관 (당근체크, 당근 둘다 웨이팅이 필요)
-- A,B,T는 바로 판매 (당근체크만 웨이팅)

SELECT * from log_pass_finder_carrot order by no DESC;
SELECT * from log_pass_finder_carrot where location = 'B' or location = 'A' order by no ASC;
SELECT * from log_pass_finder_carrot where   location = 'T' order by no ASC;
SELECT * from log_pass_finder_carrot where   location = 'D' order by no ASC;

SELECT * FROM log_pass_finder_carrot WHERE location = 'T'  AND no >= (    SELECT no     FROM log_pass_finder_carrot     WHERE number = '01039691284'     AND location = 'T'    LIMIT 1  )ORDER BY no ASC;

SELECT * from log_pass_finder_carrot order by no ASC;
select * from member;
select * from las_tool_user;
INSERT INTO las_tool_user (service, user_id, name, coin, created_at, updated_at)
VALUES ('livescore', 'asct1_o#w2D^6dg', '라스아스', 0, NOW(), NOW());


-- log_create_telegram
-- server like '%192.168.200.111%'
-- 용인 1서버 = 192.168.200.111
-- 2 = 112
-- 3 = 113
SELECT * from log_create_telegram WHERE server like '%192.168.200.112%' and status like 'success%' order by no desc;
SELECT * FROM log_create_telegram
WHERE server LIKE '%192.168.200.112%'
  AND status LIKE 'success%'
  AND created_at >= '2025-08-27 00:00:00'  -- 시작 날짜
  AND created_at <= '2025-08-28 23:59:59'  -- 종료 날짜
ORDER BY no DESC;



select * from phone_status_table;
select * from get_number_pool;

SELECT *
FROM phone_status_table pst
JOIN uuid u ON pst.androidId = u.androidId
WHERE u.phname LIKE 'B%';

select * from uuid;

SELECT *
FROM phone_status_table pst
JOIN uuid u ON pst.androidId = u.androidId
WHERE u.phname LIKE 'A%'
  AND pst.status_telegram_check = 'sms_not_receive_3';
--
UPDATE phone_status_table pst
JOIN uuid u ON pst.androidId = u.androidId
SET pst.status_telegram = ''
WHERE u.phname LIKE 'A%'
  AND pst.status_telegram = 'waiting';


--
-- UPDATE phone_status_table pst
-- JOIN uuid u ON pst.androidId = u.androidId
-- SET pst.status_carrot = ''
-- WHERE u.phname LIKE 'D%';

-- 12, 18, 23, 37, 72, 77, 121, 151, 153

-- UPDATE phone_status_table pst
-- JOIN uuid u ON pst.androidId = u.androidId
-- SET pst.status_carrot = ''
-- WHERE u.phname REGEXP '^D-0(0[1-9]|[1-3][0-9]|4[0-4])$';



-- UPDATE phone_status_table pst
-- JOIN uuid u ON pst.androidId = u.androidId
-- SET pst.status_naver = 'waiting'
-- WHERE u.phname LIKE 'D%'
--   AND pst.status_naver = 'sms_not_receive_3';


-- ALTER TABLE phone_status_table
-- ADD COLUMN status_carrot_check VARCHAR(255)
-- AFTER status_carrot;

-- ALTER TABLE phone_status_table
-- CHANGE telegram_status_check status_telegram_check VARCHAR(255) DEFAULT NULL;


select * from maintable;