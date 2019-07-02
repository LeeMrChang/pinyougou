package com.lee.es.dao;

import com.lee.es.model.TbItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

/**
 * @ClassName:ItemDao
 * @Author：Mr.lee
 * @DATE：2019/07/01
 * @TIME： 22:30
 * @Description: TODO
 */
public interface ItemDao extends ElasticsearchCrudRepository<TbItem,Long> {

    //继承的这一个接口，里面有很多基本的增删查改的方法
}
