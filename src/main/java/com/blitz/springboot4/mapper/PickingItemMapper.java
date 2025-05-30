package com.blitz.springboot4.mapper;

import com.blitz.springboot4.entity.PickingItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PickingItemMapper {

    @Insert("INSERT IGNORE INTO picking_list " +
            "(id, picking_order_number, item_no, item_code, operator_account, goods_quantity, scan_time) " +
            "VALUES (#{id}, #{pickingOrderNumber}, #{itemNo}, #{itemCode}, #{operatorAccount}, #{goodsQuantity}, #{scanTime})")
    void insertIgnore(PickingItem item);


}
