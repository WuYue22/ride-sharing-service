package com.ridesharing.passengermanagement.com.ridesharing.passengermanagement.controller;


import com.ridesharing.common.pojo.RideRequest;
import com.ridesharing.common.repository.RideRequestRepository;
import com.ridesharing.drivermanagement.pojo.Driver;
import com.ridesharing.passengermanagement.controller.PassengerController;
import com.ridesharing.passengermanagement.service.PassengerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PassengerControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate; // 用于执行 HTTP 请求
    @Autowired
    private RideRequestRepository rideRequestRepository; // 用于测试 RideRequest 数据存储
    @Autowired
    private PassengerService passengerService;

    @Test
    void trackRide_shouldReturnUpdatedRideRequest_whenValidRideRequestId() {
        // 创建并保存一个乘车请求
        Integer rideRequestId = 1;
        RideRequest rideRequest = new RideRequest();
        rideRequest.setRideRequestId(rideRequestId);
        rideRequest.setDriverId(1); // 假设司机 ID 为 123
        rideRequestRepository.save(rideRequest);

        // 执行实际的 POST 请求到 /track-ride
        ResponseEntity<RideRequest> response = restTemplate.postForEntity(
                "/api/passenger/track-ride?rideRequestId=" + rideRequestId,
                null, // 请求体为空
                RideRequest.class);

        // 验证返回的状态和数据是否符合预期
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(rideRequestId, response.getBody().getRideRequestId());
        assertNotNull(response.getBody().getDriverLatitude());
        assertNotNull(response.getBody().getDriverLongitude());
    }

    @Test
    void getDriverInfo_shouldReturnDriverDetails_whenValidDriverId() {

        Integer driverId = 1;

        // 执行实际的 GET 请求到 /driver/{driverId}
        ResponseEntity<Driver> response = restTemplate.getForEntity(
                "/api/passenger/driver/" + driverId,
                Driver.class);

        // 验证返回的状态和数据是否符合预期
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(driverId, response.getBody().getId());
        assertNotNull(response.getBody().getLatitude());
        assertNotNull(response.getBody().getLongitude());
    }
}


