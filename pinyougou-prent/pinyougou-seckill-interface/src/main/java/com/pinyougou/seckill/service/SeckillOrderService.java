package com.pinyougou.seckill.service;
import java.util.List;
import com.pinyougou.pojo.TbSeckillOrder;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillOrderService extends CoreService<TbSeckillOrder> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize, TbSeckillOrder SeckillOrder);

    /**
     *  此方法用于创建用户抢到秒杀订单且将订单存进redis中的（预订单）
     * @param id   秒杀列表的商品id
     * @param userId   抢到商品的用户id
     */
    void submitOrder(Long id, String userId);

}
