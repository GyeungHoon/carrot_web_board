
package com.example.jpaboard.article.controller;




import com.example.jpaboard.article.dao.ArticleRepository;
import com.example.jpaboard.article.dao.CommentRepository;
import com.example.jpaboard.article.dao.InstancesRepository;
import com.example.jpaboard.article.dao.ReplyRepository;
import com.example.jpaboard.article.dao.ArticleCrawlingRepository;
import com.example.jpaboard.article.domain.*;
import com.example.jpaboard.crawling.service.CrawlingService;
import com.example.jpaboard.user.dao.UserRepository;
import com.example.jpaboard.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;

@Controller
@RequestMapping("/usr/article")
public class ArticleController {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private InstancesRepository instancesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleCrawlingRepository articleCrawlingRepository;
    
    @Autowired
    private CrawlingService crawlingService;


    @RequestMapping("list")
    public String showList(Model model, HttpSession session) {

        boolean isLogined = false;
        long loginedUserId = 0;
        short roleLevel = 0;

        if (session.getAttribute("loginedUserId") != null) {
            isLogined = true;
            loginedUserId = (long) session.getAttribute("loginedUserId");
            
            // 로그인한 사용자 정보 조회
            User loginedUser = userRepository.findById(loginedUserId).orElse(null);
            if (loginedUser != null) {
                roleLevel = loginedUser.getRoleLevel();
            }
        }

//        if (!isLogined) {
//            model.addAttribute("msg", "로그인 후 이용해주세요.");
//            model.addAttribute("historyBack", true);
//            return "common/js";
//        }

        List<Article> articles;
        List<Comment> comments;
        List<Reply> replys;

        // 관리자인 경우 (roleLevel >= 10) 모든 게시글 조회
        if (roleLevel >= 10) {
            articles = articleRepository.findAll();
            comments = commentRepository.findAll();
            replys = replyRepository.findAll();
        } else {
            // 일반 사용자인 경우 자신이 작성한 게시글만 조회
            articles = articleRepository.findByUserIdx(loginedUserId);
            comments = commentRepository.findByUserIdx(loginedUserId);
            replys = replyRepository.findByUserIdx(loginedUserId);
        }

        model.addAttribute("articles", articles);
        model.addAttribute("comments", comments);
        model.addAttribute("replys", replys);

        return "usr/article/list";
    }

//     @RequestMapping("doModify")
//     @ResponseBody
//      public String doModify(long idx, String title, String body, HttpSession session) {
//         boolean isLogined = false;
//         long loginedUserId = 0;
//
//         if (session.getAttribute("loginedUserId") != null) {
//             isLogined = true;
//             loginedUserId = (long) session.getAttribute("loginedUserId");
//         }
//
//         if(isLogined == false){
//             return """
//                <script>
//                alert('로그인 후 이용해주세요.');
//                history.back();
//                </script>
//                """;
//         }
//
//
//        Article article = articleRepository.findById(idx).get();
//
//         if(article.getUser().getIdx() != loginedUserId){
//             return """
//                <script>
//                alert('권한이 없습니다.');
//                history.back();
//                </script>
//                """;
//         }
//
//        if ( title != null ) {
//            article.setTitle(title);
//        }
//
//        if ( body != null ) {
//            article.setBody(body);
//        }
//
//        articleRepository.save(article);
//
//         return """
//                <script>
//                alert('%d번 게시물이 수정되었습니다.');
//                location.replace('detail?idx=%d');
//                </script>
//                """.formatted(article.getIdx(), article.getIdx());
//
//    }

//    @RequestMapping("doDelete")
//    @ResponseBody
//    public String doDelete(long idx, HttpSession session) {
//
//        boolean isLogined = false;
//        long loginedUserId = 0;
//
//        if (session.getAttribute("loginedUserId") != null) {
//            isLogined = true;
//            loginedUserId = (long) session.getAttribute("loginedUserId");
//        }
//
//        if(isLogined == false){
//            return """
//                <script>
//                alert('로그인 후 이용해주세요.');
//                history.back();
//                </script>
//                """;
//        }
//
//
//
//        if ( articleRepository.existsById(idx) == false ) {
//          return """
//                <script>
//                alert('%d번 게시물은 이미 삭제되었거나 존재하지 않습니다.');
//                history.back();
//                </script>
//                """.formatted(idx);
//        }
//
//        Article article = articleRepository.findById(idx).get();
//        if(article.getUser().getIdx() != loginedUserId){
//            return """
//                <script>
//                alert('권한이 없습니다.');
//                history.back();
//                </script>
//                """;
//        }
//
//
//        articleRepository.deleteById(idx);
//        return """
//                <script>
//                alert('%d번 게시물이 삭제되었습니다.');
//                location.replace('list');
//                </script>
//                """.formatted(idx);
//    }

    @RequestMapping("write")
    public String showWrite(HttpSession session, Model model) {

        boolean isLogined = false;
        long loginedUserId = 0;

        if (session.getAttribute("loginedUserId") != null) {
            isLogined = true;
            loginedUserId = (long) session.getAttribute("loginedUserId");
        }

        if (isLogined == false) {
            model.addAttribute("msg", "로그인 후 이용해주세요.");
            model.addAttribute("historyBack", true);
            return "common/js";
        }

        User loginedUser = userRepository.findById((Long) session.getAttribute("loginedUserId")).orElse(null);
        model.addAttribute("loginedUser", loginedUser);


        List<Instances> instances;

        instances = instancesRepository.findAll();

        // conferment가 비어있고 street_number가 비어있지 않은 것만 필터링
        List<Instances> filteredInstances = instances.stream()
                .filter(instance -> (instance.getConferment() == null || instance.getConferment().isEmpty()) 
                        && (instance.getStreet_number() != null && !instance.getStreet_number().isEmpty()))
                .collect(java.util.stream.Collectors.toList());

        // street_number별로 그룹화하고 flow 조건 확인
        Map<String, Object> resultInstances = filteredInstances.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Instances::getStreet_number,
                        java.util.stream.Collectors.toList()
                ))
                .entrySet().stream()
                .filter(entry -> {
                    List<Instances> groupInstances = entry.getValue();
                    
                    // 각 flow 타입별로 article_setup과 comment_setup이 1 이상인지 확인
                    boolean articleValid = groupInstances.stream()
                            .anyMatch(inst -> "article".equals(inst.getFlow()) 
                                    && inst.getArticle_setup() != null && inst.getArticle_setup() >= 1
                                    && inst.getComment_setup() != null && inst.getComment_setup() >= 1);
                    
                    boolean commentValid = groupInstances.stream()
                            .anyMatch(inst -> "comment".equals(inst.getFlow()) 
                                    && inst.getArticle_setup() != null && inst.getArticle_setup() >= 1
                                    && inst.getComment_setup() != null && inst.getComment_setup() >= 1);
                    
                    boolean replyValid = groupInstances.stream()
                            .anyMatch(inst -> "reply".equals(inst.getFlow()) 
                                    && inst.getArticle_setup() != null && inst.getArticle_setup() >= 1
                                    && inst.getComment_setup() != null && inst.getComment_setup() >= 1);
                    
                    return articleValid && commentValid && replyValid;
                })
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            // 첫 번째 인스턴스의 주소 정보를 가져옴 (모든 인스턴스가 같은 주소를 가짐)
                            Instances firstInstance = entry.getValue().get(0);
                            Map<String, Object> instanceData = new java.util.HashMap<>();
                            instanceData.put("count", (long) entry.getValue().size());
                            instanceData.put("virtualAddress", firstInstance.getVirtual_address());
                            instanceData.put("longitude", firstInstance.getLongitude());
                            instanceData.put("latitude", firstInstance.getLatitude());
                            return instanceData;
                        }
                ));

        model.addAttribute("instances", instances);
        model.addAttribute("filteredInstances", resultInstances);



        return "usr/article/write";
    }

    @RequestMapping("detail")
    public String showDetail(long idx, Model model, HttpSession session) {

        boolean isLogined = false;
        long loginedUserId = 0;
        short roleLevel = 0;

        if (session.getAttribute("loginedUserId") != null) {
            isLogined = true;
            loginedUserId = (long) session.getAttribute("loginedUserId");
            
            // 로그인한 사용자 정보 조회
            User loginedUser = userRepository.findById(loginedUserId).orElse(null);
            if (loginedUser != null) {
                roleLevel = loginedUser.getRoleLevel();
            }
        }

        if (isLogined == false) {
            model.addAttribute("msg", "로그인 후 이용해주세요.");
            model.addAttribute("historyBack", true);
            return "common/js";
        }

        Optional<Article> opArticle = articleRepository.findById(idx);
        if (opArticle.isEmpty()) {
            model.addAttribute("msg", "존재하지 않는 게시글입니다.");
            model.addAttribute("historyBack", true);
            return "common/js";
        }
        
        Article article = opArticle.get();

        // 관리자가 아니고, 자신이 작성한 게시글이 아닌 경우 접근 제한
        if (roleLevel < 10 && !article.getUser().getIdx().equals(loginedUserId)) {
            model.addAttribute("msg", "권한이 없습니다.");
            model.addAttribute("historyBack", true);
            return "common/js";
        }

        // Comment와 Reply는 groupId로 조회해야 합니다
        List<Comment> comments = commentRepository.findByGroupId(article.getGroupId());
        List<Reply> replys = replyRepository.findByGroupId(article.getGroupId());

        model.addAttribute("article", article);
        model.addAttribute("comments", comments);
        model.addAttribute("replys", replys);

        return "usr/article/detail";
    }


    @RequestMapping("sync")
    public String showSync(long idx, Model model, HttpSession session) {

        boolean isLogined = false;
        long loginedUserId = 0;
        short roleLevel = 0;

        if (session.getAttribute("loginedUserId") != null) {
            isLogined = true;
            loginedUserId = (long) session.getAttribute("loginedUserId");

            // 로그인한 사용자 정보 조회
            User loginedUser = userRepository.findById(loginedUserId).orElse(null);
            if (loginedUser != null) {
                roleLevel = loginedUser.getRoleLevel();
            }
        }

        if (isLogined == false) {
            model.addAttribute("msg", "로그인 후 이용해주세요.");
            model.addAttribute("historyBack", true);
            return "common/js";
        }

        Optional<Article> opArticle = articleRepository.findById(idx);
        if (opArticle.isEmpty()) {
            model.addAttribute("msg", "존재하지 않는 게시글입니다.");
            model.addAttribute("historyBack", true);
            return "common/js";
        }

        Article articles = opArticle.get();

        // 관리자가 아니고, 자신이 작성한 게시글이 아닌 경우 접근 제한
        if (roleLevel < 10 && !articles.getUser().getIdx().equals(loginedUserId)) {
            model.addAttribute("msg", "권한이 없습니다.");
            model.addAttribute("historyBack", true);
            return "common/js";
        }

        // 특정 게시글 조회
        Optional<Article> articleOpt = articleRepository.findById(idx);
        if (articleOpt.isEmpty()) {
            model.addAttribute("msg", "게시글을 찾을 수 없습니다.");
            return "common/js";
        }

        Article article = articleOpt.get();
        
                // 해당 게시글의 크롤링 데이터 조회
        Optional<ArticleCrawling> crawlingOpt = articleCrawlingRepository.findByArticleIdx(idx);
        
        // 크롤링된 댓글 데이터 파싱
        List<Map<String, Object>> crawledComments = new ArrayList<>();
        if (crawlingOpt.isPresent() && crawlingOpt.get().getContent() != null) {
            crawledComments = crawlingService.parseCommentsFromJson(crawlingOpt.get().getContent());
        }
        
        // 댓글과 대댓글 조회 (groupId로 조회)
        List<Comment> comments = commentRepository.findByGroupId(article.getGroupId());
        List<Reply> replys = replyRepository.findByGroupId(article.getGroupId());
        
        model.addAttribute("article", article);
        model.addAttribute("crawling", crawlingOpt.orElse(null));  // 크롤링 데이터 추가
        model.addAttribute("crawledComments", crawledComments);    // 파싱된 댓글 데이터
        model.addAttribute("comments", comments);
        model.addAttribute("replys", replys);


        return "usr/article/sync";
    }
//    @RequestMapping("modify")
//    public String showModify(long idx, Model model, HttpSession session) {
//        boolean isLogined = false;
//        long loginedUserId = 0;
//
//        if (session.getAttribute("loginedUserId") != null) {
//            isLogined = true;
//            loginedUserId = (long) session.getAttribute("loginedUserId");
//        }
//
//        if (isLogined == false) {
//            model.addAttribute("msg", "로그인 후 이용해주세요.");
//            model.addAttribute("historyBack", true);
//            return "common/js";
//        }
//
//
//        Optional<Article> opArticle = articleRepository.findById(idx);
//        Article article = opArticle.get();
//
//        if (article.getUser().getIdx() != loginedUserId) {
//            model.addAttribute("msg", "권한이 없습니다.");
//            model.addAttribute("historyBack", true);
//            return "common/js";
//        }
//
//        model.addAttribute("article", article);
//
//        return "usr/article/modify";
//    }

    @RequestMapping("doWrite")
    @ResponseBody
    public String doWrite(String title, String body, String mainImageUrls, String virtualAddress, String placeZipcode, String placeAddress, String placeAddressDetail, String placeAddressExtra,
                          String commentZipcode,String commentAddress, String commentAddressDetail, String commentAddressExtra,
                          String subject, String commentContent, String imageUrl, String reply_content, String longitude, String latitude, String selectedInstance, HttpSession session) {

        boolean isLogined = false;
        long loginedUserId = 0;
        if(session.getAttribute("loginedUserId") != null){
            isLogined = true;
            loginedUserId = (long)session.getAttribute("loginedUserId");
        }

        if(isLogined == false){
            return """
                <script>
                alert('로그인 후 이용해주세요.');
                history.back();
                </script>
                """;
        }


        if (title == null || title.trim().length() == 0) {
            return """
                <script>
                alert('제목을 입력해주세요.');
                history.back();
                </script>
                """;
        }
        title = title.trim();

        if (body == null || body.trim().length() == 0) {
            return """
                <script>
                alert('내용을 입력해주세요.');
                history.back();
                </script>
                """;
        }
        body = body.trim();

        String groupId = UUID.randomUUID().toString();


        Article article = new Article();
        article.setRegDate(LocalDateTime.now());
        article.setUpdateDate(LocalDateTime.now());
        article.setGroupId(groupId);
        article.setTitle(title);
        article.setBody(body);
        article.setMainImageUrls(mainImageUrls);
        article.setVirtualAddress(virtualAddress);
        article.setPlaceAddress(placeAddress);
        article.setPlaceZipcode(placeZipcode);
        article.setPlaceAddressDetail(placeAddressDetail);
        article.setPlaceAddressExtra(placeAddressExtra);
        article.setSubject(subject);
        article.setLongitude(longitude);
        article.setLatitude(latitude);
        
        // selectedInstance가 있으면 virtualAddress에 설정하고 해당 인스턴스들의 conferment 업데이트
        if (selectedInstance != null && !selectedInstance.trim().isEmpty()) {
            article.setVirtualAddress(selectedInstance);
            
            // 해당 virtualAddress의 모든 인스턴스 레코드 조회
            List<Instances> selectedInstances = instancesRepository.findByVirtualAddress(selectedInstance);
            
            // 필터링 조건에 맞는 인스턴스들 중에서 각 flow 타입별로 하나씩만 conferment 업데이트
            boolean articleUpdated = false;
            boolean commentUpdated = false;
            boolean replyUpdated = false;
            
            for (Instances instance : selectedInstances) {
                // conferment가 비어있고, article_setup과 comment_setup이 1 이상인 조건 확인
                if ((instance.getConferment() == null || instance.getConferment().isEmpty()) &&
                    instance.getArticle_setup() != null && instance.getArticle_setup() >= 1 &&
                    instance.getComment_setup() != null && instance.getComment_setup() >= 1) {
                    
                    // 각 flow 타입별로 하나씩만 업데이트
                    if ("article".equals(instance.getFlow()) && !articleUpdated) {
                        instance.setConferment(String.valueOf(loginedUserId));
                        instancesRepository.save(instance);
                        articleUpdated = true;
                    } else if ("comment".equals(instance.getFlow()) && !commentUpdated) {
                        instance.setConferment(String.valueOf(loginedUserId));
                        instancesRepository.save(instance);
                        commentUpdated = true;
                    } else if ("reply".equals(instance.getFlow()) && !replyUpdated) {
                        instance.setConferment(String.valueOf(loginedUserId));
                        instancesRepository.save(instance);
                        replyUpdated = true;
                    }
                }
            }
        }


        Comment comment = new Comment();
        comment.setGroupId(groupId);
        comment.setRegDate(LocalDateTime.now());
        comment.setUpdateDate(LocalDateTime.now());
        comment.setCommentContent(commentContent);
        comment.setCommentZipcode(commentZipcode);
        comment.setCommentAddress(commentAddress);
        comment.setCommentAddressDetail(commentAddressDetail);
        comment.setCommentAddressExtra(commentAddressExtra);
        comment.setImageUrl(imageUrl);

        Reply reply = new Reply();
        reply.setGroupId(groupId);
        reply.setRegDate(LocalDateTime.now());
        reply.setUpdateDate(LocalDateTime.now());
        reply.setCommentContent(commentContent);
        reply.setReplyContent(reply_content);






        User user = userRepository.findById(loginedUserId).get();
        article.setUser(user);
        comment.setUser(user);
        reply.setUser(user);

        if (user.getBalance() < 30000 ) {
            return """
                <script>
                alert('요금을 충전해주세요.');
                history.back();
                </script>
                """;
        }

        user.setBalance(user.getBalance() - 30000);
        userRepository.save(user);

        articleRepository.save(article);
        commentRepository.save(comment);
        replyRepository.save(reply);
        return """
                <script>
                alert('%d번 게시물이 생성되었습니다.');
                location.replace('list');
                </script>
                """.formatted(article.getIdx());
    }

}
