
package com.example.jpaboard.user.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
    private String userId;
    @JsonIgnore
    private String password;
    private String name;
    private String company;
    private Long balance;
    private short roleLevel;

    public User() {
        if (balance == null) {
            balance = 0L;  // 기본값 설정
        }
    }

}
