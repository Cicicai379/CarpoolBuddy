package com.example.carpoolbuddy.models;

public class Helicopter extends Vehicle{
    private int maxAltitude;
    private int maxAirSpeed;

    public Helicopter(String id, String owner, String model, int capacity, String contact, double price, String type) {
        super(id, owner, model, capacity, contact, price, type);
    }

    public int getMaxAirSpeed() {
        return maxAirSpeed;
    }

    public int getMaxAltitude() {
        return maxAltitude;
    }

    public void setMaxAirSpeed(int maxAirSpeed) {
        this.maxAirSpeed = maxAirSpeed;
    }

    public void setMaxAltitude(int maxAltitude) {
        this.maxAltitude = maxAltitude;
    }
}
