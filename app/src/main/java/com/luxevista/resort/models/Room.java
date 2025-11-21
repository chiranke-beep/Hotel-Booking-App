package com.luxevista.resort.models;

import com.google.firebase.firestore.PropertyName;

public class Room {
    private String id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    
    @PropertyName("isBooked")
    private boolean isBooked;
    
    @PropertyName("checkInDate")
    private String checkInDate;
    
    @PropertyName("checkOutDate")
    private String checkOutDate;
    
    @PropertyName("checkInTime")
    private String checkInTime;
    
    @PropertyName("checkOutTime")
    private String checkOutTime;
    
    @PropertyName("booking_id")
    private String bookingId;

    public Room() {
        // Default constructor required for Firestore
    }

    public Room(String name, String description, double price, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isBooked = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(Object price) {
        if (price instanceof String) {
            try {
                this.price = Double.parseDouble((String) price);
            } catch (NumberFormatException e) {
                this.price = 0.0;
            }
        } else if (price instanceof Number) {
            this.price = ((Number) price).doubleValue();
        } else {
            this.price = 0.0;
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @PropertyName("isBooked")
    public boolean isBooked() {
        return isBooked;
    }

    @PropertyName("isBooked")
    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    @PropertyName("checkInDate")
    public String getCheckInDate() {
        return checkInDate;
    }

    @PropertyName("checkInDate")
    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    @PropertyName("checkOutDate")
    public String getCheckOutDate() {
        return checkOutDate;
    }

    @PropertyName("checkOutDate")
    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    @PropertyName("checkInTime")
    public String getCheckInTime() {
        return checkInTime;
    }

    @PropertyName("checkInTime")
    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    @PropertyName("checkOutTime")
    public String getCheckOutTime() {
        return checkOutTime;
    }

    @PropertyName("checkOutTime")
    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    @PropertyName("booking_id")
    public String getBookingId() {
        return bookingId;
    }

    @PropertyName("booking_id")
    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                ", isBooked=" + isBooked +
                ", checkInDate='" + checkInDate + '\'' +
                ", checkOutDate='" + checkOutDate + '\'' +
                ", checkInTime='" + checkInTime + '\'' +
                ", checkOutTime='" + checkOutTime + '\'' +
                ", bookingId='" + bookingId + '\'' +
                '}';
    }
} 