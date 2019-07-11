package com.pinyougou.cart.controller;

import entity.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     *
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId,Integer num){

        return null;
    }
}
