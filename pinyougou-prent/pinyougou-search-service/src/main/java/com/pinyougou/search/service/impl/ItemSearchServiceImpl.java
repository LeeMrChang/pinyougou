package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName:ItemSearchServiceImpl
 * @Author：Mr.lee
 * @DATE：2019/07/02
 * @TIME： 15:05
 * @Description: TODO
 */
@Service  //注入dubbo的远程注入
public class ItemSearchServiceImpl implements ItemSearchService {



    @Autowired   //注入全文搜索的模版实例化对象
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 根据输入的关键字查询对应的数据
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {



        Map<String,Object> resultMap = new HashMap<>();


        //1.获取关键字
        String keywords = (String) searchMap.get("keywords");

        //2.创建搜索查询对象 的构建对象
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();

        //3.创建并添加查询条件 匹配查询  先分词再查询
        searchQueryBuilder.withQuery(QueryBuilders.matchQuery("keyword",keywords));
        searchQueryBuilder.withIndices("pinyougou");  //根据pinyougou索引下去查询的

        //4.构建查询对象
        NativeSearchQuery searchQuery = searchQueryBuilder.build();

        //5.执行查询
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class);

        //6.获取结果集  返回
        List<TbItem> itemList = tbItems.getContent(); //存储到的当前页的当前数据 List<TbItem> itemList
        long totalElements = tbItems.getTotalElements();//总记录数
        int totalPages = tbItems.getTotalPages();//总页数
        resultMap.put("rows",itemList);
        resultMap.put("total",totalElements);
        resultMap.put("totalPages",totalPages);

        return resultMap;

    }
}
