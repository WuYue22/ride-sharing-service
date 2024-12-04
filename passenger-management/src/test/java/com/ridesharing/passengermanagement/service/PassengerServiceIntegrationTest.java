package com.ridesharing.passengermanagement.service;

import com.ridesharing.common.pojo.RideRequest;
import com.ridesharing.common.repository.RideRequestRepository;
import com.ridesharing.drivermanagement.pojo.Driver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PassengerServiceIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Autowired
    private PassengerService passengerService;

    @Test
    void trackRide_shouldRetrieveDriverLocation_fromDriverManagementService() {
        // 创建一个模拟的乘车请求
        Integer rideRequestId = 1;
        Integer driverId = 1;
        RideRequest rideRequest = new RideRequest();
        rideRequest.setRideRequestId(rideRequestId);
        rideRequest.setDriverId(driverId);

        // 保存乘车请求
        rideRequestRepository.save(rideRequest);

        // 模拟 driver-management 微服务的响应
        // 注意：`driver-management` 微服务应该在集成测试时已经启动
        ResponseEntity<Driver> responseEntity = testRestTemplate.exchange(
                "/api/driver/" + driverId, HttpMethod.GET, null, Driver.class);

        // 验证返回的司机信息是否正确
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(driverId, responseEntity.getBody().getId());
        assertEquals(40.0, responseEntity.getBody().getLatitude());//tb_driver数据库中的数据
        assertEquals(40.0, responseEntity.getBody().getLongitude());

        // 运行实际的业务逻辑来更新乘车请求中的司机信息
        RideRequest updatedRideRequest = passengerService.trackRide(rideRequestId);

        // 验证更新后的乘车请求信息是否与预期一致
        assertNotNull(updatedRideRequest);
        assertEquals(40.0, updatedRideRequest.getDriverLatitude());
        assertEquals(40.0, updatedRideRequest.getDriverLongitude());
    }
}

