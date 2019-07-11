package com.lee.demo.service;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @ClassName:UserDetailServiceImpl
 * @Author：Mr.lee
 * @DATE：2019/07/10
 * @TIME： 19:21
 * @Description: TODO
 */
public class UserDetailServiceImpl implements UserDetailsService {

    /**
     * 这是一个自定义授权类
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //1.获取数据库中的用户名对应的密码，进行校验
        //2.作用，就是只做授权  不认证，认证（登录）交给cas服务器
        //此用户被授权之后， <intercept-url pattern="/**" access="ROLE_USER"/>可访问所有首页路径

        System.out.println("Where's it going the UserDetailServiceImp?");

        return new User(username,"", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
    }
}
