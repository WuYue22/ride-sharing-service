package com.ridesharing.drivermanagement.pojo;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.Data;

import  com.ridesharing.common.pojo.RideType;
@Table(name="tb_driver")
@Data
@Entity
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String password;
    private RideType rideType;
    private Boolean isAvailable;
    private Double latitude;
    private Double longitude;

    public Driver( String name, String password, String standard) {
        this.username = name;
        this.password = password;
        this.rideType = RideType.valueOf(standard);
        this.isAvailable=true;
    }
    public Driver() {

    }
}

