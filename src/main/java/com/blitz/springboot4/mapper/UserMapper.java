package com.blitz.springboot4.mapper;

import com.blitz.springboot4.entity.UserDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    @Select("SELECT STR_TO_DATE(REPLACE(SUBSTR(id,1,15), '_', ' '), '%Y%m%d %H%i%S') \n" +
            "FROM warehouse_items_newest \n" +
            "LIMIT 1;")
    String getDate();


    @Select(" select  count(*) from user where BINARY name = #{name} and BINARY  password = #{password}  and status = '0' ")
    int getUserName(String name,String password);

    @Select(" select * from user ")
    List<Map<String,Object>> getAllUsers();


    @Insert("INSERT INTO user (name, password, status, insert_time, update_time) " +
            "VALUES (#{username}, #{password}, #{status}, now(), now())")
    void addUser(Map<String, Object> map);

    @Update("update user set name = #{user.username}, password = #{user.password} , update_time = now() where id = #{userId}")
    void updateUser(@Param("userId") Long userId, @Param("user") Map<String, Object> user);

    @Update("update user set status = '1' where id = #{userId} ")
    void abandon(Long userId);
}
