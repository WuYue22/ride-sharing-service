package com.ridesharing.passengermanagement.controller;

import com.ridesharing.billing.pojo.Bill;
import com.ridesharing.common.pojo.AcceptRequest;
import com.ridesharing.common.pojo.RideRequest;
import com.ridesharing.common.repository.RideRequestRepository;
import com.ridesharing.passengermanagement.dto.RideNotification;
import com.ridesharing.passengermanagement.service.PassengerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;

class PassengerNotificationControllerTest {
    @Mock
    SimpMessagingTemplate messagingTemplate;
    @Mock
    RestTemplate restTemplate;
    @Mock
    PassengerService passengerService;
    @Mock
    RideRequestRepository rideRequestRepository;

    AcceptRequest acceptRequest;
    @InjectMocks
    PassengerNotificationController passengerNotificationController;
    @Value("${billing-service.base-url}")
    private String billingServiceBaseUrl;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 初始化测试数据
        acceptRequest = new AcceptRequest();
        acceptRequest.setDriverId(1);
        acceptRequest.setRideRequestId(1);
    }

    @Test
    void testNotifyPassenger() {
        // 调用 notifyPassenger 方法
        ResponseEntity<?> response = passengerNotificationController.notifyPassenger(acceptRequest);
        // 验证返回值是 200 OK
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testNotifyPassenger2() {
        // Arrange
        Integer requestId = 1;
        RideRequest rideRequest = new RideRequest();
        rideRequest.setDriverId(123);  // 设置 driverId，表示已分配司机

        // 模拟查询返回的 RideRequest
        when(rideRequestRepository.findById(requestId)).thenReturn(java.util.Optional.of(rideRequest));

        // Act
        ResponseEntity<RideNotification> response = passengerNotificationController.notifyPassenger(requestId);

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals("ACCEPTED", response.getBody().getStatus());
    }

    @Test
    void testGetPrice() {
        // Arrange
        Integer passengerId = 1;
        Integer rideRequestId = 100;  // 模拟获取的 rideRequestId
        Bill expectedBill = new Bill();  // 模拟返回的账单对象
        expectedBill.setPrice(50.0);  // 假设账单金额是50

        // 模拟 passengerService.findRequestId(passengerId)
        when(passengerService.findRequestId(passengerId)).thenReturn(rideRequestId);

        // 模拟 restTemplate.exchange 调用
        String url = billingServiceBaseUrl+"/bill/price/" + rideRequestId;
        ResponseEntity<Bill> responseEntity = new ResponseEntity<>(expectedBill, HttpStatus.OK);
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        // Act
        ResponseEntity<Bill> response = passengerNotificationController.getPrice(passengerId);

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(50.0, response.getBody().getPrice());

        // 验证 passengerService.findRequestId(passengerId) 是否被正确调用
        verify(passengerService).findRequestId(passengerId);

        // 验证 restTemplate.exchange 是否被正确调用
        verify(restTemplate).exchange(eq(url), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class));

    }
}
