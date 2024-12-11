package com.ridesharing.drivermanagement.dto;

public class AcceptRequest {
    private Integer driverId;
    private Integer rideRequestId;

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Integer getRideRequestId() {
        return rideRequestId;
    }

    public void setRideRequestId(Integer rideRequestId) {
        this.rideRequestId = rideRequestId;
    }
}
