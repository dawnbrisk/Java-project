package com.blitz.springboot4.mapper;


import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface MergeMapper  {


    @Select("SELECT b.Item_Code, b.pallet_Qty, a.Current_Inventory_Qty, pallet_code, a.location_code, pallet_details " +
            "FROM (SELECT Item_Code, count(Pallet_Code) AS pallet_qty, " +
            "GROUP_CONCAT(CONCAT('*', SUBSTRING(Pallet_Code, 9), '+', Current_Inventory_Qty, '+', LEFT(Location_Code, LENGTH(Location_Code) - 2)) ORDER BY Pallet_Code SEPARATOR ', ') AS pallet_details " +
            "FROM warehouse_items_newest " +
            "WHERE LEFT(Location_Code, 1) in ('A','C') AND Location_Code != 'A-03-01-1' " +
            "GROUP BY Item_Code " +
            "HAVING count(pallet_code) > 1) b " +
            "LEFT JOIN (SELECT DISTINCT Item_Code, Current_Inventory_Qty, Location_Code, pallet_code " +
            "FROM warehouse_items_newest " +
            "WHERE LEFT(Location_Code, 1) in ('A','C') AND Location_Code != 'A-03-01-1' " +
            "AND Current_Inventory_Qty < ${max} ORDER BY Current_Inventory_Qty) a " +
            "ON a.Item_Code = b.Item_Code WHERE Current_Inventory_Qty > 0")
    List<Map<String, Object>> getLessPallet(int max);

    @Insert("insert into merge_steps (id,sku,pieces,from_location,to_location,from_pallet,to_pallet,is_finish,insert_time) values (#{id},#{sku},#{pieces},#{fromLocation},#{toLocation},#{fromPallet},#{toPallet},#{isFinish},now(3))")
    void insertSteps(String id, String sku, int pieces, String fromLocation, String toLocation, String fromPallet, String toPallet, String isFinish);


    @Insert("INSERT INTO sku_pallet (id, sku, location, pallet_no, qty, isTick, isFinish, insert_time,type) " +
            "VALUES (#{id}, #{sku}, #{location}, #{palletNo}, #{qty}, #{isTick}, #{isFinish}, now(3),#{type})")
    void insertSkuPallet(String id, String sku, String location, String palletNo, Integer qty, String isTick, String isFinish, String type
    );


    @Update("update  sku_pallet set isDelete = 1 ")
    void deleteSkuPallet();

    @Delete("update  merge_steps set isDelete = 1 ")
    void deleteMergeSteps();


    @Select("SELECT sku from merge_steps where isDelete is null and is_finish = '0'  order by insert_time desc limit 1")
    String getNext();


    @Select("SELECT * from sku_pallet where sku = #{sku} and isDelete is null ")
    List<Map<String, Object>> selectSkuPallet(String sku);

    @Select("SELECT distinct sku from merge_steps where isDelete is null and is_finish = '0'  ")
    List<String> skuList();

    @Select("SELECT distinct sku from merge_steps where isDelete is null and is_finish != '0'  ")
    List<String> history();



    @Select("select * from merge_steps  where sku = #{sku} and isDelete is null ")
    List<Map<String, Object>> selectMergeSteps(String sku);


    @Update("update merge_steps set is_finish = #{isFinish},update_time = now(),user = #{username} where id = #{id}")
    void updateMergeSteps(String id, String isFinish,String username);

    @Update("update sku_pallet set isFinish =#{isFinish},update_time = now(),user = #{username} where id = #{id}")
    void updateSkuPallet(String id, String isFinish,String username);


    @Select("SELECT DISTINCT \n" +
            "    a.item_code,\n" +
            "    a.Location_Code as z_location,\n" +
            "    a.Current_Inventory_Qty as z_qty,\n" +
            "    a.Pallet_Code as z_pallet,\n" +
            "    GROUP_CONCAT(DISTINCT b.Location_Code) as abc_locations,\n" +
            "    CASE \n" +
            "        WHEN COUNT(b.Location_Code) = 0 THEN '仅在Z区域'\n" +
            "        ELSE '同时在ABC区域'\n" +
            "    END as location_status\n" +
            "FROM warehouse_items_newest a\n" +
            "LEFT JOIN warehouse_items_newest b ON a.item_code = b.item_code \n" +
            "    AND b.Location_Code REGEXP '^[ABC]'\n" +
            "WHERE a.Location_Code LIKE 'Z%'\n" +
            "GROUP BY \n" +
            "    a.item_code, \n" +
            "    a.Location_Code, \n" +
            "    a.Current_Inventory_Qty, \n" +
            "    a.Pallet_Code;")
    List<Map<String, Object>> selectSkuPalletByLocation();


    @Select("SELECT * from merge_steps where is_finish  <> '0'  ${name} ${dateRange}  ORDER BY update_time desc ")
    List<Map<String,Object>> getAllSteps(Map<String,Object> map);



}
