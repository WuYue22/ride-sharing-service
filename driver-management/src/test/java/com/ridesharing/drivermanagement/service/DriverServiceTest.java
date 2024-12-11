package com.ridesharing.drivermanagement.service;


import com.ridesharing.common.pojo.RideRequest;
import com.ridesharing.common.pojo.RideStatus;
import com.ridesharing.common.pojo.RideType;
import com.ridesharing.common.repository.RideRequestRepository;
import com.ridesharing.drivermanagement.pojo.Driver;
import com.ridesharing.drivermanagement.pojo.DriverLocation;
import com.ridesharing.drivermanagement.repository.DriverRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DriverServiceTest {

    @Autowired
    private DriverService driverService;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Test
    public void testUpdateDriverLocation() {
        Driver driver = new Driver();
        driver.setUsername("driver2");
        driver.setPassword("1234567890");
        driver.setRideType(RideType.SHARED);
        driver = driverRepository.save(driver);

        DriverLocation location = new DriverLocation();
        location.setDriverId(driver.getId());
        location.setLatitude(37.7749);
        location.setLongitude(-122.4194);

        Driver updatedDriver = driverService.updateDriverLocation(driver.getId(), location);

        assertEquals(37.7749, updatedDriver.getLatitude(), 0.01);
        assertEquals(-122.4194, updatedDriver.getLongitude(), 0.01);
    }

    @Test
    public void testAcceptRide() {
        Driver driver = new Driver();
        driver.setUsername("driver3");
        driver.setPassword("111111");
        driver.setRideType(RideType.PREMIUM);
        driver.setIsAvailable(true);
        driver = driverRepository.save(driver);

        RideRequest rideRequest = new RideRequest();
        rideRequest.setPassengerId(1);  // Assume passenger exists
        rideRequest.setRideType(RideType.PREMIUM);
        rideRequest.setPickupLocation("Location A");
        rideRequest.setDropoffLocation("Location B");
        rideRequest.setRideStatus(RideStatus.PENDING.name());
        rideRequest = rideRequestRepository.save(rideRequest);

        RideRequest updatedRideRequest = driverService.acceptRide(driver.getId(), rideRequest.getRideRequestId());
        // 重新从数据库中获取更新后的司机对象
        Driver updatedDriver = driverRepository.findById(driver.getId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        assertEquals(RideStatus.IN_PROGRESS.name(), updatedRideRequest.getRideStatus());
        assertFalse(updatedDriver.getIsAvailable());
    }
}

