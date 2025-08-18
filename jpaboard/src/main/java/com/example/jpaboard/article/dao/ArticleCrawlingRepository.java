package com.example.jpaboard.article.dao;

import com.example.jpaboard.article.domain.ArticleCrawling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleCrawlingRepository extends JpaRepository<ArticleCrawling, Long> {
    
    /**
     * article_idx로 크롤링 데이터 조회
     */
    Optional<ArticleCrawling> findByArticleIdx(Long articleIdx);
    
    /**
     * group_id로 크롤링 데이터 목록 조회
     */
    List<ArticleCrawling> findByGroupId(String groupId);
    
    /**
     * article_idx로 크롤링 데이터 존재 여부 확인
     */
    boolean existsByArticleIdx(Long articleIdx);
} 