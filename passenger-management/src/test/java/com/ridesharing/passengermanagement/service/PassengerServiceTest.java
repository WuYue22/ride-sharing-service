package com.ridesharing.passengermanagement.service;

import com.ridesharing.common.pojo.Driver;
import com.ridesharing.common.pojo.RideRequest;
import com.ridesharing.common.pojo.RideStatus;
import com.ridesharing.common.pojo.RideType;
import com.ridesharing.common.repository.RideRequestRepository;
import com.ridesharing.passengermanagement.dto.LoginResponse;
import com.ridesharing.passengermanagement.dto.PassengerDto;
import com.ridesharing.passengermanagement.dto.RegisterRequest;
import com.ridesharing.passengermanagement.pojo.Passenger;
import com.ridesharing.passengermanagement.repository.PassengerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class PassengerServiceTest {
    @Mock
    PassengerRepository passengerRepository;
    @Mock
    RideRequestRepository rideRequestRepository;
    @Mock
    RestTemplate restTemplate;
    @InjectMocks
    PassengerService passengerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister() {
        // 初始化测试数据
        RegisterRequest validRequest = new RegisterRequest("validUsername", "validPassword");
        RegisterRequest invalidRequest = new RegisterRequest("existingUsername", "validPassword");
        // Arrange
        when(passengerRepository.existsByPassengerName(validRequest.getUsername())).thenReturn(false);

        // Act
        String result = passengerService.register(validRequest);

        // Assert
        Assertions.assertEquals("User registered successfully", result);
        verify(passengerRepository, times(1)).save(any(Passenger.class));  // 验证保存方法是否被调用
    }

    @Test
    void testSubmitRequest() {
        Passenger existingPassenger= new Passenger(1,"validUsername", "validPassword");;
        RideRequest expectedRideRequest = new RideRequest();
        expectedRideRequest.setPassengerId(1);
        expectedRideRequest.setRideType(RideType.STANDARD);
        expectedRideRequest.setPickupLocation("Location A");
        expectedRideRequest.setDropoffLocation("Location B");
        expectedRideRequest.setDistance(10.0);
        expectedRideRequest.setRideStatus(RideStatus.PENDING.name());
        // Arrange
        when(passengerRepository.findById(1)).thenReturn(java.util.Optional.of(existingPassenger));
        when(rideRequestRepository.save(any(RideRequest.class))).thenReturn(expectedRideRequest);

        // Act
        RideRequest result = passengerService.submitRequest(1, RideType.STANDARD, "Location A", "Location B", 10.0);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(RideStatus.PENDING.name(), result.getRideStatus());
        Assertions.assertEquals(1, result.getPassengerId());
        Assertions.assertEquals("Location A", result.getPickupLocation());
        Assertions.assertEquals("Location B", result.getDropoffLocation());
        verify(rideRequestRepository, times(1)).save(any(RideRequest.class));  // 验证保存方法是否被调用
    }

    @Test
    void testTrackRide() {
        RideRequest existingRideRequest = new RideRequest();
        // 初始化模拟数据
        existingRideRequest = new RideRequest();
        existingRideRequest.setRideRequestId(1);
        existingRideRequest.setDriverId(2);
        Driver mockDriver = new Driver();
        mockDriver.setId(2);
        mockDriver.setLatitude(40.7128);
        mockDriver.setLongitude(-74.0060);
        // Arrange
        when(rideRequestRepository.findById(1)).thenReturn(java.util.Optional.of(existingRideRequest));
        when(restTemplate.getForObject(anyString(), eq(Driver.class))).thenReturn(mockDriver);
        when(rideRequestRepository.save(any(RideRequest.class))).thenReturn(existingRideRequest);

        // Act
        RideRequest result = passengerService.trackRide(1);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(40.7128, result.getDriverLatitude());
        Assertions.assertEquals(-74.0060, result.getDriverLongitude());
        verify(rideRequestRepository, times(1)).save(any(RideRequest.class));
    }

    @Test
    void testConfirmRide() {
        RideRequest existingRideRequest = new RideRequest();
        existingRideRequest.setRideRequestId(1);
        existingRideRequest.setRideStatus(RideStatus.PENDING.name());
        // Arrange
        when(rideRequestRepository.findById(1)).thenReturn(java.util.Optional.of(existingRideRequest));
        when(rideRequestRepository.save(any(RideRequest.class))).thenReturn(existingRideRequest);

        // Act
        RideRequest result = passengerService.confirmRide(1);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(RideStatus.IN_PROGRESS.name(), result.getRideStatus());
        verify(rideRequestRepository, times(1)).save(any(RideRequest.class));
    }

    @Test
    void testCompleteRide() {
        RideRequest existingRideRequest = new RideRequest();
        existingRideRequest.setRideRequestId(1);
        existingRideRequest.setRideStatus(RideStatus.IN_PROGRESS.name());
        // Arrange
        when(rideRequestRepository.findById(1)).thenReturn(java.util.Optional.of(existingRideRequest));
        when(rideRequestRepository.save(any(RideRequest.class))).thenReturn(existingRideRequest);

        // Act
        RideRequest result = passengerService.completeRide(1);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(RideStatus.COMPLETED.name(), result.getRideStatus());
        verify(rideRequestRepository, times(1)).save(any(RideRequest.class));
    }

    @Test
    void testLoginSuccess() {
        // 初始化模拟数据
        Passenger existingPassenger = new Passenger();
        existingPassenger.setPassengerId(1);
        existingPassenger.setPassengerName("validUsername");
        existingPassenger.setPassengerPassword("validPassword");

        RegisterRequest validLoginRequest = new RegisterRequest();
        validLoginRequest.setUsername("validUsername");
        validLoginRequest.setPassword("validPassword");
        // Arrange
        when(passengerRepository.findByPassengerName("validUsername")).thenReturn(java.util.Optional.of(existingPassenger));

        // Act
        LoginResponse response = passengerService.login(validLoginRequest);

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getUserId());
        Assertions.assertEquals("Login successful", response.getMessage());
        verify(passengerRepository, times(1)).findByPassengerName("validUsername");
    }

    @Test
    void testLoginPasswordIncorrect() {
        // 初始化模拟数据
        Passenger existingPassenger = new Passenger();
        existingPassenger.setPassengerId(1);
        existingPassenger.setPassengerName("validUsername");
        existingPassenger.setPassengerPassword("validPassword");

        // Arrange
        RegisterRequest incorrectPasswordRequest = new RegisterRequest();
        incorrectPasswordRequest.setUsername("validUsername");
        incorrectPasswordRequest.setPassword("incorrectPassword");

        when(passengerRepository.findByPassengerName("validUsername")).thenReturn(java.util.Optional.of(existingPassenger));

        // Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            passengerService.login(incorrectPasswordRequest);
        });
        Assertions.assertEquals("Password is incorrect", exception.getMessage());
    }

    @Test
    void testFindRequestId() {
        // 初始化模拟数据
        RideRequest rideRequest1 = new RideRequest();
        rideRequest1.setRideRequestId(1);
        rideRequest1.setPassengerId(1);

        RideRequest rideRequest2 = new RideRequest();
        rideRequest2.setRideRequestId(2);
        rideRequest2.setPassengerId(1);
        // Arrange
        List<RideRequest> rideRequests = Arrays.asList(rideRequest1, rideRequest2);
        when(rideRequestRepository.findByPassengerId(1)).thenReturn(rideRequests);

        // Act
        Integer result = passengerService.findRequestId(1);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result); // Latest rideRequestId should be 2
        verify(rideRequestRepository, times(1)).findByPassengerId(1);
    }
}

