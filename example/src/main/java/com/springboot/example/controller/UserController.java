package com.springboot.example.controller;

import com.springboot.example.common.Const;
import com.springboot.example.common.ResponseCode;
import com.springboot.example.common.ServerResponse;
import com.springboot.example.models.User;
import com.springboot.example.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Park on 2019-5-24.
 */
@Controller
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService userService;

    /**
     * 用户注册
     * @param user
     * @return
     */
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse register(User user){
        return userService.register(user);
    }

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse login(String username, String password , HttpSession session){
        ServerResponse response = userService.login(username,password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return  response;
    }

    /**
     * 检查用户名是否有效
     * @param string
     * @param type
     * @return
     */
    @RequestMapping(value = "check_valid.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse checkValid(String string , String type){
        return userService.checkValid(string,type);
    }

    /**
     * 获取当前登录用户信息
     * @param session
     * @return
     */
    @RequestMapping(value = "get_current_userInfo.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getCurrentUserInfo(HttpSession session){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(),"用户未登录,无法获取当前用户信息");
        }
        return ServerResponse.createBySuccess(ResponseCode.SUCCESS.getDesc(),currentUser);
    }

    /**
     * 忘记密码
     * @param username
     * @return
     */
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse forgetGetQuestion(String username){
        return  userService.forgetGetQuestion(username);
    }

    /**
     * 校验问题答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse forgetCheckAnswer(String username,String question,String answer){
        return userService.forgetCheckAnswer(username,question,answer);
    }

    /**
     * 忘记密码的重设密码
     * @param username
     * @param password
     * @param token
     * @param session
     * @return
     */
    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse forgetResetPassword(String username,String password,String token,HttpSession session){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(),"用户未登录");
        }
        return userService.forgetResetPassword(username,password,token);
    }
}
