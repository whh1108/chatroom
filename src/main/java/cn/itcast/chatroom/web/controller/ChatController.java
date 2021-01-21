package cn.itcast.chatroom.web.controller;


import cn.itcast.chatroom.entity.User;
import cn.itcast.chatroom.repository.UserRepository;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Controller
@RequestMapping("/chat")
public class ChatController {

    //加载MyBatis配置⽂文件
    InputStream inputStream =
            Resources.getResourceAsStream("config.xml");
    SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new
            SqlSessionFactoryBuilder();
    SqlSessionFactory sqlSessionFactory =
            sqlSessionFactoryBuilder.build(inputStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();
    UserRepository userRepository = sqlSession.getMapper(UserRepository.class);

    public ChatController() throws IOException {
    }

    @RequestMapping(value = "/loginpage",method = RequestMethod.GET)
    public ModelAndView loginpage(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }
    @RequestMapping("/registerfw")
    public ModelAndView registerfw(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        return modelAndView;
    }

    @RequestMapping(value = "/register")
    public ModelAndView register(User user){

        String id = UUID.randomUUID().toString();
        user.setId(id);
        //判断是否重名，不可以重名
        User userTmp = userRepository.findByUsername(user.getNickname());
        //此处无问题，重复确实可以判断
        if (userTmp != null){
            ModelAndView modelAndView = new ModelAndView();
            //跳转登录失败页面
            modelAndView.setViewName("regiserror");
            return modelAndView;
        }else {
            if(user.getNickname() != null){
                //整合了mybatis进行账号管理
                int rows = userRepository.save(user);
                if(rows > 0){
                    System. out.println("您成功插入了"+rows+"条数据！");
                }else{
                    System.out.println("执行插入操作失败！！！");
                }
                //不在此处进行close，后面应该进行close，否则会造成资源浪费
                sqlSession.commit();

            }

        }
//        System.out.println(user);
        ModelAndView modelAndView = new ModelAndView();
        //跳转到登录成功界面
        modelAndView.setViewName("regisright");
        return modelAndView;
    }

    @RequestMapping(value = "/login")
    public ModelAndView login(User user,HttpServletRequest request){
        HttpSession session = request.getSession();
        // 登录操作
        // 判断是否是一个已经登录的用户，没有则登录
        if (null != session.getAttribute("user")) {
            // 清除旧的用户
            session.removeAttribute("user");
        }
        //需要一个通过username来查询id，否则id为空会导致用户信息误判为系统信息
        String username = user.getNickname();
        String userId = userRepository.findIdByname(username);
        user.setId(userId);
//        System.out.println("user对象:"+userId);
        //登录判断，此处应后来引入数据校验
        int rows = userRepository.fCountByNameAndPw(user);
        if(rows > 0){
            System. out.println("登录成功");
            session.setAttribute("user", user);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("user",user);
            modelAndView.setViewName("chatroompage");
            return modelAndView;
        }else{
            System.out.println("登录失败！");
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("loginerror");
            return modelAndView;
        }


    }
}
