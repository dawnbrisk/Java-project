package com.blitz.springboot4.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

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
}
