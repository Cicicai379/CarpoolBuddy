package com.example.carpoolbuddy.models;

import android.location.Location;

import com.google.android.libraries.places.api.model.Place;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class Vehicle implements Serializable {
    private User owner;
    private String model;
    private String contact;
    private int capacity;
    private String vehicleID;
    private ArrayList<String> ridersUIDs;
    private boolean open;
    private String vehicleType;
    private double price;
    private Calendar time;

    private Place pickUpLocation;
    private Place dropOffLocation;


    public Vehicle(){

    }
    public Vehicle(String id, User owner, int capacity, double price, String type, Place p, Place d, Calendar time) {
        this.vehicleID = id;
        this.owner = owner;
        this.capacity = capacity;
        this.price = price;
        this.vehicleType= type;
        this.pickUpLocation = p;
        this.dropOffLocation = d;
        this.time = time;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }
// Getters

    public Place getDropOffLocation() {
        return dropOffLocation;
    }

    public Place getPickUpLocation() {
        return pickUpLocation;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setDropOffLocation(Place dropOffLocation) {
        this.dropOffLocation = dropOffLocation;
    }

    public void setPickUpLocation(Place pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public User getOwner() {
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
    public void setOwner(User owner) {
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