package com.ridesharing.passengermanagement.dto;

import com.ridesharing.passengermanagement.pojo.RideType;
import lombok.Data;

@Data
public class RideRequestDto {
    private Integer rideRequestId;
    private Integer passengerId;
    private RideType rideType;
    private String pickupLocation;
    private String dropoffLocation;
    private String rideStatus;

}
