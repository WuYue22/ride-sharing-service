package com.ridesharing.passengermanagement.service;


import com.ridesharing.common.pojo.*;
import com.ridesharing.common.pojo.RideType;
import com.ridesharing.passengermanagement.dto.PassengerDto;
import com.ridesharing.passengermanagement.pojo.*;
import com.ridesharing.passengermanagement.repository.PassengerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import com.ridesharing.common.repository.RideRequestRepository;
import java.util.List;
@ComponentScan(basePackages = "com.ridesharing")
@Service
public class PassengerService implements IPassengerService {
    @Autowired
    PassengerRepository passengerRepository;
    @Autowired
    RideRequestRepository rideRequestRepository;
    @Override
    public Passenger add(PassengerDto passenger) {
        Passenger passengerPojo = new Passenger();
        BeanUtils.copyProperties(passenger, passengerPojo);
        return passengerRepository.save(passengerPojo);
    }

    @Override
    public Passenger add(String passengerName, String passengerPassword) {
        Passenger passenger = new Passenger();
        passenger.setPassengerName(passengerName);
        passenger.setPassengerPassword(passengerPassword);
        return passengerRepository.save(passenger);
    }
    @Override
    public Passenger get(Integer passengerId) {
        return passengerRepository.findById(passengerId).orElseThrow(() -> {
            throw new IllegalArgumentException("Passenger Not Found");
        });
    }
    @Override
    public Passenger edit(PassengerDto passenger) {
        Passenger passengerPojo = new Passenger();
        BeanUtils.copyProperties(passenger, passengerPojo);
        return passengerRepository.save(passengerPojo);
    }

    @Override
    public void delete(Integer passengerId) {
        passengerRepository.deleteById(passengerId);
    }

    // 1) 搜索乘车
    public List<RideRequest> searchRide(String pickupLocation, String dropoffLocation) {
        // 查找匹配的乘车请求
        return rideRequestRepository.findByPickupLocationAndDropoffLocation(pickupLocation, dropoffLocation);
    }

    // 2) 选择乘车类型
    public RideRequest chooseRideType(Integer passengerId, RideType rideType, String pickupLocation, String dropoffLocation) {
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException("Passenger not found"));

        RideRequest rideRequest = new RideRequest();
        rideRequest.setPassengerId(passengerId);
        rideRequest.setRideType(rideType);
        rideRequest.setPickupLocation(pickupLocation);
        rideRequest.setDropoffLocation(dropoffLocation);
        rideRequest.setRideStatus(RideStatus.PENDING.name());
        return rideRequestRepository.save(rideRequest);
    }
    // 3) 跟踪乘车位置
    public RideRequest trackRide(Integer passengerId) {
        return rideRequestRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException("RideRequest not found"));
    }
    // 4) 确认乘车
    public RideRequest confirmRide(Integer rideRequestId) {
        RideRequest rideRequest = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new RuntimeException("RideRequest not found"));

        rideRequest.setRideStatus(RideStatus.IN_PROGRESS.name());
        return rideRequestRepository.save(rideRequest);
    }

    // 5) 完成乘车
    public RideRequest completeRide(Integer rideRequestId) {
        RideRequest rideRequest = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new RuntimeException("RideRequest not found"));

        rideRequest.setRideStatus(RideStatus.COMPLETED.name());
        return rideRequestRepository.save(rideRequest);
    }

}
