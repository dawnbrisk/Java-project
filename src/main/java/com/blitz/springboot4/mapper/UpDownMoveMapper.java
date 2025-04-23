package com.blitz.springboot4.mapper;

import org.apache.ibatis.annotations.*;
import com.blitz.springboot4.entity.UpDownMove;

import java.util.List;
import java.util.Map;


@Mapper
public interface UpDownMoveMapper {

    @Insert("INSERT INTO up_down_move (location, fromLocation, toLocation, username,status,insert_time,update_time) " +
            "VALUES (#{location},#{fromLocation},#{toLocation},#{username},#{status},NOW(),NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertInventory(UpDownMove inventory);


    @Select("select id,username,fromLocation  ,toLocation  ,time(insert_time) as insertTime,location,status from up_down_move " +
            "where username = #{username} and status = #{status} and DATE(insert_time) = CURDATE() order by insertTime desc")
    List<UpDownMove> selectUpDownMove(String username, String status);

    @Update(" update up_down_move set status = 0 where id = #{id}")
    int deleteUpDownMove(String id);

    @Select("SELECT username, fromLocation, toLocation, insert_time, location \n" +
            "FROM up_down_move \n" +
            "WHERE status = '1' \n" +
            "  ${params.name} \n" +
            "  ${params.dateRange} \n" +
            "\t order by insert_time desc  LIMIT #{params.pageSize} OFFSET #{params.offset} ")
    List<UpDownMove> selectActionList(@Param("params") Map<String, Object> params);


    @Select("SELECT username, fromLocation, toLocation, insert_time, location \n" +
            "FROM up_down_move \n" +
            "WHERE status = '1' \n" +
            "  ${params.name} \n" +
            "  ${params.dateRange} \n" )
    List<UpDownMove> selectActionListTotal(@Param("params") Map<String, Object> params);


    @Select("SELECT\n" +
            "    username,\n" +
            "    DATE(insert_time) AS movement_date,\n" +
            "    COUNT(*) AS total_movements\n" +
            "FROM\n" +
            "    up_down_move\n" +
            "GROUP BY\n" +
            "    username,\n" +
            "    movement_date\n" +
            "ORDER BY\n" +
            "   username, movement_date DESC ;\n")
    List<Map<String,Object>> updownDetail();



    @Select("WITH hourly_moves AS (\n" +
            "  SELECT\n" +
            "    username,\n" +
            "    DATE(insert_time) AS move_date,\n" +
            "    DATE_FORMAT(insert_time, '%Y-%m-%d %H:00:00') AS hour_slot,\n" +
            "    COUNT(*) AS move_count\n" +
            "  FROM(SELECT *\n" +
            "FROM up_down_move\n" +
            "WHERE insert_time >= CURDATE() - INTERVAL 2 MONTH) as up_down_move" +
            "  WHERE insert_time IS NOT NULL\n" +
            "  GROUP BY username, move_date, hour_slot\n" +
            "),\n" +
            "min_hourly_moves AS (\n" +
            "  SELECT\n" +
            "    username,\n" +
            "    move_date,\n" +
            "    MIN(move_count) AS min_move_count\n" +
            "  FROM hourly_moves\n" +
            "  GROUP BY username, move_date\n" +
            ")\n" +
            "SELECT\n" +
            "  h.username,\n" +
            "  h.move_date,\n" +
            "  h.hour_slot,\n" +
            "  h.move_count\n" +
            "FROM hourly_moves h\n" +
            "LEFT JOIN min_hourly_moves m\n" +
            "  ON h.username = m.username\n" +
            "  AND h.move_date = m.move_date\n" +
            "  AND h.move_count = m.min_move_count\n" +
            "WHERE m.username IS NULL\n" +
            "ORDER BY h.username, h.move_date, h.hour_slot;\n")
    List<Map<String,Object>> averageMovePerHour();
}
