package com.qjr.demo.config.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//需要认证的API被调用前执行的拦截器
public class TokenFilter extends AuthenticationFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        String token = getToken(servletRequest);
        if (StringUtils.isEmpty(token)) {
            return false;
        } else {
            boolean isSuccess = this.login(token);
            if (!isSuccess) {
                this.printUnauthorized("401", (HttpServletResponse) servletResponse);
            }
            return isSuccess;
        }
    }

    private boolean login(String token) {
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.login(new Token(token));//subject.login（） 调用 自定义的 TokenRealm 对象，进行认证和授权。
            return true;
        } catch (AuthenticationException e) {
//            e.printStackTrace();
            return false;
        }

    }

    private String getToken(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String authorizationHeader = request.getHeader("token");//获取请求头中的Authorization属性
        if (!StringUtils.isEmpty(authorizationHeader)) {
            return authorizationHeader.replace(" ", "");
        }
        return null;
    }

    private void printUnauthorized(String messageCode, HttpServletResponse response) {
        String content = String.format("{\"code\":\"%s\",\"msg\":\"%s\"}", messageCode, HttpStatus.UNAUTHORIZED.getReasonPhrase());
        response.setContentType("application/json;charset=UTF-8");
        response.setContentLength(content.length());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        try {
            PrintWriter writer = response.getWriter();
            writer.write(content);
        } catch (IOException var5) {
            var5.printStackTrace();
        }

    }
}
