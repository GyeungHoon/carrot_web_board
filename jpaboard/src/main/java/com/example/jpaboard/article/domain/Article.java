
package com.example.jpaboard.article.domain;


import com.example.jpaboard.user.domain.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(name = "group_id", nullable = false, length = 36)
    private String groupId; // ✅ 이 부분 추가

    private LocalDateTime regDate;
    private LocalDateTime updateDate;
    private String status;
    private String title;
    private String body;
    private String mainImageUrls;
    private String virtualAddress;
    private String longitude;
    private String latitude;
    private String placeZipcode;
    private String placeAddress;
    private String placeAddressDetail;
    private String placeAddressExtra;
    private String subject;
    private String articleUrl;
    private String articleInstanceName;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")  // 이 부분 확인
    private User user;

}





//@Entity
//@Getter
//@Setter
//@ToString
//public class Article {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long idx;
//    private LocalDateTime regDate;
//    private LocalDateTime updateDate;
//    private String title;
//    private String body;
//    private String mainImageUrls;
//    private String virtualAddress;
//    private String longitude;
//    private String latitude;
//    private String placeZipcode;
//    private String placeAddress;
//    private String placeAddressDetail;
//    private String placeAddressExtra;
//    private String subject;
//
//
//    private String commentContent;
//    private String commentZipcode;
//    private String commentAddress;
//    private String commentAddressDetail;
//    private String commentAddressExtra;
//    private String imageUrl;
//
//    private String replyContent;
//
//
//
//    @ManyToOne
//    private User user;
//
// }
