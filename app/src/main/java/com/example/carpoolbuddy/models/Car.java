package com.example.carpoolbuddy.models;

public class Car extends Vehicle{

    private int range;
    public Car(String id, String owner, String model, int capacity, String contact, double price, String type) {
        super(id, owner, model, capacity, contact, price, type);
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getRange() {
        return range;
    }
}
