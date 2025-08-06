
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
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(name = "group_id", nullable = false, length = 36)
    private String groupId; // ✅ 이 부분 추가

    private LocalDateTime regDate;
    private LocalDateTime updateDate;
    private String status;
    private String replyContent;
    private String replyInstanceName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")  // 이 부분 확인
    private User user;

 }
