package com.example.carpoolbuddy.models;

import java.util.ArrayList;

public class User {
    private String uid;
    private String name;
    private String email;
    private String userType;
    private String imageUrl;
    private String phone;
    private double priceMultiplier;
    private ArrayList<String> ownedVehicles;

    public User(){

    }


    public User(String uid, String username, String email,  String type){
        this.uid = uid;
        this.email = email;
        this.name = username;
        this.userType = type;
    }

    public User(String username, String email, String phone, String imageUrl, int t){
        this.email = email;
        this.name = username;
        this.phone = phone;
        this.imageUrl = imageUrl;
    }
    public User( String username,String email,  String phone){
        this.email = email;
        this.name = username;
        this.phone = phone;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public double getPriceMultiplier() {
        return priceMultiplier;
    }

    public void setPriceMultiplier(double priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
    }

    public ArrayList<String> getOwnedVehicles() {
        return ownedVehicles;
    }

    public void setOwnedVehicles(ArrayList<String> ownedVehicles) {
        this.ownedVehicles = ownedVehicles;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}