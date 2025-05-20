package com.blitz.springboot4.mapper;

import org.apache.ibatis.annotations.*;
import com.blitz.springboot4.entity.UpDownMove;

import java.util.List;
import java.util.Map;

@Mapper
public interface UpDownMoveMapper {

    @Insert("""
        INSERT INTO up_down_move (location, fromLocation, toLocation, username, status, insert_time, update_time)
        VALUES (#{location}, #{fromLocation}, #{toLocation}, #{username}, #{status}, NOW(), NOW())
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertInventory(UpDownMove inventory);

    @Select("""
        SELECT id, username, fromLocation, toLocation, TIME(insert_time) AS insertTime, location, status
        FROM up_down_move
        WHERE username = #{username}
          AND status = #{status}
          AND DATE(insert_time) = CURDATE()
        ORDER BY insertTime DESC
        """)
    List<UpDownMove> selectUpDownMove(String username, String status);

    @Update("UPDATE up_down_move SET status = 0 WHERE id = #{id}")
    int deleteUpDownMove(String id);

    @Select("""
        SELECT username, fromLocation, toLocation, insert_time AS insertTime, location
        FROM up_down_move
        WHERE status = '1'
          ${params.name}
          ${params.dateRange}
        ORDER BY insert_time DESC
        LIMIT #{params.pageSize} OFFSET #{params.offset}
        """)
    List<UpDownMove> selectActionList(@Param("params") Map<String, Object> params);
    //https://github.com/dawnbrisk/warehouse.git
    @Select("""
        SELECT username, fromLocation, toLocation, insert_time AS insertTime, location
        FROM up_down_move
        WHERE status = '1'
          ${params.name}
          ${params.dateRange}
        """)
    List<UpDownMove> selectActionListTotal(@Param("params") Map<String, Object> params);

    @Select("""
        SELECT
            username,
            DATE(insert_time) AS movement_date,
            COUNT(*) AS total_movements
        FROM up_down_move
        GROUP BY username, movement_date
        ORDER BY username, movement_date DESC
        """)
    List<Map<String, Object>> updownDetail();

    @Select("""
            SELECT
                trim(username) as username,
                DATE(insert_time) AS move_date,
                DATE_FORMAT(insert_time, '%Y-%m-%d %H:00:00') AS hour_slot,
                COUNT(*) AS move_count
            FROM (
                SELECT *
                FROM up_down_move
                WHERE insert_time >= CURDATE() - INTERVAL 2 MONTH
            ) AS up_down_move
            WHERE insert_time IS NOT NULL
            GROUP BY trim(username), move_date, hour_slot
     	order by trim(username), move_date ,hour_slot
        """)
    List<Map<String, Object>> averageMovePerHour();
}
