package com.luxevista.resort.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

public class RoomBooking {
    private String id;
    private String userId;
    private String roomId;
    private String roomName;
    private String checkInDate;
    private String checkOutDate;
    private String checkInTime;
    private String checkOutTime;
    private int guests;
    private double totalPrice;
    private String status;

    // Required empty constructor for Firestore
    public RoomBooking() {}

    public RoomBooking(String id, String userId, String roomId, String roomName,
                      String checkInDate, String checkOutDate, String checkInTime, String checkOutTime,
                      int guests, double totalPrice, String status) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.roomName = roomName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.guests = guests;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    @PropertyName("id")
    public String getId() {
        return id;
    }

    @PropertyName("id")
    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("user_id")
    public String getUserId() {
        return userId;
    }

    @PropertyName("user_id")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @PropertyName("room_id")
    public String getRoomId() {
        return roomId;
    }

    @PropertyName("room_id")
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @PropertyName("room_name")
    public String getRoomName() {
        return roomName;
    }

    @PropertyName("room_name")
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    @PropertyName("check_in_date")
    public String getCheckInDate() {
        return checkInDate;
    }

    @PropertyName("check_in_date")
    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    @PropertyName("check_out_date")
    public String getCheckOutDate() {
        return checkOutDate;
    }

    @PropertyName("check_out_date")
    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    @PropertyName("check_in_time")
    public String getCheckInTime() {
        return checkInTime;
    }

    @PropertyName("check_in_time")
    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    @PropertyName("check_out_time")
    public String getCheckOutTime() {
        return checkOutTime;
    }

    @PropertyName("check_out_time")
    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    @PropertyName("guests")
    public int getGuests() {
        return guests;
    }

    @PropertyName("guests")
    public void setGuests(int guests) {
        this.guests = guests;
    }

    @PropertyName("total_price")
    public double getTotalPrice() {
        return totalPrice;
    }

    @PropertyName("total_price")
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @PropertyName("status")
    public String getStatus() {
        return status;
    }

    @PropertyName("status")
    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(Timestamp now) {
    }

    public void setTimestamp(long l) {

    }
} 