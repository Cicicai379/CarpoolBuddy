package com.example.carpoolbuddy.models;

public class Bike extends Vehicle{

    private int weight;
    private int weightCapacity;
    private String bycicleType;
    public Bike(String id, String owner, String model, int capacity, String contact, double price, String type) {
        super(id, owner, model, capacity, contact, price, type);
    }

    public int getWeightCapacity() {
        return weightCapacity;
    }

    public int getWeight() {
        return weight;
    }

    public String getBycicleType() {
        return bycicleType;
    }

    public void setWeightCapacity(int weightCapacity) {
        this.weightCapacity = weightCapacity;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setBycicleType(String bycicleType) {
        this.bycicleType = bycicleType;
    }
}
