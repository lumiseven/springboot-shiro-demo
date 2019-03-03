//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.qjr.demo.config.shiro;

import org.apache.shiro.authc.AuthenticationToken;

//自定义Token 实现AuthenticationToken 认证是需要用到
public class Token implements AuthenticationToken {
    private String token;

    Token(String token) {
        this.token = token;
    }

    String getToken() {
        return this.token;
    }

    @Override
    public Object getPrincipal() {
        return this.getToken();
    }

    @Override
    public Object getCredentials() {
        return this.getToken();
    }

}
