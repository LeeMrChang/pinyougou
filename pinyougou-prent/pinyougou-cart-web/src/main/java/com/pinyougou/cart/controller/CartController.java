package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.utils.CookieUtil;
import entity.Cart;
import entity.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName:CartController
 * @Author：Mr.lee
 * @DATE：2019/07/11
 * @TIME： 11:28
 * @Description: TODO
 */

@RestController
@RequestMapping("/cart")
public class CartController {

    //使用dubbo框架远程注入一个cartService
    @Reference
    private CartService cartService;

    /**
     *添加商品到已有的购物车列表中去
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num,
                                     HttpServletRequest request,
                                     HttpServletResponse response){

        //根据spring-security框架获取用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(name);

        //判断此名称不为空的情况 anonymousUser表示匿名用户
        if ("anonymousUser".equals(name)) {
            //如果没有登录
            System.out.println("Not login!");

            //1.1从匿名点击收藏商品到购物车中Cookie中获取购物车列表，这里是去cookie
            String cartListStr = CookieUtil.getCookieValue(request, "cartList", true);

            //判断cookie如果为空
            if(StringUtils.isEmpty(cartListStr)){
                    cartListStr = "[]";  //表示一个空的 new ArrayList集合，然后将cookie的值付给这个集合
            }

            //需要JSON字符串转为Array集合的JSON对象
            List<Cart> cartList = JSON.parseArray(cartListStr, Cart.class);

            //1.2 向已有的购物车列表中添加商品，返回一个最新的购物车列表
            List<Cart> cartListNew = cartService.addGoodsToCartList(cartList,itemId,num);
            //再判断如果购物车列表为空
            if(cartListNew==null){
                //则将购物车列表 也是 new ArrayList
                cartListNew = new ArrayList<>();
            }

            //1.3 将最新的购物车列表数据重新写入到Cookie中,存cookie
            CookieUtil.setCookie(request,response,
                    "cartList",
                    JSON.toJSONString(cartListNew),
                    7*3600*24,
                    true
                    );

        }else {
            //如果登录
            System.out.println("This is login!");

            //2.1从Redis中获取购物车列表

            //2.2 向已有的购物车列表中添加商品，返回一个最新的购物车列表

            //2.3将最新的购物车列表数据重新写入到Redis中

        }

        return null;
    }
}
