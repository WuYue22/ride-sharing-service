package com.ridesharing.passengermanagement.pojo;

import jakarta.persistence.*;
import lombok.Data;

@Table(name = "ride_request")
@Entity
@Data
public class RideRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rideRequestId;
    private Integer passengerId;
    private RideType rideType;
    private String pickupLocation;
    private Double driverLatitude;
    private Double driverLongitude;
    private String dropoffLocation;
    private String rideStatus;
}

