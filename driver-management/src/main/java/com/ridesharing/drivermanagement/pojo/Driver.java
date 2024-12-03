package com.ridesharing.drivermanagement.pojo;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.Data;
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
}

