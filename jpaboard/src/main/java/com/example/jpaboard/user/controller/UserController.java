
package com.example.jpaboard.user.controller;




import com.example.jpaboard.article.dao.ArticleRepository;
import com.example.jpaboard.article.domain.Article;
import com.example.jpaboard.user.dao.UserRepository;
import com.example.jpaboard.user.domain.User;
import org.apache.logging.log4j.LogManager;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/usr/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("doLogout")
    @ResponseBody
    public String doLogout(HttpSession session){
        boolean isLogined = false;


        if(session.getAttribute("loginedUserId") != null){
            isLogined = true;
        }

        if( isLogined == false){
//            return "이미 로그아웃 되었습니다.";
            return """
                <script>
                alert('이미 로그아웃 되었습니다.');
                location.replace('/');
                </script>
                """;

        }
        session.removeAttribute("loginedUserId");
//        return "로그아웃 되었습니다.";
        return """
                <script>
                location.replace('/');
                </script>
                """;
    }


    @RequestMapping("login")
    public String showLogin(HttpSession session, Model model) {

        boolean isLogined = false;
        long loginedUserId = 0;

        if (session.getAttribute("loginedUserId") != null) {
            isLogined = true;
            loginedUserId = (long) session.getAttribute("loginedUserId");
        }

        if (isLogined) {
            model.addAttribute("msg", "이미 로그인 되었습니다.");
            model.addAttribute("historyBack", true);
            return "common/js";
        }
        return "usr/user/login";
    }

    @RequestMapping("doLogin")
    @ResponseBody
    public String doLogin(String userId, String password, HttpServletRequest req, HttpServletResponse resp) {

        if (userId == null || userId.trim().length() == 0) {
            return """
                <script>
                alert('아이디를 입력해주세요');
                history.back();
                </script>
                """;
        }


        userId = userId.trim();
        Optional<User> user = userRepository.findByUserId(userId);

        if(user.isEmpty()){
            return """
                <script>
                alert('일치하는 회원이 존재하지 않습니다.');
                history.back();
                </script>
                """;
        }


        if (password == null || password.trim().length() == 0) {
            return """
                <script>
                alert('비밀번호를 입력해주세요');
                history.back();
                </script>
                """;
        }
        password = password.trim();

        if(user.get().getPassword().equals(password) == false){
            return """
                <script>
                alert('비밀번호가 일치하지 않습니다');
                history.back();
                </script>
                """;
        }

        HttpSession session = req.getSession();
        session.setAttribute("loginedUserId", user.get().getIdx());
        session.setAttribute("roleLevel", user.get().getRoleLevel());

        return """
                <script>
                alert('%s님 환영합니다.');
                location.replace('/');
                </script>
                """.formatted(user.get().getName());
    }

    @RequestMapping("join")
    public String showJoin(HttpSession session, Model model) {

        boolean isLogined = false;
        long loginedUserId = 0;

        if (session.getAttribute("loginedUserId") != null) {
            isLogined = true;
            loginedUserId = (long) session.getAttribute("loginedUserId");
        }


        if (isLogined && loginedUserId != 1) {
            model.addAttribute("msg", "권한이 없습니다.");
            model.addAttribute("historyBack", true);
            return "common/js";
        }

        if (!isLogined) {
            model.addAttribute("msg", "권한이 없습니다..");
            model.addAttribute("historyBack", true);
            return "common/js";
        }

//        if (isLogined) {
//            model.addAttribute("msg", "이미 로그인중 입니다.");
//            model.addAttribute("historyBack", true);
//            return "common/js";
//        }
        return "usr/user/join";
    }

    @RequestMapping("doJoin")
    @ResponseBody
    public String doJoin(String userName, String userId, String password, String userCompany) {



        if (userId == null || userId.trim().length() == 0) {
//            return "아이디를 입력해주세요.";
            return """
                <script>
                alert('아이디를 입력해주세요');
                history.back();
                </script>
                """;
        }
        userId = userId.trim();

        boolean existsByUserId = userRepository.existsByUserId(userId);

        if(existsByUserId){
//            return "입력하신 아이디(%s)은 이미 사용중입니다.".formatted(userId);
            return """
                <script>
                alert('입력하신 아이디(%s)는 이미 사용중입니다.');
                history.back();
                </script>
                """.formatted(userId);
        }


        if (password == null || password.trim().length() == 0) {
//            return "비밀번호를 입력해주세요.";
            return """
                <script>
                alert('비밀번호를 입력해주세요');
                history.back();
                </script>
                """;
        }
        password = password.trim();


        if (userName == null || userName.trim().length() == 0) {
//            return "이름을 입력해주세요.";
            return """
                <script>
                alert('이름을 입력해주세요');
                history.back();
                </script>
                """;
        }
        userName = userName.trim();




        if (userCompany == null || userCompany.trim().length() == 0) {
//            return "회사명을 입력해주세요.";
            return """
                <script>
                alert('회사명을 입력해주세요');
                history.back();
                </script>
                """;
        }
        userCompany = userCompany.trim();


        User user = new User();
        user.setRegDate(LocalDateTime.now());
        user.setUpdateDate(LocalDateTime.now());
        user.setName(userName);
        user.setUserId(userId);
        user.setPassword(password);
        user.setCompany(userCompany);

        userRepository.save(user);
//        return "%d번 회원이 생성되었습니다.".formatted(user.getIdx());
        return """
                <script>
                alert('%s회원이 생성되었습니다.');
                location.replace('/');
                </script>
                """.formatted(user.getUserId());
    }

    @RequestMapping("mypage")
    public User showMypage(HttpSession session, Model model){

        boolean isLogined = false;
        long loginedUserId = 0;

        if(session.getAttribute("loginedUserId") != null){
            isLogined = true;
            loginedUserId = (long) session.getAttribute("loginedUserId");
        }

        if( isLogined == false){
            return null;
        }

        Optional<User> user = userRepository.findById(loginedUserId);

        if(user.isEmpty()){
            return null;
        }

        List<User> users = userRepository.findAll();

    for(User str : users){
        if(str.getIdx().equals(loginedUserId)){
            model.addAttribute("users", users);
        }
    }

        return user.get();
    }


    @RequestMapping("doAddMoney")
    @ResponseBody
    public String doAddMoney(Long idx, Long addMoney, HttpSession session, Model model){

        boolean isLogined = false;
        long loginedUserId = 0;

        if (session.getAttribute("loginedUserId") != null) {
            isLogined = true;
            loginedUserId = (long) session.getAttribute("loginedUserId");
        }

        if(isLogined == false){
            return """
                <script>
                alert('로그인 후 이용해주세요.');
                history.back();
                </script>
                """;
        }


        User user = userRepository.findById(idx).get();

        if(loginedUserId != 1){
            return """
                <script>
                alert('권한이 없습니다.');
                history.back();
                </script>
                """;
        }
        if (idx == null) {
            return """
        <script>
        alert('사용자 ID가 누락되었습니다.');
        history.back();
        </script>
        """;
        }
        if (addMoney == null || addMoney == 0) {
            return """
        <script>
        alert('금액이 누락되었습니다.');
        history.back();
        </script>
        """;
        }


            user.setIdx(idx);
            user.setBalance(user.getBalance()+addMoney);


        userRepository.save(user);

        return """
                <script>
                alert('%d번 %s에 %d원 충전되었습니다');
                 location.replace('/usr/user/mypage');
                </script>
                """.formatted(user.getIdx(), user.getName(), addMoney);

    }

}
