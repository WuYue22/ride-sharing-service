package com.ridesharing.drivermanagement.pojo;

import lombok.Data;

@Data
public class DriverLocation {
    private Integer driverId;
    private Double latitude;
    private Double longitude;
    public DriverLocation() {}
    public DriverLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude= longitude;
    }
}

