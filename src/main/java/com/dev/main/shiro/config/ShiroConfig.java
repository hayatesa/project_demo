package com.dev.main.shiro.config;

import com.dev.main.shiro.redis.ShiroRedisCacheManager;
import com.dev.main.shiro.redis.ShiroRedisSessionDAO;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Bean(name = "securityManager")
    public SecurityManager securityManager(RedisTemplate redisTemplate) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        // 自定义Realm
        // securityManager.setRealm(exampleRealm());

        securityManager.setSessionManager(sessionManager(redisTemplate));

        // 自定义缓存实现，使用redis
        securityManager.setCacheManager(redisCacheManager(redisTemplate));

        // 注入记住我管理器
        securityManager.setRememberMeManager(rememberMeManager());
        return securityManager;
    }

    /**
     * cookie管理对象;记住我功能
     */
    @Bean
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
        cookieRememberMeManager.setCipherKey(Base64.decode("3AvVhmFLUs0KTA3Kprsdag=="));
        return cookieRememberMeManager;
    }

    @Bean(name = "ShiroRedisCacheManager")
    public ShiroRedisCacheManager redisCacheManager(RedisTemplate redisTemplate) {
        ShiroRedisCacheManager redisCacheManager = new ShiroRedisCacheManager(redisTemplate);
        redisCacheManager.createCache("shiro_redis");
        return redisCacheManager;
    }

    @Bean(name = "sessionManager")
    @DependsOn(value = "lifecycleBeanPostProcessor")
    public SessionManager sessionManager(RedisTemplate redisTemplate) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        ShiroRedisSessionDAO redisSessionDao = new ShiroRedisSessionDAO(redisTemplate);
        //这个name的作用也不大，只是有特色的cookie的名称。
        // redisSessionDao.setSessionIdGenerator(sessionIdGenerator("starrkCookie"));
        sessionManager.setSessionDAO(redisSessionDao);
        sessionManager.setDeleteInvalidSessions(true);
        SimpleCookie cookie = new SimpleCookie();
        cookie.setName("starrkCookie");
        sessionManager.setSessionIdCookie(cookie);
        sessionManager.setSessionIdCookieEnabled(true);
        return sessionManager;
    }

    /**
     * 自定义的SessionId生成器
     * @param name
     * @return
     */
   /* public MySessionIdGenerator sessionIdGenerator(String name) {
        return new MySessionIdGenerator(name);
    }*/

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, RedisTemplate redisTemplate) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 配置拦截器的跳转路径
        shiroFilterFactoryBean.setLoginUrl("/login.html");
        // 配置登录成功后跳转页面
        shiroFilterFactoryBean.setSuccessUrl("/index.html");
        // 配置权限不足时的跳转页面
        shiroFilterFactoryBean.setUnauthorizedUrl("/403.html");

        // 过滤器链
        Map<String, Filter> filters = new LinkedHashMap<>();
        // filters.put("example", null);
        shiroFilterFactoryBean.setFilters(filters);

        // 权限映射链
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/403.html", "anon");
        filterChainDefinitionMap.put("/login.html", "anon");
        filterChainDefinitionMap.put("/logout/html", "logout");
        filterChainDefinitionMap.put("/register.html", "anon");
        // filterChainDefinitionMap.put("/**/example/**", "example[user]");
        // filterChainDefinitionMap.put("/example.html", "authc,roles[role]");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    // 生命周期
    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();

    }

    /**
     * 这个参数是RememberMecookie的名称，随便起。
     * remenberMeCookie是一个实现了将用户名保存在客户端的一个cookie，与登陆时的cookie是两个simpleCookie。
     * 登陆时会根据权限去匹配，如是user权限，则不会先去认证模块认证，而是先去搜索cookie中是否有rememberMeCookie，
     * 如果存在该cookie，则可以绕过认证模块，直接寻找授权模块获取角色权限信息。
     * 如果权限是authc,则仍会跳转到登陆页面去进行登陆认证.
     * @return
     */
    public SimpleCookie rememberMeCookie() {
        SimpleCookie simpleCookie = new SimpleCookie("remenbermeCookie");
        //<!-- 记住我cookie生效时间30天 ,单位秒;-->
        simpleCookie.setMaxAge(60);
        return simpleCookie;
    }


}
