package com.ridesharing.passengermanagement.pojo;

import jakarta.persistence.*;
import lombok.Data;

@Table(name="tb_passenger")
@Entity
@Data
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer passengerId;
    private String passengerName;
    private String passengerPassword;


    @Override
    public String toString() {
        return "Passenger{" +
                "passengerId=" + passengerId +
                ", passengerName='" + passengerName + '\'' +
                ", passengerPassword='" + passengerPassword + '\'' +
                '}';
    }
}

