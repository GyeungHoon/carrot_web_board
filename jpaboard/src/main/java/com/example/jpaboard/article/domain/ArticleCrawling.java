package com.example.jpaboard.article.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "article_crawling")
public class ArticleCrawling {

    // Getter와 Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Long idx;
    
    @Column(name = "group_id", nullable = false, length = 36)
    private String groupId;
    
    @Column(name = "article_idx", nullable = false)
    private Long articleIdx;
    
    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "body")
    private String body;
    
    @Column(name = "category", length = 50)
    private String category;
    
    @Column(name = "author", length = 50)
    private String author;
    
    @Column(name = "virtual_address", nullable = false)
    private String virtualAddress;
    
    @Column(name = "place_address")
    private String placeAddress;
    
    @Column(name = "`like`", length = 10)
    private String like;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "views", length = 10)
    private String views;
    
    @Column(name = "main_image_urls", columnDefinition = "TEXT")
    private String mainImageUrls;
    
    @Column(name = "user_idx", nullable = false)
    private Long userIdx;
    
    @Column(name = "status", length = 200)
    private String status;
    
    // 기본 생성자
//    public ArticleCrawling() {}
    
    // 생성자
    public ArticleCrawling(String groupId, Long articleIdx, LocalDateTime regDate, String title, 
                          String body, String category, String author, String virtualAddress, 
                          String placeAddress, String like, String content, String views, 
                          String mainImageUrls, Long userIdx, String status) {
        this.groupId = groupId;
        this.articleIdx = articleIdx;
        this.regDate = regDate;
        this.title = title;
        this.body = body;
        this.category = category;
        this.author = author;
        this.virtualAddress = virtualAddress;
        this.placeAddress = placeAddress;
        this.like = like;
        this.content = content;
        this.views = views;
        this.mainImageUrls = mainImageUrls;
        this.userIdx = userIdx;
        this.status = status;
    }

}