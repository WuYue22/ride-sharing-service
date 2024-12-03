package com.ridesharing.passengermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class PassengerDto {
    private Integer passengerId;
    @NotBlank(message = "用户名不能为空")
    private String passengerName;
    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 12)
    private String passengerPassword;

    public Integer getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Integer passengerId) {
        this.passengerId = passengerId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerPassword() {
        return passengerPassword;
    }

    public void setPassengerPassword(String passengerPassword) {
        this.passengerPassword = passengerPassword;
    }

    @Override
    public String toString() {
        return "PassengerDto{" +
                "passengerName='" + passengerName + '\'' +
                ", passengerPassword='" + passengerPassword + '\'' +
                '}';
    }
}

