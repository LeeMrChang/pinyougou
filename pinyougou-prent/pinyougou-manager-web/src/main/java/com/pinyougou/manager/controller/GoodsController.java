package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.Goods;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return goodsService.findPage(pageNo, pageSize);
    }
	
	/**添加商品，添加商品需要涉及到商品的名称（商品表）
     * 涉及到商品的描述
     * 涉及商品的SKu
     * 所以给入的参数需要是三个表的结合对象pojo  Goods
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
		    //1.获取到商家的ID
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            //2.商家ID在做商品添加时需要，将商家ID添加进goods中，goods是组合对象
            goods.getGoods().setSellerId(sellerId);

            goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改  组合对象的修改，需改动成Goods
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
     * 根据id获取整个组合对象的数据信息
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne/{id}")
    public Goods findOne(@PathVariable(value = "id") Long id){
	    //根据id查询获取整合组合对象的数据信息
        return goodsService.findOne(id);
    }

    /*逻辑删除：逻辑删除的点击按钮，逻辑删除，数据不能根本地删除某个字段的值，只能修改某个字段的状态
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			goodsService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/search")
    public PageInfo<TbGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbGoods goods) {

	    //添加一个商家的条件，根据商家的名称查询显示
       // goods.setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());

        return goodsService.findPage(pageNo, pageSize, goods);
    }


    //需要创建一个新的修改方法，批量修改商品的状态，根据商品id

    /**
     *
     * @param   ，使用数组接收
     * @param status  要修改的状态的参数
     * @return
     */
    @RequestMapping("/updateStatus")
    public Result updateStatus(@RequestParam String status,@RequestBody Long[] ids){

        try {
            goodsService.updateStatus( ids,status);
            return new Result(true,"更新成功！！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新失败！！");
        }
    }
}
