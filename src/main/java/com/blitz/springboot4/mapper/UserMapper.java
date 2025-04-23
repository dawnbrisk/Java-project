package com.blitz.springboot4.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT STR_TO_DATE(REPLACE(SUBSTR(id,1,15), '_', ' '), '%Y%m%d %H%i%S') \n" +
            "FROM warehouse_items_newest \n" +
            "LIMIT 1;")
    String getDate();


    @Select(" select  count(*) from user where BINARY name = #{name} and BINARY  password = #{password} ")
    int getUserName(String name,String password);
}
