package com.blitz.springboot4.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class InventoryMovement {
    @Id
    String sku;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    String fromLocation;
    String fromPallet;
    String toLocation;
    String toPallet;
    int quantity;

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getFromPallet() {
        return fromPallet;
    }

    public void setFromPallet(String fromPallet) {
        this.fromPallet = fromPallet;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public String getToPallet() {
        return toPallet;
    }

    public void setToPallet(String toPallet) {
        this.toPallet = toPallet;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
