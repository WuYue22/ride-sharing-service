package com.ridesharing.drivermanagement.service;

import com.ridesharing.common.pojo.RideRequest;
import com.ridesharing.common.pojo.RideStatus;
import com.ridesharing.common.pojo.RideType;
import com.ridesharing.common.repository.RideRequestRepository;
import com.ridesharing.drivermanagement.pojo.Driver;
import com.ridesharing.drivermanagement.pojo.DriverLocation;
import com.ridesharing.drivermanagement.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
@ComponentScan(basePackages = "com.ridesharing")
@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private RideRequestRepository rideRequestRepository;

    public Driver getDriverById(Integer driverId){
        return driverRepository.findById(driverId).orElseThrow(() -> new RuntimeException("Driver not found"));

    }
    // 1) 注册司机
    public Driver registerDriver(String username, String password, RideType rideType) {
        Driver driver = new Driver();
        driver.setUsername(username);
        driver.setPassword(password);
        driver.setRideType(rideType);
        driver.setIsAvailable(true); // 默认有空闲
        return driverRepository.save(driver);
    }
    public Driver registerDriver(Driver driver){
        driver.setIsAvailable(true); // 默认有空闲
        return driverRepository.save(driver);
    }

    // 2) 更新司机位置
    public Driver updateDriverLocation(Integer driverId, DriverLocation location) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        // 更新司机位置
        driver.setLatitude(location.getLatitude());
        driver.setLongitude(location.getLongitude());

        return driverRepository.save(driver);
    }

    // 3) 确认接单
    public RideRequest acceptRide(Integer driverId, Integer rideRequestId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        RideRequest rideRequest = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new RuntimeException("RideRequest not found"));

        if (!driver.getIsAvailable()) {
            throw new RuntimeException("Driver is not available");
        }

        // 更新司机状态
        driver.setIsAvailable(false);
        driverRepository.save(driver);
        //更新请求位置
        rideRequest.setDriverLatitude(driver.getLatitude());
        rideRequest.setDriverLongitude(driver.getLongitude());
        // 更新乘车请求的状态
        rideRequest.setRideStatus(RideStatus.IN_PROGRESS.name());
        return rideRequestRepository.save(rideRequest);
    }

    // 4) 完成行程
    public RideRequest completeRide(Integer rideRequestId) {
        RideRequest rideRequest = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new RuntimeException("RideRequest not found"));

        rideRequest.setRideStatus(RideStatus.COMPLETED.name());
        // 将司机设置为空闲
        Driver driver = driverRepository.findById(rideRequest.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        driver.setIsAvailable(true);
        driverRepository.save(driver);

        return rideRequestRepository.save(rideRequest);
    }
}
