package com.ridesharing.passengermanagement.controller;


import com.ridesharing.billing.pojo.Bill;
import com.ridesharing.common.pojo.RideRequest;
import com.ridesharing.common.pojo.RideType;
import com.ridesharing.common.repository.RideRequestRepository;
import com.ridesharing.common.pojo.Driver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PassengerControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate; // 用于执行 HTTP 请求
    @Autowired
    private RideRequestRepository rideRequestRepository; // 用于测试 RideRequest 数据存储

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

    //测试前启动billing微服务，并且向其中加入了passengerId=1的数据
    @Test
    void getBillList() {

        Integer passengerId = 1;

        // 执行 GET 请求到 /bill/passenger/{passengerId}
        ResponseEntity<List<Bill>> response = restTemplate.exchange(
                "/api/passenger/bill/" + passengerId,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Bill>>() {});

        // 验证返回的状态和数据是否符合预期
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
    //测试前启动billing微服务
    @Test
    void getPrice() {
        Integer rideRequestId = 1;
        RideRequest rideRequest=new RideRequest();
        rideRequest.setRideRequestId(rideRequestId);
        rideRequest.setPassengerId(1);
        rideRequest.setDistance(100.0);
        rideRequest.setRideType(RideType.STANDARD);
        rideRequestRepository.save(rideRequest);
        // 执行 GET 请求
        ResponseEntity<Bill> response = restTemplate.getForEntity(
                "/api/passenger/price/" + rideRequestId, Bill.class);

        // 验证返回的状态和数据是否符合预期
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(rideRequestId, response.getBody().getRideRequestId());
    }
}


