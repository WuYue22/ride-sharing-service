package com.ridesharing.passengermanagement.dto;

public class RideNotification {
    private String status; // 状态，eg. "ACCEPTED"
    private Integer driverId;

    public RideNotification(String status, Integer driverId) {
        this.status = status;
        this.driverId = driverId;
    }
    public RideNotification(){}

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }
}

