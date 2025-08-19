package com.example.jpaboard.crawling.service;

import com.example.jpaboard.article.dao.ArticleRepository;
import com.example.jpaboard.article.dao.ArticleCrawlingRepository;
import com.example.jpaboard.article.domain.Article;
import com.example.jpaboard.article.domain.ArticleCrawling;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class CrawlingService {

    @Autowired
    private ArticleRepository articleRepository;
    
    @Autowired
    private ArticleCrawlingRepository articleCrawlingRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 웹페이지를 크롤링하여 기본 정보를 추출합니다.
     * @param url 크롤링할 URL
     * @return 크롤링된 데이터
     */
    public Map<String, String> crawlWebPage(String url) {
        Map<String, String> crawledData = new HashMap<>();
        
        try {
            // Jsoup을 사용하여 웹페이지 크롤링
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get();
            
            // 당근마켓 특화 크롤링
            if (url.contains("daangn.com")) {
                return crawlDaangnPage(doc, url);
            }
            
            // 일반적인 웹페이지 크롤링
            return crawlGeneralPage(doc, url);
            
        } catch (IOException e) {
            crawledData.put("status", "error");
            crawledData.put("error", "크롤링 중 오류 발생: " + e.getMessage());
        } catch (Exception e) {
            crawledData.put("status", "error");
            crawledData.put("error", "예상치 못한 오류: " + e.getMessage());
        }
        
        return crawledData;
    }
    
    /**
     * 당근마켓 페이지를 크롤링합니다.
     */
    private Map<String, String> crawlDaangnPage(Document doc, String url) {
        Map<String, String> crawledData = new HashMap<>();
        
        try {
            // 제목 추출 (h1 태그)
            Element h1Element = doc.selectFirst("h1");
            if (h1Element != null) {
                crawledData.put("title", h1Element.text().trim());
            }
            
            // 본문 내용 추출 (p.n4kioi2 태그)
            Element contentElement = doc.selectFirst("p.n4kioi2");
            if (contentElement != null) {
                crawledData.put("content", contentElement.text().trim());
            }
            
            // 카테고리 추출 (운동, 중고거래 등) - span.mlbp660 안의 a 태그
            Element categoryElement = doc.selectFirst("span.mlbp660 a");
            if (categoryElement != null) {
                crawledData.put("category", categoryElement.text().trim());
            }
            
            // 작성자 닉네임 추출 - 정확한 CSS Selector 사용
            // 방법 1: 정확한 CSS Selector로 찾기 (제거 - 잘못된 값 설정)
            // Element nicknameElement = doc.selectFirst("#main-content > div > div > div > div > div > button > div > div > span");
            // if (nicknameElement != null) {
            //     crawledData.put("authorName", nicknameElement.text().trim());
            //     System.out.println("방법 1으로 작성자 찾음: " + nicknameElement.text().trim());
            // }
            
            // 방법 2: 더 구체적인 클래스 조합으로 찾기 (제거 - 잘못된 값 설정)
            // Element altNicknameElement = doc.selectFirst("div.n4kioi9 button div div span.sprinkles_fontSize_200_base__1byufe8uu.sprinkles_fontWeight_semibold__1byufe81y.sprinkles_lineHeight_body.medium_base__1byufe8w6.sprinkles_color_neutral__1byufe81");
            // if (altNicknameElement != null) {
            //     crawledData.put("authorName", altNicknameElement.text().trim());
            //     System.out.println("방법 2로 작성자 찾음: " + altNicknameElement.text().trim());
            // }
            
            // 방법 3: 모든 semibold span을 가져와서 세 번째 요소 선택
            if (!crawledData.containsKey("authorName") || crawledData.get("authorName").isEmpty()) {
                Elements allSemiboldSpans = doc.select("span.sprinkles_fontWeight_semibold__1byufe81y");
                System.out.println("찾은 semibold span 개수: " + allSemiboldSpans.size());
                
                if (allSemiboldSpans.size() >= 3) {
                    // 모든 span의 텍스트 출력
                    for (int i = 0; i < allSemiboldSpans.size(); i++) {
                        Element span = allSemiboldSpans.get(i);
                        System.out.println("span " + i + " 텍스트: '" + span.text().trim() + "'");
                    }
                    
                    // 세 번째 요소가 작성자 (인덱스 2)
                    Element authorSpan = allSemiboldSpans.get(2);
                    crawledData.put("authorName", authorSpan.text().trim());
                    System.out.println("방법 3으로 작성자 찾음: " + authorSpan.text().trim());
                }
            }
            
            // 작성자 프로필 이미지 추출
            Element authorImageElement = doc.selectFirst("span._17hdezf0 img");
            if (authorImageElement != null) {
                crawledData.put("authorImage", authorImageElement.attr("src"));
            }
            
            // 위치 정보 추출 (곤지암읍)
            Element locationElement = doc.selectFirst("span._1pwsqmm0");
            if (locationElement != null) {
                crawledData.put("location", locationElement.text().trim());
            }
            
            // 작성 시간 추출
            Element timeElement = doc.selectFirst("time");
            if (timeElement != null) {
                crawledData.put("createdTime", timeElement.attr("datetime"));
            }
            
            // 상세 주소 추출 (곤지암천자전거길 1900)
            Element addressElement = doc.selectFirst("span._1pwsqmm0._1pwsqmm2");
            if (addressElement != null) {
                crawledData.put("address", addressElement.text().trim());
            }
            
            // 좋아요, 댓글, 저장 수 추출
            Elements actionButtons = doc.select("button:has(svg)");
            if (actionButtons.size() >= 3) {
                // 좋아요 수
                Element likeButton = actionButtons.get(0);
                Element likeCount = likeButton.selectFirst("span.sprinkles_fontSize_100_base__1byufe8uq");
                if (likeCount != null) {
                    crawledData.put("likes", likeCount.text().trim());
                }
                
                // 댓글 수
                Element commentButton = actionButtons.get(1);
                Element commentCount = commentButton.selectFirst("span.sprinkles_fontSize_100_base__1byufe8uq");
                if (commentCount != null) {
                    crawledData.put("comments", commentCount.text().trim());
                }
                
                // 저장 수
                Element saveButton = actionButtons.get(2);
                Element saveCount = saveButton.selectFirst("div._1c6hyct0");
                if (saveCount != null) {
                    crawledData.put("saves", saveCount.text().trim());
                }
            }
            
            // 조회수 추출
            Element viewElement = doc.selectFirst("span:contains(조회)");
            if (viewElement != null) {
                String viewText = viewElement.text().trim();
                if (viewText.contains("조회")) {
                    String viewCount = viewText.replace("조회", "").trim();
                    crawledData.put("views", viewCount);
                }
            }
            
            // 메인 이미지 추출 (assetstorage.krrt.io)
            Element mainImageElement = doc.selectFirst("img[src*='assetstorage.krrt.io']");
            if (mainImageElement != null) {
                crawledData.put("mainImage", mainImageElement.attr("src"));
            }
            
            // 댓글과 대댓글 크롤링
            crawlComments(doc, crawledData);
            
            // 성공 상태 추가
            crawledData.put("status", "success");
            crawledData.put("url", url);
            crawledData.put("source", "daangn");
            
        } catch (Exception e) {
            crawledData.put("status", "error");
            crawledData.put("error", "당근마켓 크롤링 중 오류: " + e.getMessage());
        }
        
        return crawledData;
    }
    
    /**
     * 댓글과 대댓글을 크롤링합니다.
     */
    private void crawlComments(Document doc, Map<String, String> crawledData) {
        try {
            // 댓글 목록 찾기
            Elements commentItems = doc.select("ul.sprinkles_display_flex_base__1byufe82i.sprinkles_flexDirection_column_base__1byufe8te.sprinkles_gap_4_base__1byufe8qq > li");
            
            if (commentItems.isEmpty()) {
                return;
            }
            
            StringBuilder commentsJson = new StringBuilder();
            commentsJson.append("[");
            
            for (int i = 0; i < commentItems.size(); i++) {
                Element commentItem = commentItems.get(i);
                
                if (i > 0) commentsJson.append(",");
                commentsJson.append("{");
                
                // 댓글 작성자
                Element authorElement = commentItem.selectFirst("button span._17hdezf0 img");
                if (authorElement != null) {
                    commentsJson.append("\"authorImage\":\"").append(authorElement.attr("src")).append("\",");
                }
                
                // 댓글 작성자 닉네임
                Element nicknameElement = commentItem.selectFirst("button.sprinkles_fontSize_100_base__1byufe8uq");
                if (nicknameElement != null) {
                    commentsJson.append("\"authorName\":\"").append(nicknameElement.text().trim()).append("\",");
                }
                
                // 댓글 위치와 시간
                Element locationTimeElement = commentItem.selectFirst("span._1pwsqmm0._1pwsqmm1._1pwsqmm8");
                if (locationTimeElement != null) {
                    String locationTime = locationTimeElement.text().trim();
                    if (locationTime.contains("·")) {
                        String[] parts = locationTime.split("·");
                        if (parts.length >= 2) {
                            commentsJson.append("\"location\":\"").append(parts[0].trim()).append("\",");
                            commentsJson.append("\"time\":\"").append(parts[1].trim()).append("\",");
                        }
                    }
                }
                
                // 댓글 내용
                Element contentElement = commentItem.selectFirst("p.fu5dlc2");
                if (contentElement != null) {
                    commentsJson.append("\"content\":\"").append(contentElement.text().trim()).append("\",");
                }
                
                // 좋아요 수
                Element likeElement = commentItem.selectFirst("div._1c6hyct0");
                if (likeElement != null) {
                    commentsJson.append("\"likes\":\"").append(likeElement.text().trim()).append("\",");
                }
                
                // 대댓글 크롤링
                Elements replyItems = commentItem.select("ul.sprinkles_display_flex_base__1byufe82i.sprinkles_flexDirection_column_base__1byufe8te.sprinkles_gap_3_base__1byufe8qm > li");
                if (!replyItems.isEmpty()) {
                    commentsJson.append("\"replies\":[");
                    for (int j = 0; j < replyItems.size(); j++) {
                        Element replyItem = replyItems.get(j);
                        
                        if (j > 0) commentsJson.append(",");
                        commentsJson.append("{");
                        
                        // 대댓글 작성자 이미지
                        Element replyAuthorElement = replyItem.selectFirst("button span._17hdezf0 img");
                        if (replyAuthorElement != null) {
                            commentsJson.append("\"authorImage\":\"").append(replyAuthorElement.attr("src")).append("\",");
                        }
                        
                        // 대댓글 작성자 닉네임
                        Element replyNicknameElement = replyItem.selectFirst("button.sprinkles_fontSize_100_base__1byufe8uq");
                        if (replyNicknameElement != null) {
                            commentsJson.append("\"authorName\":\"").append(replyNicknameElement.text().trim()).append("\",");
                        }
                        
                        // 대댓글 위치와 시간
                        Element replyLocationTimeElement = replyItem.selectFirst("span._1pwsqmm0._1pwsqmm1._1pwsqmm8");
                        if (replyLocationTimeElement != null) {
                            String replyLocationTime = replyLocationTimeElement.text().trim();
                            if (replyLocationTime.contains("·")) {
                                String[] replyParts = replyLocationTime.split("·");
                                if (replyParts.length >= 2) {
                                    commentsJson.append("\"location\":\"").append(replyParts[0].trim()).append("\",");
                                    commentsJson.append("\"time\":\"").append(replyParts[1].trim()).append("\",");
                                }
                            }
                        }
                        
                        // 대댓글 내용
                        Element replyContentElement = replyItem.selectFirst("p.fu5dlc2");
                        if (replyContentElement != null) {
                            commentsJson.append("\"content\":\"").append(replyContentElement.text().trim()).append("\",");
                        }
                        
                        // 대댓글 좋아요 수
                        Element replyLikeElement = replyItem.selectFirst("div._1c6hyct0");
                        if (replyLikeElement != null) {
                            commentsJson.append("\"likes\":\"").append(replyLikeElement.text().trim()).append("\"");
                        } else {
                            commentsJson.append("\"likes\":\"0\"");
                        }
                        
                        commentsJson.append("}");
                    }
                    commentsJson.append("]");
                } else {
                    commentsJson.append("\"replies\":[]");
                }
                
                commentsJson.append("}");
            }
            
            commentsJson.append("]");
            
            // 댓글 데이터를 JSON 문자열로 저장
            crawledData.put("comments", commentsJson.toString());
            
        } catch (Exception e) {
            crawledData.put("comments", "[]");
            System.err.println("댓글 크롤링 중 오류: " + e.getMessage());
        }
    }
    
    /**
     * 일반적인 웹페이지를 크롤링합니다.
     */
    private Map<String, String> crawlGeneralPage(Document doc, String url) {
        Map<String, String> crawledData = new HashMap<>();
        
        try {
            // 제목 추출 (title 태그 또는 h1 태그)
            String title = doc.title();
            if (title != null && !title.trim().isEmpty()) {
                crawledData.put("title", title);
            }
            
            // 메인 제목 추출 (h1 태그)
            Element h1Element = doc.selectFirst("h1");
            if (h1Element != null) {
                crawledData.put("mainTitle", h1Element.text().trim());
            }
            
            // 메타 설명 추출
            Element metaDesc = doc.selectFirst("meta[name=description]");
            if (metaDesc != null) {
                crawledData.put("description", metaDesc.attr("content"));
            }
            
            // 메인 이미지 추출 (og:image 또는 첫 번째 img 태그)
            Element ogImage = doc.selectFirst("meta[property=og:image]");
            if (ogImage != null) {
                crawledData.put("mainImage", ogImage.attr("content"));
            } else {
                Element firstImg = doc.selectFirst("img");
                if (firstImg != null) {
                    String imgSrc = firstImg.attr("src");
                    if (!imgSrc.startsWith("http")) {
                        // 상대 경로인 경우 절대 경로로 변환
                        imgSrc = url + (imgSrc.startsWith("/") ? "" : "/") + imgSrc;
                    }
                    crawledData.put("mainImage", imgSrc);
                }
            }
            
            // 본문 내용 추출 (article, main, 또는 p 태그들)
            Element article = doc.selectFirst("article");
            if (article != null) {
                crawledData.put("content", article.text().trim());
            } else {
                Element main = doc.selectFirst("main");
                if (main != null) {
                    crawledData.put("content", main.text().trim());
                } else {
                    // p 태그들의 텍스트를 수집
                    Elements paragraphs = doc.select("p");
                    StringBuilder content = new StringBuilder();
                    for (Element p : paragraphs) {
                        if (p.text().trim().length() > 50) { // 짧은 텍스트는 제외
                            content.append(p.text().trim()).append(" ");
                        }
                    }
                    if (content.length() > 0) {
                        crawledData.put("content", content.toString().trim());
                    }
                }
            }
            
            // 주소 정보 추출 (주소 관련 텍스트가 포함된 요소들)
            Elements addressElements = doc.select("*:containsOwn(주소), *:containsOwn(Address), *:containsOwn(위치)");
            if (!addressElements.isEmpty()) {
                for (Element element : addressElements) {
                    String text = element.text().trim();
                    if (text.contains("주소") || text.contains("Address") || text.contains("위치")) {
                        crawledData.put("address", text);
                        break;
                    }
                }
            }
            
            // 성공 상태 추가
            crawledData.put("status", "success");
            crawledData.put("url", url);
            crawledData.put("source", "general");
            
        } catch (Exception e) {
            crawledData.put("status", "error");
            crawledData.put("error", "일반 크롤링 중 오류: " + e.getMessage());
        }
        
        return crawledData;
    }
    
    /**
     * 크롤링된 데이터를 Article 엔티티에 저장합니다.
     * @param articleIdx 업데이트할 게시글의 인덱스
     * @param crawledData 크롤링된 데이터
     * @return 저장 성공 여부
     */
    public boolean saveCrawledDataToDatabase(Long articleIdx, Map<String, String> crawledData) {
        try {
            Optional<Article> articleOpt = articleRepository.findById(articleIdx);
            if (articleOpt.isPresent()) {
                Article article = articleOpt.get();
                
                // 크롤링된 데이터로 Article 엔티티 업데이트
                if (crawledData.containsKey("title") && (article.getTitle() == null || article.getTitle().trim().isEmpty())) {
                    article.setTitle(crawledData.get("title"));
                }
                
                if (crawledData.containsKey("mainTitle") && (article.getTitle() == null || article.getTitle().trim().isEmpty())) {
                    article.setTitle(crawledData.get("mainTitle"));
                }
                
                if (crawledData.containsKey("content") && (article.getBody() == null || article.getBody().trim().isEmpty())) {
                    // 내용이 너무 길 경우 잘라내기
                    String content = crawledData.get("content");
                    if (content.length() > 1000) {
                        content = content.substring(0, 1000) + "...";
                    }
                    article.setBody(content);
                }
                
                if (crawledData.containsKey("mainImage") && (article.getMainImageUrls() == null || article.getMainImageUrls().trim().isEmpty())) {
                    article.setMainImageUrls(crawledData.get("mainImage"));
                }
                
                if (crawledData.containsKey("description") && (article.getSubject() == null || article.getSubject().trim().isEmpty())) {
                    String description = crawledData.get("description");
                    if (description.length() > 200) {
                        description = description.substring(0, 200) + "...";
                    }
                    article.setSubject(description);
                }
                
                if (crawledData.containsKey("address") && (article.getPlaceAddress() == null || article.getPlaceAddress().trim().isEmpty())) {
                    article.setPlaceAddress(crawledData.get("address"));
                }
                
                // 업데이트 시간 설정
                article.setUpdateDate(java.time.LocalDateTime.now());
                
                // 저장
                articleRepository.save(article);
                
                // 댓글 데이터도 DB에 저장 (선택사항)
                if (crawledData.containsKey("comments")) {
                    saveCommentsToDatabase(articleIdx, crawledData.get("comments"));
                }
                
                // 크롤링 데이터를 article_crawling 테이블에 저장
                saveCrawlingDataToDatabase(articleIdx, crawledData, article);
                
                return true;
                
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * JSON 댓글 데이터를 파싱하여 객체 리스트로 변환합니다.
     */
    public List<Map<String, Object>> parseCommentsFromJson(String commentsJson) {
        try {
            if (commentsJson == null || commentsJson.trim().isEmpty()) {
                return new ArrayList<>();
            }
            
            TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<List<Map<String, Object>>>() {};
            return objectMapper.readValue(commentsJson, typeRef);
            
        } catch (Exception e) {
            System.err.println("댓글 JSON 파싱 중 오류: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * 댓글 데이터를 DB에 저장합니다.
     */
    private void saveCommentsToDatabase(Long articleIdx, String commentsJson) {
        try {
            // JSON 파싱
            // 여기서 Comment 엔티티에 저장하는 로직을 구현할 수 있습니다.
            // 현재는 로그만 출력
            System.out.println("댓글 데이터를 DB에 저장할 수 있습니다: " + commentsJson);
            
            // TODO: Comment 엔티티에 저장하는 로직 구현
            // 1. CommentRepository 주입
            // 2. JSON 파싱하여 Comment 객체 생성
            // 3. articleIdx와 연결하여 저장
            
        } catch (Exception e) {
            System.err.println("댓글 저장 중 오류: " + e.getMessage());
        }
    }
    
    /**
     * 크롤링 데이터를 article_crawling 테이블에 저장합니다.
     */
    private void saveCrawlingDataToDatabase(Long articleIdx, Map<String, String> crawledData, Article article) {
        try {
            // 기존 크롤링 데이터가 있는지 확인
            if (articleCrawlingRepository.existsByArticleIdx(articleIdx)) {
                System.out.println("이미 크롤링 데이터가 존재합니다: articleIdx=" + articleIdx);
                return;
            }
            
            // ArticleCrawling 객체 생성 (idx는 null로 설정하여 자동 생성되도록 함)
            ArticleCrawling articleCrawling = new ArticleCrawling(
                null,                                    // idx (자동 생성)
                article.getGroupId(),                    // group_id
                articleIdx,                              // article_idx
                java.time.LocalDateTime.now(),           // reg_date (현재 시간)
                crawledData.get("title"),                // title
                crawledData.get("content"),              // body
                crawledData.get("category"),             // category
                crawledData.get("authorName"),           // author
                article.getVirtualAddress(),             // virtual_address
                crawledData.get("address"),              // place_address
                crawledData.get("likes"),                // like
                crawledData.get("comments"),             // content (댓글 JSON)
                crawledData.get("views"),                // views
                crawledData.get("mainImage"),            // main_image_urls
                article.getUser() != null ? article.getUser().getIdx() : 0L,  // user_idx
                "success"                               // status
            );
            
            // DB에 저장
            articleCrawlingRepository.save(articleCrawling);
            System.out.println("크롤링 데이터를 article_crawling 테이블에 저장했습니다: articleIdx=" + articleIdx);
            
        } catch (Exception e) {
            System.err.println("크롤링 데이터 저장 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 