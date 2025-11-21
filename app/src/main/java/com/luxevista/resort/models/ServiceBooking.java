package com.luxevista.resort.models;

import com.google.firebase.firestore.PropertyName;

public class ServiceBooking {
    private String id;
    private String userId;
    private String serviceId;
    private String serviceName;
    private String bookingDate;
    private String bookingTime;
    private String status;
    private double price;
    private long timestamp;

    public ServiceBooking() {
        // Default constructor required for Firestore
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("userId")
    public String getUserId() {
        return userId;
    }

    @PropertyName("userId")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @PropertyName("serviceId")
    public String getServiceId() {
        return serviceId;
    }

    @PropertyName("serviceId")
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @PropertyName("serviceName")
    public String getServiceName() {
        return serviceName;
    }

    @PropertyName("serviceName")
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @PropertyName("bookingDate")
    public String getBookingDate() {
        return bookingDate;
    }

    @PropertyName("bookingDate")
    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    @PropertyName("bookingTime")
    public String getBookingTime() {
        return bookingTime;
    }

    @PropertyName("bookingTime")
    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    @PropertyName("status")
    public String getStatus() {
        return status;
    }

    @PropertyName("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @PropertyName("price")
    public double getPrice() {
        return price;
    }

    @PropertyName("price")
    public void setPrice(double price) {
        this.price = price;
    }

    @PropertyName("timestamp")
    public long getTimestamp() {
        return timestamp;
    }

    @PropertyName("timestamp")
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
} 