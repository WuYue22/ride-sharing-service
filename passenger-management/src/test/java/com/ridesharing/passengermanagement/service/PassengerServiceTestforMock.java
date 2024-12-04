package com.ridesharing.passengermanagement.service;

import com.ridesharing.common.pojo.RideRequest;
import com.ridesharing.common.repository.RideRequestRepository;
import com.ridesharing.drivermanagement.pojo.Driver;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@SpringBootTest
public class PassengerServiceTestforMock {
    @Mock
    private RideRequestRepository rideRequestRepository;
    @Mock
    private RestTemplate restTemplate;
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private PassengerService passengerService;
    @Value("${driver-service.base-url}")
    private String driverServiceBaseUrl;
    @Test
    void testTrackRide() throws Exception {
        // 准备数据
        Integer rideRequestId = 1;
        RideRequest rideRequest = new RideRequest();
        rideRequest.setRideRequestId(rideRequestId);
        rideRequest.setDriverId(123);

        Driver driver = new Driver();
        driver.setLatitude(40.7128);
        driver.setLongitude(-74.0060);

        // 模拟调用
        when(rideRequestRepository.findById(rideRequestId)).thenReturn(Optional.of(rideRequest));
        when(restTemplate.getForObject(anyString(), eq(Driver.class))).thenReturn(driver);
        when(rideRequestRepository.save(any(RideRequest.class))).thenReturn(rideRequest);

        // 执行方法
        RideRequest updatedRideRequest = passengerService.trackRide(rideRequestId);

        // 验证
        assertNotNull(updatedRideRequest);
        assertEquals(40.7128, updatedRideRequest.getDriverLatitude());
        assertEquals(-74.0060, updatedRideRequest.getDriverLongitude());
        verify(rideRequestRepository, times(1)).save(updatedRideRequest);
    }
    @Test
    void trackRide_shouldThrowException_whenRideRequestNotFound() {
        Integer rideRequestId = 1;
        when(rideRequestRepository.findById(rideRequestId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            passengerService.trackRide(rideRequestId);
        });
    }

}
