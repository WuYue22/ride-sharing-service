package com.ridesharing.billing.pojo;

import jakarta.persistence.*;
import lombok.Data;

@Table(name = "tb_bill")
@Data
@Entity
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer RideRequestId;
    private Integer passengerId;
    private Integer driverId;
    private Double price;
}
