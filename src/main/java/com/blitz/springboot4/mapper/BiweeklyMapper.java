package com.blitz.springboot4.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.transaction.annotation.Transactional;

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


    /*
   清空数据库表 double_weeks_check 中的所有数据
    */
    @Transactional
    @Update("TRUNCATE TABLE double_weeks_check;")
    void truncateDoubleWeeksCheck();


    @Transactional
    @Insert("""
                INSERT INTO double_weeks_check (
                    item_code, Location_Code, Pallet_Code, Current_Inventory_Qty, insert_time
                )
                SELECT item_code, Location_Code, Pallet_Code, Current_Inventory_Qty, #{batchTime}
                FROM warehouse_items_newest
                WHERE item_code IN (${itemCodes});
            """)
    void insertIntoDoubleWeeksCheck(
            @Param("itemCodes") String itemCodes,
            @Param("batchTime") String batchTime  // 改成字符串类型
    );


    @Select("SELECT item_code,Location_Code,Pallet_Code,Current_Inventory_Qty FROM warehouse_items_newest WHERE item_code IN (${itemCodes})")
    List<Map<String, Object>> getDoubleCheckList(@Param("itemCodes") String itemCodes);


    @Select("""
            SELECT
            	item_code,
            	location_code,
            	GROUP_CONCAT( CONCAT( pallet_code, ':', current_inventory_qty ) SEPARATOR '+' ) AS pallet_qty_combined 
            FROM
            	double_weeks_check 
            	where user is null
            GROUP BY
            	item_code,
            	location_code 
            ORDER BY
            	location_code
            """)
    List<Map<String,Object>>  getToCheckList();



    @Update("UPDATE double_weeks_check SET checked_qty = #{qty} ,update_time = now(),user = #{user} WHERE item_code = #{itemCode} AND pallet_code = #{palletCode}")
    void updateQtyByItemAndPallet(@Param("itemCode") String itemCode,
                                  @Param("palletCode") String palletCode,
                                  @Param("qty") String qty,
                                  @Param("user")    String username );
}
