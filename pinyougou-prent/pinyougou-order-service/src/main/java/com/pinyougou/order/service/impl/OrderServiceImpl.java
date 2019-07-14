package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.utils.IdWorker;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @ClassName:OrderServiceImpl
 * @Author：Mr.lee
 * @DATE：2019/07/13
 * @TIME： 14:06
 * @Description: TODO
 */

@Service    //这里不需要集成coreService
public class OrderServiceImpl implements OrderService {

    @Autowired  //注入订单明细列表Mapper
    private TbOrderItemMapper orderItemMapper;

    @Autowired   //注入商品Mapper
    private TbItemMapper itemMapper;

      @Autowired  //注入redis模板对象
    private RedisTemplate redisTemplate;

    @Autowired
    private TbOrderMapper orderMapper;



    /**
     * 提交订单的方法
     *1.订单号不能重复
     * 2.拆单
     * @param tbOrder
     */
    @Override   //这个tbOrder是页面传过来的，是一个大的订单，需要进行拆弹支付
    public void add(TbOrder tbOrder) {

        //从redis中获取购物车列表
        List<Cart> cartList = (List<Cart>)
                redisTemplate.boundHashOps("Redis_CartList").get(tbOrder.getUserId());

        //遍历购物车列表先，这里就是拆单，每一个cart（购物车对象）就是一个订单
        for (Cart cart : cartList) {
            //1.获取订单的数据  插入到订单列表中
            //1.1使用雪花算法生成订单Id
            long orderId = new IdWorker(0, 1).nextId();
            System.out.println("");

            //新创建一个订单对象，拆单，拆单支付
            TbOrder order = new TbOrder();

            order.setOrderId(orderId);//订单ID

            order.setUserId(tbOrder.getUserId());//用户名
            order.setPaymentType(tbOrder.getPaymentType());//支付类型,页面选择，页面传递
            order.setStatus("1");//状态：未付款

            order.setCreateTime(new Date());//订单创建日期
            order.setUpdateTime(new Date());//订单更新日期

            order.setReceiverAreaName(tbOrder.getReceiverAreaName());//地址
            order.setReceiverMobile(tbOrder.getReceiverMobile());//手机号

            order.setReceiver(tbOrder.getReceiver());//收货人
            order.setSourceType(tbOrder.getSourceType());//订单来源
            order.setSellerId(cart.getSellerId());//商家ID

            //获取购物车明细列表的数据
            List<TbOrderItem> orderItemList = cart.getOrderItemList();

            //循环购物车明细
            double money=0;
            for (TbOrderItem orderItem : orderItemList) {
                //2.获取订单选项的数据  订单选项表
                long orderItemId = new IdWorker(0, 1).nextId();
                orderItem.setId(orderItemId);
                //订单ID
                orderItem.setOrderId(orderId);
                //商家id
                orderItem.setSellerId(cart.getSellerId());

                TbItem item = itemMapper.selectByPrimaryKey(orderItem.getItemId());//商品
                orderItem.setGoodsId(item.getGoodsId());//设置商品的SPU的ID

                //订单的金额累加
                money+=orderItem.getTotalFee().doubleValue();//金额累加
                orderItemMapper.insert(orderItem);
            }

            //最后设置支付金额
            order.setPayment(new BigDecimal(money));
            ////提交订单
            orderMapper.insert(order);
        }

            //这时，如果订单提交了，就需要把redis中的某一个用户的购物车订单清空
        redisTemplate.boundHashOps("Redis_CartList").delete(tbOrder.getUserId());


    }
}
