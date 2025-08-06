
package com.example.jpaboard.article.dao;

import com.example.jpaboard.article.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByUserIdx(Long userIdx);
}
