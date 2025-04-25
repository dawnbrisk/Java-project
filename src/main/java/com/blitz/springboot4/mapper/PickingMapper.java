package com.blitz.springboot4.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface PickingMapper {

    @Select("""
    SELECT 
        DATE_FORMAT(scan_time, '%Y-%m') AS month,
        operator_account,
        SUM(goods_quantity) AS total_quantity  
    FROM picking_list
    GROUP BY month, operator_account
    ORDER BY month DESC, total_quantity DESC
    """)
    List<Map<String, Object>> findAllPickings();




    @Select("""
    SELECT
      operator_account AS username,
      DATE(scan_time) AS move_date,
      DATE_FORMAT(scan_time, '%Y-%m-%d %H:00:00') AS hour_slot,
      SUM(goods_quantity) AS move_count
    FROM picking_list
    WHERE operator_account = #{account}
      AND DATE_FORMAT(scan_time, '%Y-%m') = #{month}
      AND scan_time IS NOT NULL
    GROUP BY operator_account, move_date, hour_slot
    ORDER BY operator_account, move_date, hour_slot
    """)
    List<Map<String, Object>> findPickingsByDay(String account, String month);




    @Select("select * from picking_list ")
    List<Map<String,Object>> findPickingsByAccount();

}
