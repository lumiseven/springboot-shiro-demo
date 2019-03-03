package com.qjr.demo.config.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.*;

@Configuration
public class ShiroConfig {

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);//设置 securityManager
        Map<String, String> filterChainDefinitionMapping = shiroFilter.getFilterChainDefinitionMap();
        setUrl(filterChainDefinitionMapping, "anon", AnonUrl());//不需要拦截的路径
        setUrl(filterChainDefinitionMapping, "method,auth", AuthUrl());//需要被拦截的路径 这里的 method、auth 和下面的拦截器对应

        Map<String, Filter> filterMap = new HashMap<>();
        //MethodFilter TokenFilter 自定义拦截器
        filterMap.put("method", new MethodFilter());// cors自己随便定义，写abc也行 个人喜欢
        filterMap.put("auth", new TokenFilter());// auth自己随便定义，写abc也行 个人喜欢
        shiroFilter.setFilters(filterMap);
        return shiroFilter;
    }

    private void setUrl(Map<String, String> filterChainDefinitionMapping, String filterName, List<String> urlList) {
        if (!urlList.isEmpty()) {
            Iterator<String> iterator = urlList.iterator();
            while (iterator.hasNext()) {
                String url = iterator.next();
                filterChainDefinitionMapping.put(url, filterName);//
            }
        }
    }

    //不对swagger访问路径拦截
    private List<String> AnonUrl() {
        List<String> list = new ArrayList<>();
        list.add("/swagger*");
        list.add("/api/test/login");
        return list;
    }

    //对拦截 /api/** 的API进行拦截
    private List<String> AuthUrl() {
        List<String> list = new ArrayList<>();
        list.add("/api/**");
        return list;
    }


    @Bean("securityManager")
    public SecurityManager securityManager(TokenRealm tokenRealm) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(tokenRealm);//设置域对象
        DefaultSubjectDAO de = (DefaultSubjectDAO) manager.getSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = (DefaultSessionStorageEvaluator) de.getSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);//禁用Session存储
        StatelessDefaultSubjectFactory statelessDefaultSubjectFactory = new StatelessDefaultSubjectFactory();
        manager.setSubjectFactory(statelessDefaultSubjectFactory);
        manager.setSessionManager(this.defaultSessionManager());
        SecurityUtils.setSecurityManager(manager);
        return manager;
    }

    @Bean("tokenRealm")
    public TokenRealm tokenRealm() {
        return new TokenRealm();
    }

    @Bean
    public DefaultSessionManager defaultSessionManager() {
        DefaultSessionManager manager = new DefaultSessionManager();
        manager.setSessionValidationSchedulerEnabled(false);//禁用Session
        return manager;
    }

    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public AuthorizationAttributeSourceAdvisor advisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);

        return authorizationAttributeSourceAdvisor;
    }

    //启用Shiro注解
    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
}
