package com.blitz.springboot4.entity;




public class Warehouse {



    private String location;

    private int emptySpaces;

    public Warehouse(String location, int emptySpaces) {
        this.location = location;
        this.emptySpaces = emptySpaces;
    }

    public String getLocation() {
        return location;
    }

    public int getEmptySpaces() {
        return emptySpaces;
    }

    public void setEmptySpaces(int emptySpaces) {
        this.emptySpaces = emptySpaces;
    }

    public void setLocation(String location) {}
}
