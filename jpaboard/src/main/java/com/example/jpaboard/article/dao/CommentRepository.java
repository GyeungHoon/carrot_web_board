
package com.example.jpaboard.article.dao;

import com.example.jpaboard.article.domain.Article;
import com.example.jpaboard.article.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByUserIdx(Long userIdx);
    List<Comment> findByGroupId(String groupId);
}
