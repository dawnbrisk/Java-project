package com.blitz.springboot4.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface VisualizationMapper {

    @Select("SELECT location,(max_number - qty) as num,type , qty from locations LEFT JOIN (\n" +
            "SELECT Location_Code, count(*) as qty from warehouse_items_newest GROUP BY Location_Code ) b on locations.location = b.Location_Code\n")
    List<Map<String,Object>> getAllLocations();
}
