package com.ridesharing.billing.pojo;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table(name = "tb_bill")
@Entity
@Data
public class Bill {
    @Id
    private Integer rideRequestId;
    private Integer passengerId;
    private Integer driverId;
    private Double price;
    private LocalDateTime timestamp;
    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }
    public Bill(Integer rideRequestId, Integer passengerId, Integer driverId, Double price) {
        this.rideRequestId=rideRequestId;
        this.passengerId=passengerId;
        this.driverId=driverId;
        this.price=price;

    }
    public Bill(){
    }

}
