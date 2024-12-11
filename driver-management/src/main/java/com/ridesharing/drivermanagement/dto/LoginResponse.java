package com.ridesharing.drivermanagement.dto;


import com.ridesharing.common.pojo.RideType;

public class LoginResponse {
    private Integer userId;
    private RideType rideType;
    private String message;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RideType getRideType() {
        return rideType;
    }

    public void setRideType(RideType rideType) {
        this.rideType = rideType;
    }
}

