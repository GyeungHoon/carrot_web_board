
package com.example.jpaboard.article.dao;

import com.example.jpaboard.article.domain.Article;
import com.example.jpaboard.article.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findByUserIdx(Long userIdx);
    List<Reply> findByGroupId(String groupId);
}
