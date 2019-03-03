package com.qjr.demo.config.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

//自定义域对象，实现认证和授权的方法
public class TokenRealm extends AuthorizingRealm {

    @Resource(name = "stringRedisTemplate")
    private ValueOperations<String, String> redis;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public String getName() {
        return "Realm";
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token != null && Token.class.isAssignableFrom(token.getClass());
    }

    //认证 Authentication
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("认证 Authentication");
        Token token = (Token) authenticationToken;
        String role = redis.get("random.token." + token.getToken());//获取对应token的角色
        if (!StringUtils.isEmpty(role)) {
            return new SimpleAuthenticationInfo(role, token.getToken(), this.getName());
        }
        return null;
    }

    //授权 Authorization
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
        System.out.println("授权 Authorization");
        String role = (String) principal.getPrimaryPrincipal();
        if (!StringUtils.isEmpty(role)) {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            info.addRole(role);
            return info;
        }
        return null;
    }
}
