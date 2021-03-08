package com.epi.controller;

import com.epi.bean.Article;
import com.epi.bean.User;
import com.epi.service.ArticleService;
import com.epi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RequestMapping("/user")
@Controller
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    ArticleService articleService;

    @RequestMapping(value="/login",method= RequestMethod.GET)
    public String login(){
        return "user/login";
    }
    @RequestMapping(value="/login",method= RequestMethod.POST)
    public String login(HttpServletRequest request, HttpServletResponse response, Model mv, HttpSession session){
        String userName = request.getParameter("username");
        String password = request.getParameter("password");
        if(userName.equals("")){
            mv.addAttribute("loginInfo","用户名不能为空");
            return "user/login";
        }else if(password.equals("")){
            mv.addAttribute("loginInfo","密码不能为空");
            return "user/login";
        }else{
            User user = userService.selectByUserName(userName);
            if(user != null && (user.getUserPassword().equals(password))){
                // 将物品列表全部获取
                List<Article> list=articleService.findAll();
                if(!(list == null)) {
                    mv.addAttribute("articleList", list);
                }
                // 获取user的id值进行
                User user1 = userService.selectByUserName(userName);
                session.setAttribute("username",userName);
                mv.addAttribute("userId",user1.getUserId());
                request.getSession().setAttribute("username",userName);
                mv.addAttribute("loginInfo",userName);
                return "article/listAll";
            }else {
                mv.addAttribute("loginInfo","用户名或密码错误");
                return "user/login";
            }
        }


    }

    @RequestMapping(value="/regist",method= RequestMethod.GET)
    public String regist(){
        return "user/regist";
    }
    @RequestMapping(value="/register",method= RequestMethod.POST)
    public String regist(HttpServletRequest request, HttpServletResponse response, ModelMap mv) throws Exception{
        String userName = request.getParameter("userName");
        String passWord = request.getParameter("userPassword");
        String confirmPassword = request.getParameter("user_confirm_password");
        String email = request.getParameter("email");
        Boolean register = true;
        if(userName.equals("")){
            mv.addAttribute("registerInfo","用户名不能为空");
            register = false;
        } else if(passWord.equals("")){
            mv.addAttribute("registerInfo","密码不能为空");
            register = false;
        } else if(confirmPassword.equals("")){
            mv.addAttribute("registerInfo","请输入确定密码");
            register = false;
        } else if(!passWord.equals(confirmPassword)){
            mv.addAttribute("registerInfo","两次密码输入不同");
            register = false;
        }else if (email.equals("")) {
            mv.addAttribute("registerInfo","邮箱不能为空");
            register = false;
        } else{

        }
        if(register){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = df.format(new Date());
            Date date = df.parse(time);
            userService.insertUser(new User(null,userName,passWord,email,date));
            return "user/login";
        }else{
            return "user/regist";
        }

    }


    // 异步判断用户是否已经注册
    @RequestMapping("/ajax")
    public void findByName(HttpServletRequest request,HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String userName = request.getParameter("username");
        User user = userService.selectByUserName(userName);
        if(user == null){
            response.getWriter().write("false");
        }else {
            response.getWriter().write("yes");
        }

    }


    // 个人主页
    @RequestMapping("/toUserMainPage")
    public String userMainPage(HttpServletRequest request, Model mv){
        // 再user表中查找到user的信息发送到个人主页的页面上
        String id  = request.getParameter("id");
        User user = userService.selectByUserId(Integer.valueOf(id));
        mv.addAttribute("user",user);
        return "user/userPage";
    }

    // 反馈和建议
    @RequestMapping("/toFeedBack")
    public String feedBack(HttpServletRequest request, Model mv){
        String userName = request.getParameter("id");
        // 在新建的反馈表中插入信息即可
        String id = request.getParameter("id");
        User user = userService.selectByUserId(Integer.valueOf(id));
        mv.addAttribute("user",user);
        // 返回到反馈成功界面
        return "user/advice";
    }

    // 处理反馈意见
    @RequestMapping("/postFeedback")
    public String getFeedback(HttpServletRequest request, Model mv){
        String id = request.getParameter("id");
        String advice  = request.getParameter("advice");
        mv.addAttribute("userId",id);
        // 新键一个表把反馈村进入
        return "user/adviceSuccess";
    }



}
