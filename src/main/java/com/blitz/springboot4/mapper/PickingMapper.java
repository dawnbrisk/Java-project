package com.blitz.springboot4.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface PickingMapper {

    @Select("SELECT \n" +
            "    DATE_FORMAT(scan_time, '%Y-%m') AS month,\n" +
            "    operator_account,\n" +
            "    SUM(goods_quantity) AS total_quantity  \n" +
            "FROM picking_list\n" +
            "GROUP BY month, operator_account\n" +
            "ORDER BY month DESC, total_quantity DESC")
     List<Map<String,Object>> findAllPickings();



    @Select(
            "SELECT\n" +
                    "  operator_account AS username,\n" +
                    "  DATE(scan_time) AS move_date,\n" +
                    "  DATE_FORMAT(scan_time, '%Y-%m-%d %H:00:00') AS hour_slot,\n" +
                    "  SUM(goods_quantity) AS move_count\n" +
                    "FROM picking_list\n" +
                    "WHERE operator_account = #{account}\n" +
                    "  AND DATE_FORMAT(scan_time, '%Y-%m') = #{month}\n" +
                    "  AND scan_time IS NOT NULL\n" +
                    "GROUP BY operator_account, move_date, hour_slot\n" +
                    "ORDER BY operator_account, move_date, hour_slot;\n")
    List<Map<String,Object>> findPickingsByDay(String account,String month);



    @Select("select * from picking_list ")
    List<Map<String,Object>> findPickingsByAccount();

}
