package com.rabbiter.bms.web;

import com.rabbiter.bms.model.User;
import com.rabbiter.bms.service.UserService;
import com.rabbiter.bms.utils.MyResult;
import com.rabbiter.bms.utils.MyUtils;
import com.rabbiter.bms.utils.TokenProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    UserService userService;

    // 登录
    @RequestMapping(value = "/login")
    public Map<String, Object> login(@RequestBody User user) {
        // 登录
        User userObj = userService.login(user);
        if (userObj == null) {   // 账号或密码错误
            // 返回结果对象
            return MyResult.getResultMap(420, "账号或密码错误");
        } else {    // 账号密码正确
            // 创建token
            String token = TokenProcessor.getInstance().makeToken();
            // 保存到Redis
            userService.saveUser(token, userObj);
            // 返回结果对象
            return MyResult.getResultMap(200, "登录成功",
                    new HashMap<String, String>() {{
                        put("token", token);
                    }});
        }
    }

    // 查看用户信息
    @RequestMapping(value = "/info")
    public Map<String, Object> info(String token) {
        // 从redis中取用户
        User user = userService.getUser(token);
        if (user == null) {  // 获取失败
            return MyResult.getResultMap(420, "获取用户信息失败");
        } else {    // 获取成功
            return MyResult.getResultMap(200, "获取用户信息成功", user);
        }
    }

}