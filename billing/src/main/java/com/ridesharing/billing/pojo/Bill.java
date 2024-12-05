package com.ridesharing.billing.pojo;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;

@Table(name = "tb_bill")
@Data
@Entity
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rideRequestId;
    private Integer passengerId;
    private Integer driverId;
    private Double price;

    public Bill(Integer rideRequestId, Integer passengerId, Integer driverId, Double price) {
        this.rideRequestId=rideRequestId;
        this.passengerId=passengerId;
        this.driverId=driverId;
        this.price=price;
    }
    public Bill(){

    }
}
