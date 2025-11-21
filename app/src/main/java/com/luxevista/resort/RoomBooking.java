package com.luxevista.resort;

import com.google.firebase.Timestamp;

public class RoomBooking {
    private String roomId;
    private String roomName;
    private String checkInDate;
    private String checkOutDate;
    private String checkInTime;
    private String checkOutTime;
    private String status;
    private double totalPrice;
    private Timestamp createdAt;
    private Timestamp timestamp;
    private int guests;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setCreatedAt(long millis) {
        this.createdAt = new Timestamp(millis / 1000, 0);
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimestamp(long millis) {
        this.timestamp = new Timestamp(millis / 1000, 0);
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public int getGuests() {
        return guests;
    }

    public void setGuests(int guests) {
        this.guests = guests;
    }
} 