package com.springboot.example.service.impl;

import com.springboot.example.common.Const;
import com.springboot.example.common.ResponseCode;
import com.springboot.example.common.ServerResponse;
import com.springboot.example.common.TokenCache;
import com.springboot.example.dao.UserMapper;
import com.springboot.example.models.User;
import com.springboot.example.service.IUserService;
import com.springboot.example.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by Park on 2019-5-24.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse register(User user) {
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(),"注册用户不允许为空！");
        }
        //根据用户名验证
        String username = user.getUsername();
        int rowCount = userMapper.checkByUsername(username);
        if(rowCount>0){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(),"用户已被注册！");
        }
        //根据邮箱校验
        String email = user.getEmail();
        rowCount = userMapper.checkByEmail(email);
        if(rowCount>0){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(),"邮箱已被注册！");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        user.setRole(Const.role.ROLE_CUSTOMER);
        rowCount = userMapper.insert(user);
        if(rowCount == 0){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(),"注册失败！");
        }
        return ServerResponse.createBySuccessCodeMessage(ResponseCode.SUCCESS.getCode(),"注册成功！");
    }

    @Override
    public ServerResponse login(String username, String password) {
        if(StringUtils.isBlank(username)){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(),"用户名不允许为空");
        }
        if(StringUtils.isBlank(password)){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(),"密码不允许为空");
        }
        int rowCount = userMapper.checkByUsername(username);
        if(rowCount == 0){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(),"用户名不存在");
        }
        User user = userMapper.selectByUsernameAndPassword(username,MD5Util.MD5EncodeUtf8(password));
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(),"密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

    @Override
    public ServerResponse checkValid(String string, String type) {
        if (StringUtils.isBlank(type) || StringUtils.isBlank(string)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(), "检验参数不允许为空");
        }
        int rowCount;
        if (Const.USERNAME.equals(type)) {
            rowCount = userMapper.checkByUsername(string);
            if (rowCount > 0) {
                return ServerResponse.createBySuccessCodeMessage(ResponseCode.SUCCESS.getCode(), "用户:" + string + "已存在");
            }
        } else if (Const.EMAIL.equals(type)) {
            rowCount = userMapper.checkByEmail(string);
            if (rowCount > 0) {
                return ServerResponse.createBySuccessCodeMessage(ResponseCode.SUCCESS.getCode(), "邮箱:" + string + "已存在");
            }
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(), "检验参数错误!");
    }

    @Override
    public ServerResponse forgetGetQuestion(String username) {
        if(StringUtils.isBlank(username)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(),"用户名不允许为空");
        }
        int rowCount = userMapper.checkByUsername(username);
        if(rowCount == 0){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(),"用户不存在");
        }
        User user = userMapper.forgetGetQuestion(username);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(),"用户不存在");
        }
        String question = user.getQuestion();
        if(StringUtils.isBlank(question)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(),"该用户没有设置找回密码的问题");
        }
        return ServerResponse.createBySuccess(ResponseCode.SUCCESS.getDesc(),question);
    }

    @Override
    public ServerResponse forgetCheckAnswer(String username, String question, String answer) {
        if(StringUtils.isBlank(username)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(),"用户名不允许为空");
        }
        int rowCount = userMapper.checkByUsername(username);
        if(rowCount == 0){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(),"用户不存在");
        }
        rowCount = userMapper.forgetCheckAnswer(username,question,answer);
        if(rowCount == 0){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(),"问题答案错误");
        }
        String token = UUID.randomUUID().toString();
        TokenCache.setKey("token_"+username,token);
        return ServerResponse.createBySuccessCodeMessage(ResponseCode.SUCCESS.getCode(),token);
    }

    @Override
    public ServerResponse forgetResetPassword(String username, String password, String token) {
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(), "参数错误，token不允许为空");
        }
        ServerResponse response = this.checkValid(username, Const.USERNAME);
        if (!response.isSuccess()) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(), "用户不存在");
        }
        String newToken = TokenCache.getKey("token_" + username);
        if (StringUtils.isBlank(newToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(), "token值无效或已过期");
        }
        if (StringUtils.equals(token, newToken)) {
            String MD5Password = MD5Util.MD5EncodeUtf8(password);
            int rowCount = userMapper.updatePasswordByUsername(username, MD5Password);
            if (rowCount > 0) {
                return ServerResponse.createBySuccessCodeMessage(ResponseCode.SUCCESS.getCode(), "密码修改成功");
            }
        } else {
                return ServerResponse.createByErrorMessage("token错误,请重新获取重置密码的Token!");
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(), "密码修改失败");
    }
}
