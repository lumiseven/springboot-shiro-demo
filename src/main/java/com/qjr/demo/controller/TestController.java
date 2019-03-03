package com.qjr.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/test")
@Api(tags = "测试模块")
public class TestController {

    @Resource(name = "stringRedisTemplate")
    private ValueOperations<String, String> redis;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private String[] tokens = {"token1", "token2", "token3"};

    private String[] roles = {"admin", "user"};

    @ApiOperation("测试")
    @GetMapping("/getAdmin")
    @RequiresRoles(logical = Logical.OR, value = {"admin"})//shiro注解 admin角色
    public String getAdmin() {
        return "Admin测试成功";
    }

    @ApiOperation("测试")
    @GetMapping("/getUser")
    @RequiresRoles(logical = Logical.OR, value = {"user"})//shiro注解 user角色
    public String getUser() {
        return "User测试成功";
    }

    @ApiOperation("登录")
    @PostMapping("/login")
    public Map<String, String> login(@RequestParam @ApiParam String name, @RequestParam @ApiParam String password) {
        String token = "";
        String role = "";
        if (name.equals("user") && password.equals("123456")) {
            Random random = new Random();
            token = tokens[random.nextInt(tokens.length)];//随机token ，开发项目可用随机字符替代
            role = roles[random.nextInt(roles.length)];//随机角色
            redis.set("random.token." + token, role); //将token和token对应的role存入 Redis中
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            map.put("role", role);
            return map;
        }
        return null;
    }
}
