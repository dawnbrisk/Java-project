package com.blitz.springboot4.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface OldestSKUMapper {


    @Select("SELECT DATE(in_stock_time) AS in_stock_date,item_code,location_code " +
            "FROM warehouse_items_newest where location_code like 'P%' \n" +
            "ORDER BY DATE(in_stock_time) ASC \n" +
            "LIMIT 50;")
    List<Map<String,Object>> getOldestSKU();

    @Select("SELECT\n" +
            "    DATE(in_stock_time) AS in_stock_date,\n" +
            "    item_code,\n" +
            "    location_code\n" +
            "FROM\n" +
            "    warehouse_items_newest\n" +
            "WHERE\n" +
            "    location_code LIKE 'A%'  -- Filters location_code starting with 'A'\n" +
            "    OR location_code LIKE 'B%'  -- Filters location_code starting with 'B'\n" +
            "    OR location_code LIKE 'C%'  -- Filters location_code starting with 'B'\n" +
            "ORDER BY\n" +
            "    DATE(in_stock_time) ASC\n" +
            "LIMIT 50;")
    List<Map<String,Object>> getOldestSKU2();


    @Select("SELECT item_code,Location_Code,Pallet_Code,Current_Inventory_Qty FROM warehouse_items_newest WHERE item_code IN (${itemCodes})")
    List<Map<String, Object>> getDoubleCheckList(@Param("itemCodes") String itemCodes);

}
