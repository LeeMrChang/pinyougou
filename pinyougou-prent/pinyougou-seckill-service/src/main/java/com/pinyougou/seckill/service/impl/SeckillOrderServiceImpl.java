package com.pinyougou.seckill.service.impl;

import java.util.Date;
import java.util.List;


import com.pinyougou.common.utils.IdWorker;
import com.pinyougou.common.utils.SysConstants;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;


import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillOrder;  





/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillOrderServiceImpl extends CoreServiceImpl<TbSeckillOrder>  implements SeckillOrderService {

	
	private TbSeckillOrderMapper seckillOrderMapper;

	@Autowired
	public SeckillOrderServiceImpl(TbSeckillOrderMapper seckillOrderMapper) {
		super(seckillOrderMapper, TbSeckillOrder.class);
		this.seckillOrderMapper=seckillOrderMapper;
	}

	
	

	
	@Override
    public PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbSeckillOrder> all = seckillOrderMapper.selectAll();
        PageInfo<TbSeckillOrder> info = new PageInfo<TbSeckillOrder>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeckillOrder> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize, TbSeckillOrder seckillOrder) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbSeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();

        if(seckillOrder!=null){			
						if(StringUtils.isNotBlank(seckillOrder.getUserId())){
				criteria.andLike("userId","%"+seckillOrder.getUserId()+"%");
				//criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getSellerId())){
				criteria.andLike("sellerId","%"+seckillOrder.getSellerId()+"%");
				//criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getStatus())){
				criteria.andLike("status","%"+seckillOrder.getStatus()+"%");
				//criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getReceiverAddress())){
				criteria.andLike("receiverAddress","%"+seckillOrder.getReceiverAddress()+"%");
				//criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getReceiverMobile())){
				criteria.andLike("receiverMobile","%"+seckillOrder.getReceiverMobile()+"%");
				//criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getReceiver())){
				criteria.andLike("receiver","%"+seckillOrder.getReceiver()+"%");
				//criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getTransactionId())){
				criteria.andLike("transactionId","%"+seckillOrder.getTransactionId()+"%");
				//criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
			}
	
		}
        List<TbSeckillOrder> all = seckillOrderMapper.selectByExample(example);
        PageInfo<TbSeckillOrder> info = new PageInfo<TbSeckillOrder>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeckillOrder> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

        @Autowired  //注入redis模板对象
        private RedisTemplate redisTemplate;

	    @Autowired  //输入秒杀商品对象
        private TbSeckillGoodsMapper seckillGoodsMapper;


    /**
     * 此方法用于创建用户抢到秒杀订单且将订单存进redis中的（预订单）
     *
     * @param id     秒杀列表的商品id
     * @param userId 抢到商品的用户id
     */
    @Override
    public void submitOrder(Long id, String userId) {

        //1.先从redis中根据秒杀商品的id获取秒杀商品的数据
        TbSeckillGoods seckillGoods = (TbSeckillGoods)
                redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).get(id);

        //2.判断 商品是否存在 如果商品不存在 或者库存为0 说明商品已经卖完
        if (seckillGoods == null || seckillGoods.getStockCount() <= 0) {
            //说明商品不存在了,或者已经没有库存了
            throw new RuntimeException("商品已经被抢光了");
        }

        //3.减库存,抢到，就减一个库存
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        //库存减少之后重新设置进redis中
        redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(id,seckillGoods);

        //4.判断 如果库存为0 更新到数据库中 删除redis中的商品
        if(seckillGoods.getStockCount() <= 0){ //如果被抢光
            //如果库存为0
            seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
            //删除redis中的商品
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).delete(id);
        }

        //5.创建 秒杀的预订单到redis中
        TbSeckillOrder seckillOrder = new TbSeckillOrder();



        seckillOrder.setId( new IdWorker(0,2).nextId());//设置订单的ID 这个就是out_trade_no
        seckillOrder.setCreateTime(new Date());//创建时间
        seckillOrder.setMoney(seckillGoods.getCostPrice());//秒杀价格  价格
        seckillOrder.setSeckillId(id);//秒杀商品的ID
        seckillOrder.setSellerId(seckillGoods.getSellerId());  //商家id
        seckillOrder.setUserId(userId);//设置用户ID
        seckillOrder.setStatus("0");//状态 未支付

        //将构建的订单保存到redis中  在秒杀订单中，userId就是唯一的标识
        redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).put(userId, seckillOrder);

    }


}
