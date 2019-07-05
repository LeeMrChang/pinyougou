package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
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

	@Reference  //SPU
	private GoodsService goodsService;


	@Reference   //SKU
    private ItemSearchService itemSearchService;

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
		    //删除数据库
			goodsService.delete(ids);
			//获取被删除的SKU的数据


            //将这些SKU的数据从ES中移除掉，需要类似前面更新保存一般根据ids(多个id)进行删除
            //根据SPU的ID数组
            itemSearchService.deleteByIds(ids);




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
     *  //在这个方法中审核商品，更新状态的值，使用数据库与ES的数据同步
     * @param   ，使用数组接收
     * @param status  要修改的状态的参数
     * @return
     */
    @RequestMapping("/updateStatus")
    public Result updateStatus(@RequestParam String status,@RequestBody Long[] ids){

        try {
            goodsService.updateStatus( ids,status);

            //做一个判断。只能是审核通过的时候才进行更新保存的操作
            if("1".equals(status)){
                //1.获取被审核的先通过SPU  获取到SPU的商品的数据
                List<TbItem> itemList = goodsService.findTbItemListByIds(ids);



                //2.将被审核的SKU的商品 的数据 更新到ES中

                //2.1引入search的服务

                //2.2调用更新索引的方法,将更新后的数据传入到ES的索引中更新,此方法内部再做保存更新数据到ES服务中的操作
                itemSearchService.updateIndex(itemList);
            }


            return new Result(true,"更新成功！！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新失败！！");
        }
    }
}
