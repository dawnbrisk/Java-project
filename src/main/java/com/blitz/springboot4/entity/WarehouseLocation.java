package com.blitz.springboot4.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "warehouse_items")
public class WarehouseLocation implements Comparable<WarehouseLocation> , Cloneable{

    @Id
    private int id;


    private String locationCode;


    private String itemCode;


    private int palletNumber;

    private int maxPalletNumber;


    public WarehouseLocation() {}

    public WarehouseLocation(String location_code, String location_type, int pallet_quantity) {
        this.locationCode = location_code;
        this.itemCode = location_type;
        this.palletNumber = pallet_quantity;

    }

    public WarehouseLocation(WarehouseLocation location) {
        this.locationCode = location.locationCode;
        this.itemCode = location.itemCode;
        this.palletNumber = location.palletNumber;
    }

    public String getItemCode() {
        return itemCode;
    }
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
    public int getPalletNumber() {
        return palletNumber;
    }
    public void setPalletNumber(int palletNumber) {
        this.palletNumber = palletNumber;
    }
    public int getMaxPalletNumber() {
        return maxPalletNumber;
    };
    public void setMaxPalletNumber(int maxPalletNumber) {
        this.maxPalletNumber = maxPalletNumber;
    }
    public String getLocationCode() {
        return locationCode;
    }
    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }
    @Override
    public int compareTo(WarehouseLocation o) {
        return this.palletNumber > o.getPalletNumber() ? 1 : -1;
    }

    @Override
    public WarehouseLocation clone() {
        try {
            return (WarehouseLocation) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Clone should be supported");
        }
    }
}

