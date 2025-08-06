-- CREATE DATABASE jpaBoard;
-- CREATE USER 'jpauser'@'localhost' IDENTIFIED BY 'tiger';
-- GRANT ALL PRIVILEGES ON jpaBoard.* TO 'jpauser'@'localhost';
-- FLUSH PRIVILEGES;

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
INSERT INTO user (
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

INSERT INTO user (
	user_id,
    reg_date,
    update_date,
    `password`,
    `name`,
    company,
    balance
) VALUES (
	'user1',
    NOW(),
    NOW(),
    '1234',
    '홍길동',
    'ABC회사',
    0
);

INSERT INTO user (
	user_id,
    reg_date,
    update_date,
    `password`,
    `name`,
    company,
    balance
) VALUES (
	'user2',
    NOW(),
    NOW(),
    '1234',
    '김유신',
    '물류회사',
    0
);


INSERT INTO user (
	user_id,
    reg_date,
    update_date,
    `password`,
    `name`,
    company
) VALUES (
	'user3',
    NOW(),
    NOW(),
    '1234',
    '이무개',
    '삼성전자'
);
# 게시물 테이블 생성
CREATE TABLE `article` (
    `idx` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, -- 게시글 고유번호
    `reg_date` DATETIME NOT NULL,
    `update_date` DATETIME NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `body` TEXT NOT NULL,


    main_image_urls TEXT COMMENT '다중 파일 선택',
    virtual_address VARCHAR(255) NOT NULL COMMENT '가상주소',


    place_zipcode VARCHAR(10) COMMENT '본문우편번호',
    place_address VARCHAR(255) COMMENT '본문장소주소',
    place_address_detail VARCHAR(255) COMMENT '상세주소',
    place_address_extra VARCHAR(255) COMMENT '참고항목',

    subject VARCHAR(100) NOT NULL COMMENT '주제 선택',

    comment_content TEXT COMMENT '댓글 내용',
    comment_zipcode VARCHAR(10) COMMENT '댓글우편번호',
    comment_address VARCHAR(255) COMMENT '댓글장소주소',
	comment_address_detail VARCHAR(255) COMMENT '상세주소',
    comment_address_extra VARCHAR(255) COMMENT '참고항목',

    image_url VARCHAR(500) COMMENT '단일 파일 선택',
    reply_content TEXT COMMENT '대댓글 내용',

    longitude VARCHAR(255) COMMENT '경도',
    latitude VARCHAR(255) COMMENT '위도',




	`user_idx` BIGINT UNSIGNED NOT NULL,
	`status` VARCHAR(200)
);



-- INSERT INTO `article` (
--     `reg_date`, `update_date`, `title`, `body`,
--     `main_image_urls`, `zipcode`, `address`, `address_detail`, `address_extra`,
--     `subject`, `comment_content`, `location_address`,
--     `image_url`, `reply_content`, `user_idx`
-- ) VALUES (
--     NOW(), NOW(),
--     '테스트 제목',
--     '이것은 테스트 본문입니다.',
--     '[]',
--     '12345',
--     '서울특별시 강남구 테헤란로 123',
--     '201호',
--     '강남빌딩',
--     '기술',
--     '댓글 내용 예시입니다.',
--     '서울특별시 중구 을지로 45',
--     'single_image.jpg',
--     '대댓글 예시입니다.',
--     2
-- );





select * from user;
select * from article;



