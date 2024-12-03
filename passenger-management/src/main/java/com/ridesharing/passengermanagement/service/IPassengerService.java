package com.ridesharing.passengermanagement.service;


import com.ridesharing.passengermanagement.dto.PassengerDto;
import com.ridesharing.passengermanagement.pojo.Passenger;

public interface IPassengerService {

    Passenger add(PassengerDto passenger);

    Passenger add(String passengerName, String passengerPassword);

    /**
     * 查询乘客
     * @param passengerId
     * @return
     */
    Passenger get(Integer passengerId);

    /**
     * 修改
     * @param passenger
     * @return
     */
    Passenger edit(PassengerDto passenger);

    /**
     * 删除
     * @param passengerId
     * @return
     */
    void delete(Integer passengerId);
}
