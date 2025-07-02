package com.blitz.springboot4.mapper;


import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface SpiderMapper {


    @Insert("""
        insert ignore into spider_result (code_level,item_code,page) values (#{codeLevel},#{itemCode},#{totalPage})
    """)
    void saveToDatabase(String codeLevel, String itemCode,int totalPage);

    @Select("""
    select distinct item_code from spider_result where is_finish is null
    """)
    List<String> getItemCodes();


    @Update("""
    update spider_result set  main_sku =#{mainSku},product_name = #{productName} ,is_finish = #{isFinish} , crawl_time = now() where item_code = #{code}
    """)
    void updateToDatabase(String mainSku,String code,String productName,String isFinish);

    @Insert("""
    insert ignore into spider_sub_sku (main_sku,sub_sku,qty) values (#{mainSku},#{subSku},#{qty})
    """)
    void insertSubSku(String mainSku,String subSku,String qty);


    @Select("""
        select distinct category_id from spider_category_ids group by category_id
    """)
    List<String> getCategoryIds();

    @Select("""
            SELECT  SKU ,count(*) as qty  from retour GROUP BY sku
            """)
    List<Map<String,Object>> getRetour();


    @Select("""
    SELECT   * from spider_sub_sku
    """)
    List<Map<String,Object>> getSubSku();


    @Insert("""
        insert into compare_result (main_sku,qty, sub_sku,required_qty,is_set) values (#{mainSku},#{qty},#{subSKU},#{requiredQty},'Y')
    """)
    void insertCompareResult(String mainSku,int qty,String subSKU,int requiredQty);

    @Delete(" delete from compare_result")
    void deleteCompareResult();

    @Update(" update retour set is_used = 'Y' where is_used is null and sku = #{sku} limit #{qtyLimit}")
    void  updateIsUsed(String sku,int qtyLimit);
}
