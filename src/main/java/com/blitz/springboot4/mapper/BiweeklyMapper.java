package com.blitz.springboot4.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface BiweeklyMapper {

    @Select("""
        SELECT 
            item_code,
            location_code,
            pallet_code,
            current_inventory_qty,
            checked_qty,
            isFinish,
            insert_time,
            update_time,
            `user`,
            remark
        FROM 
            double_weeks_check
        ORDER BY 
            item_code
        LIMIT #{limit} OFFSET #{offset}
    """)
    List<Map<String, Object>> getPageData(@Param("offset") int offset, @Param("limit") int limit);



    @Select("SELECT COUNT(*) FROM double_weeks_check")
    int getTotalCount();
}
