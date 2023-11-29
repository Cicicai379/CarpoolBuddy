package com.example.carpoolbuddy.models;

public class Segway extends Vehicle{
    private int range;
    private int capacity;

    public Segway(String id, String owner, String model, int capacity, String contact, double price, String type) {
        super(id, owner, model, capacity, contact, price, type);
    }

    public int getWeightCapacity() {
        return capacity;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }


    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

}
