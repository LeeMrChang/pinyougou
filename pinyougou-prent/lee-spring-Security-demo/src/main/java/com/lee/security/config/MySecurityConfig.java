package com.lee.security.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @ClassName:MySecurityConfig
 * @Author：Mr.lee
 * @DATE：2019/06/25
 * @TIME： 21:58
 * @Description: TODO
 */
//该类就可以代替  spring-scurity里面的security的配置项
@EnableWebSecurity   //开启security自动的配置，说明这是一个配置类
public class MySecurityConfig extends WebSecurityConfigurerAdapter {

    //配置认证  Builder构建的意思
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //定义了一个用户名 admin  密码 admin 并且拥有ADMIN角色的用户
        //角色的ROLE_下划线不要写，默认会拼上大的
        auth.inMemoryAuthentication().withUser("admin").password("{noop}admin").roles("ADMIN");
    }

    //类里面配置授权
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //自定义一个拦截的规则
        http.authorizeRequests()

                //放行登录页面或者静态资源，允许所有人访问一下的页面
                .antMatchers("/login.html").permitAll()

                //指定  /admin/** 下的所有的请求，只有拥有ADMIN角色的人才可以访问
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasRole("USER")

                //表示任意的请求  如果该用户(已经登录过的人才能访问) 认证过了就可以访问
                .anyRequest().authenticated();

            //自定义登录页面
        http.formLogin()

                .loginPage("/login.html")  //指定登录页面
                .loginProcessingUrl("/login")  //响应前端发送请求的post路径
                .defaultSuccessUrl("/index.jsp",true)  //默认跳转，true表示总是跳到这个页面
                .failureUrl("/login.html?error");   //登录失败的跳转路径

        //禁用Csrf
        http.csrf().disable();


       // super.configure(http);
    }



    //1.配置spring-scurity.xml 组件扫描
    //2.java 类，配置类  继承 抽象类 加上 开启自动配置的安全注解、@EnableWebSecurity
    //3.Java类中的配置，授权 和 认证

}
