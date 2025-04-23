package com.blitz.springboot4.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Table(name = "picking_list", uniqueConstraints = {@UniqueConstraint(
                name = "uk_picking_item_unique",
                columnNames = {"item_no", "picking_order_number","item_code","operator_account","scan_time"})})
public class PickingItem {

    @Id
    @JsonProperty("id")
    @Column(name = "id", length = 64, nullable = false, updatable = false)
    private String id;

    @JsonProperty("picking_order_number")
    @Column(name = "picking_order_number", nullable = false)
    private String pickingOrderNumber;  // 拣货单号

    @JsonProperty("item_no")
    @Column(name = "item_no", nullable = false)
    private int itemNo;             // item NO.

    @JsonProperty("item_code")
    @Column(name = "item_code", nullable = false)
    private String itemCode;          // item Code

    @JsonProperty("operator_account")
    @Column(name = "operator_account", nullable = false)
    private String operatorAccount;    // 操作账号

    @JsonProperty("goods_quantity")
    @Column(name = "goods_quantity", nullable = false)
    private Integer goodsQuantity;     // 货物数

    @JsonProperty("scan_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "scan_time", nullable = false)
    private LocalDateTime scanTime;          // 扫码时间

    @PrePersist
    public void generateId() {

        if (id == null) {
            String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String uuid = UUID.randomUUID().toString().replace("-", "");
            id = dateTime + "_" + uuid;
        }
    }
}