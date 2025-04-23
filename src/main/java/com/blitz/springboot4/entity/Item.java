package com.blitz.springboot4.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity

@Table(name = "warehouse_items_newest")
public class Item {

    @Id
    @JsonProperty("id")  // JSON 序列化时使用 "id" 作为键名
    @Column(name = "id", length = 32, nullable = false, updatable = false)  // 映射数据库字段
    private String id;

    @JsonProperty("item_code")
    @Column(name = "item_code", nullable = false)
    private String itemCode;

    @JsonProperty("Current_Inventory_Qty")
    @Column(name = "Current_Inventory_Qty", nullable = false)
    private Integer currentInventoryQty;

    @JsonProperty("Location_Code")
    @Column(name = "Location_Code", nullable = false)
    private String locationCode;

    @JsonProperty("Pallet_Code")
    @Column(name = "Pallet_Code", nullable = false)
    private String palletCode;

    @JsonProperty("In_Stock_Time")
    @Column(name = "In_Stock_Time", nullable = false)
    private String inStockTime;

    @PrePersist
    public void generateId() {
        if (id == null) {
            String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            // Generate a UUID and remove the hyphens
            String uuid = UUID.randomUUID().toString().replace("-", "");

            // Combine dateTime and UUID to create the ID
            id = dateTime +"_"+ uuid;
        }
    }
}
