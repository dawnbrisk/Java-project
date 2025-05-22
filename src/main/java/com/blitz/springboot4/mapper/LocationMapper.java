package com.blitz.springboot4.mapper;


import com.blitz.springboot4.entity.WarehouseLocation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


import java.util.List;
import java.util.Map;

@Mapper
public interface LocationMapper {


    @Select("SELECT  " +
            "    a.locationCode, " +
            "    b.Item_Code AS ItemCode, " +
            "    a.palletNumber " +
            "FROM  " +
            "    ( " +
            "        SELECT  " +
            "            Location_Code AS locationCode, " +
            "            COUNT(Item_Code) AS palletNumber " +
            "        FROM  " +
            "           warehouse_items_newest m  " +
            "      LEFT JOIN locations n on m.Location_Code = n.location " +
            "        WHERE  " +
            "           n.type = '0' " +
            "        GROUP BY  " +
            "            Location_Code " +
            "        HAVING  " +
            "            COUNT(Item_Code) < 5 " +
            "    ) AS a " +
            "LEFT JOIN  " +
            "    ( " +
            "        SELECT DISTINCT  " +
            "            Item_Code,  " +
            "            Location_Code  " +
            "        FROM  " +
            "            warehouse_items_newest " +
            "        ORDER BY  " +
            "            Item_Code " +
            "    ) AS b  " +
            "    ON a.locationCode = b.Location_Code " +
            "ORDER BY  " +
            "    ItemCode")
    List<WarehouseLocation> selectBySomeColumn();

    @Select("""
            
                SELECT
                a.Location_Code,
                a.item_code as sku ,
                COUNT(DISTINCT a.Pallet_Code) AS uniqueSkuCount
            FROM warehouse_items_newest a
            JOIN locations b ON a.Location_Code = b.location
            WHERE b.type = '0'
            GROUP BY a.Location_Code, a.item_code
            HAVING a.Location_Code IN (
                SELECT Location_Code
                FROM warehouse_items_newest a2
                JOIN locations b2 ON a2.Location_Code = b2.location
                WHERE b2.type = '0'
                GROUP BY a2.Location_Code
                HAVING COUNT(DISTINCT a2.item_code) > 1
            )
            ORDER BY a.Location_Code, a.item_code;
            """)
    List<Map<String, Object>> findMixingLocation();



    @Select("SELECT location_code,(b.max_number - count(*)) as empty_num  ,count(*) as current_num    " +
            "from warehouse_items_newest a LEFT JOIN locations b on a.Location_Code = b.location  " +
            "WHERE b.type = 1 GROUP BY a.Location_Code HAVING empty_num > 0 ORDER BY location_code ")
    List<Map<String, Object>> getEmptyLocation();


    @Select("SELECT COUNT(*) FROM moving_steps WHERE sku = #{item_code} AND pallet_count = 1 AND from_location = #{location_code} AND isDelete IS NULL")
    int isExist(Map<String, Object> map);


    @Select("SELECT m.*,n.item_code from ( " +
            "SELECT location_code,count(*) as num  " +
            "from warehouse_items_newest a LEFT JOIN locations b on a.Location_Code = b.location  " +
            "WHERE b.type = 0 GROUP BY a.Location_Code HAVING count(*) = 1  ORDER BY location_code ) " +
            "m LEFT JOIN (SELECT MIN( item_code ) AS item_code, location_code FROM warehouse_items_newest GROUP BY location_code ) n on m.location_code = n.location_code  ")
    List<Map<String, Object>> getSinglePallet();

    @Select("select count(*) as num  from sku_bigpallet where first_location = #{location} or second_location = #{location}")
    int isBigPallet(String location);


    @Select("""
            	SELECT
                item_code,
                location_code AS fromLocation
            FROM
                warehouse_items_newest a
                LEFT JOIN locations b ON a.location_code = b.location
            WHERE
                b.type = 1
                AND item_code IN (
                    SELECT item_code
                    FROM warehouse_items_newest a
                    LEFT JOIN locations b ON a.location_code = b.location
                    WHERE b.type = 1
                    GROUP BY item_code
                    HAVING COUNT(*) = 1
                );
            """)
    List<Map<String, Object>> getPalletInWay();

    @Select("""
                SELECT 
                    a.*, b.item_code
                FROM (
                    SELECT 
                        k.*, j.current_num 
                    FROM (
                        SELECT 
                            location_code, 
                            (n.max_number - COUNT(*)) AS num  
                        FROM 
                            warehouse_items_newest m
                            LEFT JOIN locations n ON m.Location_Code = n.location  
                        WHERE 
                            n.type = 0  
                        GROUP BY 
                            location_code
                    ) k 
                    LEFT JOIN (
                        SELECT 
                            location_code, 
                            COUNT(*) AS current_num 
                        FROM 
                            warehouse_items_newest 
                        GROUP BY 
                            location_code
                    ) j ON k.location_code = j.location_code
                ) a 
                LEFT JOIN (
                    SELECT 
                        MIN(item_code) AS item_code, 
                        location_code 
                    FROM 
                        warehouse_items_newest 
                    GROUP BY 
                        location_code
                ) b ON a.location_code = b.location_code
                WHERE 
                    num > 0 
                    AND num < 4  
                ORDER BY 
                    a.location_code
            """)
    List<Map<String, Object>> getGeneralLocation();


    @Insert("insert into moving_steps (id,sku, pallet_count, from_location, to_location, type,insert_time) " +
            "values (#{id},#{sku}, #{pallet_count}, #{from_location}, #{to_location}, #{type},NOW())")
    void insertSteps(String id, String sku, int pallet_count, String from_location, String to_location, String type);

    @Insert("insert into sku_location (id,sku, location,pallet_qty, insert_time,location_type, isTick,type) " +
            "values (#{id},#{sku}, #{location}, #{qty}, now(3),#{location_type}, #{isTick},#{type})")
    void insertLocations(String id, String sku, String location, int qty, String location_type, String isTick,String type);

    @Update("update sku_location set isDelete = 1")
    void deleteLocations();

    @Update("update moving_steps set isDelete = 1")
    void deleteSteps();


    @Select("select * from sku_location where sku = #{sku} and isDelete is null ")
    List<Map<String, Object>> getLocationListBySku(String sku);

    @Select("select * from moving_steps where sku = #{sku} and isDelete is null")
    List<Map<String, Object>> getStepsBySku(String sku);


    @Update("update moving_steps  set isFinish = #{isFinish}, update_time = now(),user = #{username} where id = #{id}")
    void updateSteps(String id, String isFinish, String username);

    @Update("update sku_location  set isFinish = #{isFinish}, finish_time = now() ,user = #{username} where id = #{id}")
    void updateLocation(String id, String isFinish, String username);

    @Select(""" 
            SELECT m.sku, MAX(insert_time) AS max_insert_time 
            FROM sku_location  m LEFT JOIN sku_skip n  on m.sku = n.sku  
            WHERE location_type = '1'   AND isTick = '1'    AND isFinish IS NULL     AND isdelete IS NULL     and n.sku is null  
            GROUP BY sku  ORDER BY max_insert_time DESC  
            """)
    List<Map<String, Object>> getAllLocations();


    @Select("SELECT sku, MAX(finish_time) AS max_finish_time    " +
            "FROM sku_location  where location_type = '1' and isTick = '1' and isFinish is  not null and DATE(finish_time) = CURDATE()  and user = #{name} " +
            "GROUP BY sku    " +
            "ORDER BY max_finish_time desc")
    List<Map<String, Object>> getHistory(String name);


    @Select("SELECT * from moving_steps where isFinish is not null  ${name} ${dateRange}   ORDER BY update_time desc")
    List<Map<String, Object>> getAllSteps(Map<String, Object> map);


    @Select("SELECT location , CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(location, '-', 3), '-', -1) AS UNSIGNED)  % 3  AS group_number     " +
            "FROM locations a     " +
            "LEFT JOIN warehouse_items_newest b     " +
            "    ON a.location = b.Location_Code     " +
            "WHERE b.Location_Code IS NULL    " +
            "and a.type = '0'    " +
            "ORDER BY location")
    List<Map<String, Object>> EmptyLocation();


    @Select("SELECT  " +
            "    CASE  " +
            "        WHEN MAX(sku_length) >= #{maxLength} THEN 'yes' " +
            "        ELSE 'no' " +
            "    END AS result " +
            "FROM ( " +
            "  " +
            "SELECT " +
            "\titem_code,b.sku_length " +
            "FROM " +
            "\twarehouse_items_newest a " +
            "\tLEFT JOIN sku_size b " +
            "\ton a.item_code = b.sku  " +
            "WHERE " +
            "\tlocation_code = #{location}  " +
            "\t) c")
    String getSku(String location, int maxLength);


    @Insert("insert  ignore  into sku_skip values(#{sku},#{reasonType})")
    void insetSkipSku(String sku, String reasonType);


    @Insert("insert into sku_bigpallet (first_location,second_location) values (#{location1},#{location2})")
    void insertBigPallet(String location1, String location2);

    @Select("""
            
            SELECT
                `user`,
                DATE(update_time) AS move_date,
                SUM(pallet_count) AS total_pallets
            FROM
                moving_steps
            WHERE
                isFinish = 'Y'
            		and  update_time >= DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 2 MONTH), '%Y-%m-01')
            GROUP BY
                `user`,
                DATE(update_time)
            ORDER BY
                `user`,
                move_date
            """)
    List<Map<String, Object>> getMovingHistory();
}
