package com.ridesharing.passengermanagement.controller;



import com.ridesharing.common.pojo.RideRequest;
import com.ridesharing.common.pojo.RideType;
import com.ridesharing.drivermanagement.pojo.Driver;
import com.ridesharing.passengermanagement.dto.PassengerDto;
import com.ridesharing.passengermanagement.dto.ResponseMessage;
import com.ridesharing.passengermanagement.pojo.Passenger;
import com.ridesharing.passengermanagement.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/passenger")
public class PassengerController {

    @Autowired
    private PassengerService passengerService;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${driver-service.base-url}")
    private String driverServiceBaseUrl;

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<Driver> getDriverInfo(@PathVariable Integer driverId) {
        String url = driverServiceBaseUrl + "/api/driver/" + driverId;
        Driver driver = restTemplate.getForObject(url, Driver.class);
        return ResponseEntity.ok(driver);
    }

    //add
    @PostMapping
    public ResponseMessage<Passenger> addPassenger(@Validated @RequestBody PassengerDto passenger) {

        Passenger passenger1= passengerService.add(passenger);
        return ResponseMessage.success(passenger1);
    }

    //query
    @GetMapping("/{passengerId}")
    public ResponseMessage getPassenger(@PathVariable Integer passengerId) {
        Passenger passenger1=passengerService.get(passengerId);
        return ResponseMessage.success(passenger1);
    }
    //update
    @PutMapping
    public ResponseMessage updatePassenger(@Validated @RequestBody PassengerDto passenger) {
        Passenger passenger1=passengerService.edit(passenger);
        return ResponseMessage.success(passenger1);
    }
    //delete
    @DeleteMapping("/{passengerId}")
    public ResponseMessage deletePassenger(@PathVariable Integer passengerId) {
        passengerService.delete(passengerId);
        return ResponseMessage.success(null);
    }

    // 1) 搜索乘车
    @GetMapping("/search")
    public ResponseEntity<List<RideRequest>> searchRide(
            @RequestParam String pickupLocation,
            @RequestParam String dropoffLocation) {
        List<RideRequest> rides = passengerService.searchRide(pickupLocation, dropoffLocation);
        return ResponseEntity.ok(rides);
    }

    // 2) 选择乘车类型
    @PostMapping("/choose-ride")
    public ResponseEntity<RideRequest> chooseRideType(
            @RequestParam Integer passengerId,
            @RequestParam RideType rideType,
            @RequestParam String pickupLocation,
            @RequestParam String dropoffLocation) {
        RideRequest rideRequest = passengerService.chooseRideType(passengerId, rideType, pickupLocation, dropoffLocation);
        return ResponseEntity.ok(rideRequest);
    }
    // 3) 跟踪司机位置
    @PostMapping("/track-ride")
    public ResponseEntity<RideRequest> trackRide(@RequestParam Integer rideRequestId) {
        RideRequest rideRequest = passengerService.trackRide(rideRequestId);
        return ResponseEntity.ok(rideRequest);
    }
    // 4) 确认乘车
    @PostMapping("/confirm-ride")
    public ResponseEntity<RideRequest> confirmRide(@RequestParam Integer rideRequestId) {
        RideRequest updatedRideRequest = passengerService.confirmRide(rideRequestId);
        return ResponseEntity.ok(updatedRideRequest);
    }

    // 5) 完成乘车
    @PostMapping("/complete-ride")
    public ResponseEntity<RideRequest> completeRide(@RequestParam Integer rideRequestId) {
        RideRequest completedRideRequest = passengerService.completeRide(rideRequestId);
        return ResponseEntity.ok(completedRideRequest);
    }

}
