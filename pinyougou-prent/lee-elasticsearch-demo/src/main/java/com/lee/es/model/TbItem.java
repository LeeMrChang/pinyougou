package com.lee.es.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Map;

/**
 * @ClassName:TbItem
 * @Author：Mr.lee
 * @DATE：2019/07/01
 * @TIME： 22:01
 * @Description: TODO
 */


@Document(indexName = "pinyougou",type="item")
public class TbItem implements Serializable {

    @Override
    public String toString() {
        return "TbItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", goodsId=" + goodsId +
                ", category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", seller='" + seller + '\'' +
                '}';
    }



    //要索引 对象类型   对象域查询
    @Field(index = true,type=FieldType.Object)
    private Map<String,String> specMap;

    public Map<String, String> getSpecMap() {
        return specMap;
    }

    public void setSpecMap(Map<String, String> specMap) {
        this.specMap = specMap;
    }

    /**
     * 商品id，同时也是商品编号
     */
    @Id//文档唯一的ID  标识该字段为文档的唯一标识
    @Field(type = FieldType.Long) //标识 该数据也要作为字段进行展示
    private Long id;

    /**
     * 商品标题
     * index:索引，根据索引查文本
     * analyzer：分词器
     * searchAnalyzer：搜索时的分词器
     * type：返回值类型：text 文本类型， id 一般是FieldType.Long，keyword
     * copyTo = "keyword": 作用：将多个字段的值copy到一个指定字段中，在搜索的时候可以根据指定字段来搜索
     * 效率比多个字段要高，并且达到的效果是一样的
     */
    @Field(index = true,analyzer = "ik_smart", searchAnalyzer = "ik_smart",type = FieldType.Text,copyTo = "keyword")
    private String title;


    @Field(type = FieldType.Long)
    private Long goodsId;

    /**
     * 冗余字段 存放三级分类名称  关键字 只能按照确切的词来搜索
     * 不能分词
     */
    @Field(type = FieldType.Keyword,copyTo = "keyword")
    private String category;

    /**
     * 冗余字段 存放品牌名称
     */
    @Field (type = FieldType.Keyword,copyTo = "keyword")
    private String brand;

    /**
     * 冗余字段，用于存放商家的店铺名称
     */
    @Field(type = FieldType.Keyword,copyTo = "keyword")
    private String seller;
    //getter和setter


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }
}
