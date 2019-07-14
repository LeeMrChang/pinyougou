package com.pinyougou.order.service;

import com.pinyougou.pojo.TbOrder;

/**
 * @ClassName:OrderService
 * @Author：Mr.lee
 * @DATE：2019/07/13
 * @TIME： 14:04
 * @Description: TODO
 */

//因为是订单提交，需要拆弹，这里不继承通用Mapper
public interface OrderService {

    /**
     * 提交订单的方法
     * @param tbOrder
     */
    void add(TbOrder tbOrder);
}
