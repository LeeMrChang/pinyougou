package com.pinyougou.cart.service;

import entity.Cart;

import java.util.List;

/**
 * @ClassName:CartService
 * @Author：Mr.lee
 * @DATE：2019/07/11
 * @TIME： 20:34
 * @Description: TODO
 */
public interface CartService {
    /**
     * 向已有的购物车列表中，添加商品，返回一个新的购物车列表
     * @param cartList  旧的购物车列表
     * @param itemId    表示商品的规格选项ID
     * @param num   选中的商品数量
     * @return
     */

    List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);
}
