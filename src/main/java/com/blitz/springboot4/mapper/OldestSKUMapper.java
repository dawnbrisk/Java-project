package com.blitz.springboot4.mapper;


import org.apache.ibatis.annotations.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Mapper
public interface OldestSKUMapper {


    @Select("""
            SELECT DATE(in_stock_time) AS in_stock_date, item_code, location_code
            FROM warehouse_items_newest
            WHERE location_code LIKE 'P%'
            ORDER BY DATE(in_stock_time) ASC
            LIMIT 50
            """)
    List<Map<String, Object>> getOldestSKU();


    @Select("""
            SELECT
                DATE(in_stock_time) AS in_stock_date,
                item_code,
                location_code
            FROM
                warehouse_items_newest
            WHERE
                location_code LIKE 'A%'  
                OR location_code LIKE 'B%'  
                OR location_code LIKE 'C%'  
            ORDER BY
                DATE(in_stock_time) ASC
            LIMIT 50
            """)
    List<Map<String, Object>> getOldestSKU2();


    @Select("SELECT item_code,Location_Code,Pallet_Code,Current_Inventory_Qty FROM warehouse_items_newest WHERE item_code IN (${itemCodes})")
    List<Map<String, Object>> getDoubleCheckList(@Param("itemCodes") String itemCodes);


    /*
    清空数据库表 double_weeks_check 中的所有数据
     */
    @Transactional
    @Update("TRUNCATE TABLE double_weeks_check;")
    void truncateDoubleWeeksCheck();

    @Transactional
    @Insert("""
                INSERT INTO double_weeks_check (item_code, Location_Code, Pallet_Code, Current_Inventory_Qty, insert_time)
                SELECT item_code, Location_Code, Pallet_Code, Current_Inventory_Qty, NOW()
                FROM warehouse_items_newest
                WHERE item_code IN (${itemCodes});
            """)
    void insertIntoDoubleWeeksCheck(@Param("itemCodes") String itemCodes);


}
