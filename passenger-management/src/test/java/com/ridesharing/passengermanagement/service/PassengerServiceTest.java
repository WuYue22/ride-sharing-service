package com.ridesharing.passengermanagement.service;

import static org.junit.jupiter.api.Assertions.*;
import com.ridesharing.common.pojo.RideRequest;
import com.ridesharing.common.pojo.RideStatus;
import com.ridesharing.common.pojo.RideType;
import com.ridesharing.common.repository.RideRequestRepository;
import com.ridesharing.passengermanagement.pojo.*;
import com.ridesharing.passengermanagement.repository.PassengerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class PassengerServiceTest {

    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private RideRequestRepository rideRequestRepository;
    @Autowired
    private PassengerService passengerService;

    private Passenger passenger;

    @BeforeEach
    void setUp() {
        // 设置测试数据并保存到数据库
        passenger = new Passenger();
        passenger.setPassengerName("testPassenger");
        passenger.setPassengerPassword("password");
        passengerRepository.save(passenger);
    }

    /*@Test
    void testAdd() {
        PassengerDto passenger1 = new PassengerDto();
        passenger1.setPassengerName("testPassenger2");
        passenger1.setPassengerPassword("password2");
        (Passenger)passenger1=passengerService.add(passenger1);
        assertNotNull(passenger1);
        assertEquals("testPassenger2", passenger1.getPassengerName());
        assertEquals("password2", passenger1.getPassengerPassword());
    }*/
    @Test
    void testAdd() {
        Passenger passenger2 =passengerService.add("testPassenger2","password2");
        assertNotNull(passenger2);
        assertEquals("testPassenger2", passenger2.getPassengerName());
        assertEquals("password2", passenger2.getPassengerPassword());
    }
    @Test
    void testChooseRideType() {
        RideRequest rideRequest = passengerService.chooseRideType(1, RideType.STANDARD, "Location A", "Location B");
        assertNotNull(rideRequest);
        assertEquals(RideStatus.PENDING.name(), rideRequest.getRideStatus());
        assertEquals("Location A", rideRequest.getPickupLocation());
        assertEquals("Location B", rideRequest.getDropoffLocation());
    }

    @Test
    void testSearchRide() {
        passengerService.chooseRideType(1, RideType.STANDARD, "Location A", "Location B");
        List<RideRequest> rideRequests = passengerService.searchRide( "Location A", "Location B");

        assertNotNull(rideRequests);
        assertFalse(rideRequests.isEmpty());
    }

    /*@Test
    void testTrackRide() {
        RideRequest rideRequest = new RideRequest();
        rideRequest.setPassengerId(1);
        rideRequest.setDriverLatitude(40.7128);
        rideRequest.setDriverLongitude(74.0060);

        RideRequest updatedRideRequest = passengerService.trackRide(1);

        assertNotNull(updatedRideRequest);
        assertEquals(40.7128, updatedRideRequest.getDriverLatitude());
        assertEquals(74.0060, updatedRideRequest.getDriverLongitude());
    }*/


    @Test
    void testConfirmRide() {
        RideRequest rideRequest = new RideRequest();
        rideRequest.setRideRequestId(1);
        rideRequest.setRideStatus(RideStatus.PENDING.name());
        RideRequest confirmedRideRequest = passengerService.confirmRide(1);

        assertNotNull(confirmedRideRequest);
        assertEquals(RideStatus.IN_PROGRESS.name(), confirmedRideRequest.getRideStatus());
    }
}
