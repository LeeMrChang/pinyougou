package com.pinyougou.search.service;

import java.util.Map;

/**
 * @ClassName:ItemSearchService
 * @Author：Mr.lee
 * @DATE：2019/07/02
 * @TIME： 15:05
 * @Description: TODO
 */
public interface ItemSearchService {

    /**
     * 根据输入的关键字查询对应的数据
     * @param searchMap
     * @return
     */
    Map<String, Object> search(Map<String, Object> searchMap);

}
