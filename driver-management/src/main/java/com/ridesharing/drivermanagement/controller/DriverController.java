package com.ridesharing.drivermanagement.controller;

import com.ridesharing.billing.pojo.Bill;
import com.ridesharing.common.pojo.RideRequest;
import com.ridesharing.drivermanagement.pojo.Driver;
import com.ridesharing.drivermanagement.pojo.DriverLocation;
import com.ridesharing.drivermanagement.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/driver")
public class DriverController {

    @Autowired
    private DriverService driverService;
    @Value("${billing-service.base-url}")
    private String billingServiceBaseUrl;
    @Autowired
    RestTemplate restTemplate;
    @GetMapping("/{driverId}")
    public ResponseEntity<Driver> getDriverById(@PathVariable Integer driverId) {
        return ResponseEntity.ok(driverService.getDriverById(driverId));
    }

    // 1) 注册司机
    @PostMapping("/register")
    public ResponseEntity<Driver> registerDriver(@RequestBody Driver driver) {
        Driver newDriver = driverService.registerDriver(driver.getUsername(), driver.getPassword(), driver.getRideType());
        return ResponseEntity.ok(newDriver);
    }

    // 2) 更新司机位置
    @PostMapping("/update-location")
    public ResponseEntity<Driver> updateDriverLocation(@RequestBody DriverLocation driverLocation) {
        Driver updatedDriver = driverService.updateDriverLocation(driverLocation.getDriverId(), driverLocation);
        return ResponseEntity.ok(updatedDriver);
    }

    // 3) 接单
    @PostMapping("/accept-ride")
    public ResponseEntity<RideRequest> acceptRide(@RequestParam Integer driverId, @RequestParam Integer rideRequestId) {
        RideRequest updatedRideRequest = driverService.acceptRide(driverId, rideRequestId);
        return ResponseEntity.ok(updatedRideRequest);
    }

    // 4) 完成行程
    @PostMapping("/complete-ride")
    public ResponseEntity<Bill> completeRide(@RequestParam Integer rideRequestId) {
        RideRequest completedRideRequest = driverService.completeRide(rideRequestId);
        // 调用 Billing 模块生成账单
        String billingUrl = billingServiceBaseUrl + "/bill/add/" + rideRequestId;
        // 发送请求到 Billing 服务
        ResponseEntity<Bill> billResponse = restTemplate.getForEntity(billingUrl, Bill.class);
        return ResponseEntity.ok(billResponse.getBody());

    }
}

