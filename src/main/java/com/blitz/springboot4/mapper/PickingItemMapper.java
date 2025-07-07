package com.blitz.springboot4.mapper;

import com.blitz.springboot4.entity.PickingItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PickingItemMapper {

    @Insert("INSERT IGNORE INTO picking_list " +
            "(id, picking_order_number, item_no, item_code, operator_account, goods_quantity, scan_time) " +
            "VALUES (#{id}, #{pickingOrderNumber}, #{itemNo}, #{itemCode}, #{operatorAccount}, #{goodsQuantity}, #{scanTime})")
    void insertIgnore(PickingItem item);


    @Insert({
            "<script>",
            "INSERT INTO picking_list ",
            "(id, picking_order_number, item_no, item_code, operator_account, goods_quantity, scan_time) ",
            "VALUES ",
            "<foreach collection='list' item='item' separator=','>",
            "(#{item.id}, #{item.pickingOrderNumber}, #{item.itemNo}, #{item.itemCode}, ",
            "#{item.operatorAccount}, #{item.goodsQuantity}, #{item.scanTime})",
            "</foreach> ",
            "ON DUPLICATE KEY UPDATE id = id",
            "</script>"
    })
    void batchInsertIgnore(@Param("list") List<PickingItem> items);




}
