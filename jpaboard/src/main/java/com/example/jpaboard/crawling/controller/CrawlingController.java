package com.example.jpaboard.crawling.controller;

import com.example.jpaboard.crawling.service.CrawlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CrawlingController {

    @Autowired
    private CrawlingService crawlingService;

    /**
     * 게시글 크롤링 API
     */
    @PostMapping("/crawl")
    public ResponseEntity<Map<String, Object>> crawlArticle(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long articleIdx = Long.valueOf(request.get("articleIdx").toString());
            String url = request.get("url").toString();
            
            // 크롤링 실행
            Map<String, String> crawledData = crawlingService.crawlWebPage(url);
            
            if ("success".equals(crawledData.get("status"))) {
                // DB에 저장
                boolean saveResult = crawlingService.saveCrawledDataToDatabase(articleIdx, crawledData);
                
                if (saveResult) {
                    response.put("success", true);
                    response.put("message", "크롤링이 성공적으로 완료되었습니다.");
                    response.put("data", crawledData);
                } else {
                    response.put("success", false);
                    response.put("message", "데이터 저장에 실패했습니다.");
                }
            } else {
                response.put("success", false);
                response.put("message", "크롤링에 실패했습니다: " + crawledData.get("error"));
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "오류가 발생했습니다: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 크롤링 서비스 상태 확인 API
     */
    @GetMapping("/crawl/status")
    public ResponseEntity<Map<String, Object>> getCrawlStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "running");
        response.put("message", "크롤링 서비스가 정상적으로 실행 중입니다.");
        return ResponseEntity.ok(response);
    }
} 