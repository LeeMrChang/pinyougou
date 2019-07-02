package com.pinyougou;

import com.lee.es.dao.ItemDao;
import com.lee.es.model.TbItem;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName:ElasticSearchTest
 * @Author：Mr.lee
 * @DATE：2019/07/01
 * @TIME： 22:20
 * @Description: TODO
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:spring-es.xml")
public class ElasticSearchTest {

    @Autowired  //注入搜索对象
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired  //注入dao接口方法
    private ItemDao itemDao;

    /**
     * 创建索引和映射
     */
    @Test
    public void testCreateIndexAndMapping() {
        //创建索引
        elasticsearchTemplate.createIndex(TbItem.class);
        //创建映射
        elasticsearchTemplate.putMapping(TbItem.class);
    }

    //创建文档
    @Test
    public void test01() {


        TbItem tbitem = new TbItem();
        tbitem.setId(20000L);
        tbitem.setTitle("华为手机");
        tbitem.setBrand("华为");
        tbitem.setGoodsId(1L);
        tbitem.setSeller("神峨眉");
        tbitem.setCategory("手机");
        itemDao.save(tbitem);


    }

    //创建文档，，使用对象域创建
    @Test
    public void test08() {

        TbItem tbitem = new TbItem();
        tbitem.setId(20000L);
        tbitem.setTitle("华为手机");
        tbitem.setBrand("华为");
        tbitem.setGoodsId(1L);
        tbitem.setSeller("神峨眉");
        tbitem.setCategory("手机");
        //map就是创建出来的对象域
        Map<String, String> map = new HashMap<>();
        map.put("网络制式", "移动4G");
        map.put("机身内存", "128G");
        tbitem.setSpecMap(map);
        itemDao.save(tbitem);

    }


    //更新文档
    @Test
    public void test02() {
        TbItem tbitem = new TbItem();
        tbitem.setId(20000L);
        tbitem.setTitle("华为手机");
        tbitem.setBrand("华为");
        tbitem.setGoodsId(1L);
        tbitem.setSeller("华为旗舰店");
        tbitem.setCategory("手机");
        itemDao.save(tbitem);
    }


    //删除文档
    @Test
    public void deleteById() {
        itemDao.deleteById(20000L);
    }

    //查询    查询所有
    @Test
    public void QueryAll() {
        Iterable<TbItem> all = itemDao.findAll();
        for (TbItem tbItem : all) {
            System.out.println(tbItem);
        }
    }

    //分页查询
    @Test
    public void test30() {


        //第一个参数代表页码
        //第二个参数代表每页显示的行
        Pageable pageable = PageRequest.of(0, 10);
        Page<TbItem> all = itemDao.findAll(pageable);
        System.out.println("总页数：" + all.getTotalPages());
        List<TbItem> content = all.getContent();
        System.out.println(content);

        for (TbItem tbItem : all) {
            System.out.println(tbItem);
        }

    }

    //更新文档
    @Test
    public void test012() {

        for (long i = 0; i < 100; i++) {
            TbItem tbitem = new TbItem();
            tbitem.setId(3000 + i);
            tbitem.setTitle("华为手机" + i);
            tbitem.setBrand("华为" + i);
            tbitem.setGoodsId(1L);
            tbitem.setSeller("华为旗舰店" + i);
            tbitem.setCategory("手机" + i);
            itemDao.save(tbitem);
        }

    }

    /**
     * 通配符查询   例如:手机、手表、手套，类似带手的模糊查询
     * 手?与手*的区别   ?问好代表一定占用一个通配符，*不一定占用，可占用可不占用
     * 不分词查询，按照通配符
     */
    @Test
    public void queryBywildcarQuery() {
        //1.创建查询对象
        SearchQuery query = new NativeSearchQuery(QueryBuilders.wildcardQuery("title", "华为手?"));
        //2.设置搜索查询的条件
        //QueryBuilders.wildcardQuery("title","手机")这就是在设置查询的条件，从title里面查，包含手机的查询

        //3.执行查询
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(query, TbItem.class);

        //4.获取结果
        int totalPages = tbItems.getTotalPages();
        System.out.println("总页数：" + totalPages);

        long totalElements = tbItems.getTotalElements();
        System.out.println("总记录数：" + totalElements);

        List<TbItem> content = tbItems.getContent();//查询到的数据集

        System.out.println("查询到的集合：" + content);
    }

    //按照先 分词，再进行查询
    @Test
    public void mathQuery() {

        //keyword:copyTo = "keyword": 作用：将多个字段的值copy到一个指定字段中，在搜索的时候可以根据指定字段来搜索
        //效率比多个字段要高，并且达到的效果是一样的,高亮显示不能使用copyTo

        //1.创建查询对象
        SearchQuery searchQuery = new NativeSearchQuery(QueryBuilders.matchQuery("keyword", "手机"));
        //2.设置查询条件

        //QueryBuilders.matchQuery("title","华为手机")
        //3.执行查询
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class);

        //4.获取结果
        int totalPages = tbItems.getTotalPages();
        System.out.println("总页数：" + totalPages);

        long totalElements = tbItems.getTotalElements();
        System.out.println("总记录数：" + totalElements);

        List<TbItem> content = tbItems.getContent();//查询到的数据集

        System.out.println("查询到的集合：" + content);

    }

    //查询机身内存为128G  的手机
    @Test
    public void ByObj() {

        //1.创建搜索对象
        SearchQuery searchQuery =  new NativeSearchQuery(QueryBuilders.matchQuery("specMap.机身内存.keyword","128G"));
        //2.设置查询条件
       // "specMap.机身内存.keyword","128G"   specMap.机身内存.都表示属性，keyword是固定写法
        //keyword字符段类型都是不分词的查询的
        //3.执行查询
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class);

        //4.获取结果
        int totalPages = tbItems.getTotalPages();
        System.out.println("总页数：" + totalPages);

        long totalElements = tbItems.getTotalElements();
        System.out.println("总记录数：" + totalElements);

        List<TbItem> content = tbItems.getContent();//查询到的数据集

        System.out.println("查询到的集合：" + content);
    }

    /**
     * 多条件组个查询，多个条件必须满足一起查询
     * 查询 title为 华为 的数据
     * 查询 网络制式 为 移动4G的数据
     *
     */
    @Test
    public void boolQuery(){

        //1.创建一个查询对象，构建对象，因为条件比较多，所以创建构建者对象，来封装根据多个条件查询
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();

        //2.设置查询的条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //must,代表必须满足条件的查询
        //must_NOT  必须不满足条件的查询、
        //SHOULD  表示应该满足条件的查询
        //filter  过滤条件的查询

        //必须满足某一个条件 查询title 为华为的数据、  条件1
        boolQueryBuilder.filter(QueryBuilders.matchQuery("title","华为"));

        //必须满足某一个条件为 网络制式 移动4G的数据   条件2
        //termQuery 表示精确查询，不分词 的
        boolQueryBuilder.filter(QueryBuilders.termQuery("specMap.网络制式.keyword","移动4G"));

        //从pinyougou的索引对象中查询
        builder.withIndices("pinyougou");
        //返回的类型就是item的pojo类型
        builder.withTypes("item");

        //查询的条件，组合在一起封装
        builder.withQuery(boolQueryBuilder);

        //3.构建查询对象
        NativeSearchQuery build = builder.build();

        //4.执行查询
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(build, TbItem.class);

        //5.获取结果
        int totalPages = tbItems.getTotalPages();
        System.out.println("总页数：" + totalPages);

        long totalElements = tbItems.getTotalElements();
        System.out.println("总记录数：" + totalElements);

        List<TbItem> content = tbItems.getContent();//查询到的数据集

        System.out.println("查询到的集合：" + content);
    }
}
