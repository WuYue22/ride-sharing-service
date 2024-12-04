package com.ridesharing.drivermanagement.pojo;

import lombok.Data;

@Data
public class DriverLocation {
    private Integer driverId;
    private Double latitude;
    private Double longitude;
}

