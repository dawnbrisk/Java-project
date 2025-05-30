package com.blitz.springboot4.mapper;


import org.apache.ibatis.annotations.*;

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









    @Select("""
                SELECT DISTINCT picking_order_number  
                FROM picking_list 
                WHERE picking_order_number LIKE CONCAT(#{month}, '%') 
                ORDER BY picking_order_number
            """)
    List<String> getPickingOrderNumber(@Param("month") String month);


}
