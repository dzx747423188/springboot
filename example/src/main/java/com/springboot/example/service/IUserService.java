package com.springboot.example.service;

import com.springboot.example.common.ServerResponse;
import com.springboot.example.models.User;

import javax.servlet.http.HttpSession;

/**
 * Created by Park on 2019-5-24.
 */
public interface IUserService {
    /**
     * 用户注册
     * @param user
     * @return
     */
    public ServerResponse register(User user);

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    public  ServerResponse login(String username,String password);

    /**
     * 检查用户名是否有效
     * @param string
     * @param type
     * @return
     */
    public ServerResponse checkValid(String string, String type);

    /**
     * 忘记密码
     * @param username
     * @return
     */
    public ServerResponse forgetGetQuestion(String username);

    /**
     * 校验问题答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    public ServerResponse forgetCheckAnswer(String username,String question,String answer);

    /**
     * 重置密码
     * @param username
     * @param password
     * @param token
     * @return
     */
    public ServerResponse forgetResetPassword(String username, String password, String token);
}
