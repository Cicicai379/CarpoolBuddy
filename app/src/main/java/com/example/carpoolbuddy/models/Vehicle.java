package com.example.carpoolbuddy.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Vehicle implements Serializable {
    private String owner;
    private String model;
    private String contact;

    private int capacity;

    private String vehicleID;
    private ArrayList<String> ridersUIDs;
    private boolean open;
    private String vehicleType;
    private double price;

    public Vehicle(){

    }
    public Vehicle(String id, String owner, String model, int capacity, String contact, double price, String type) {
        this.vehicleID = id;
        this.owner = owner;
        this.model = model;
        this.capacity = capacity;
        this.contact = contact;
        this.price = price;
        this.vehicleType= type;
    }

    // Getters
    public String getOwner() {
        return owner;
    }

    public String getModel() {
        return model;
    }

    public String getContact() {
        return contact;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public ArrayList<String> getRidersUIDs() {
        return ridersUIDs;
    }

    public boolean isOpen() {
        return open;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public double getPrice() {
        return price;
    }

    public int getCapacity() {
        return capacity;
    }

    // Setters
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    public void setRidersUIDs(ArrayList<String> ridersUIDs) {
        this.ridersUIDs = ridersUIDs;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}