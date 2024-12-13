package com.ridesharing.common.pojo;

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
    private Double latitude;
    private Double longitude;

    public Driver( String name, String password, String rideType) {
        this.username = name;
        this.password = password;
        this.rideType = RideType.valueOf(rideType);
        this.isAvailable=true;
    }
    public Driver() {

    }
}

