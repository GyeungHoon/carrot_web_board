
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
public class Instances {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private Long instance_index;
    private String conferment;
    private String instance_name;
    private String flow;
    private String status;
    private String standby_id;
    private String street_number;
    private String virtual_address;
    private String longitude;
    private String latitude;
    private Long count;
    private Long aicount;
    private Long article_setup;
    private Long comment_setup;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;

 }
