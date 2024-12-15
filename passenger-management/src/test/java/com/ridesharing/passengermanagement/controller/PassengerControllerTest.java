package com.ridesharing.passengermanagement.controller;

import com.ridesharing.billing.pojo.Bill;
import com.ridesharing.common.pojo.RideRequest;
import com.ridesharing.common.pojo.RideType;
import com.ridesharing.passengermanagement.dto.PassengerDto;
import com.ridesharing.passengermanagement.dto.ResponseMessage;
import com.ridesharing.passengermanagement.pojo.Passenger;
import com.ridesharing.passengermanagement.service.PassengerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PassengerControllerTest {
    @Mock
    PassengerService passengerService;
    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    PassengerController passengerController;
    private Integer rideRequestId;
    private RideRequest rideRequest;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rideRequestId = 100; // 假设的 rideRequestId
        rideRequest = new RideRequest();
        rideRequest.setRideRequestId(rideRequestId);
        rideRequest.setPassengerId(1);
        rideRequest.setDriverId(2);
        rideRequest.setRideType(RideType.STANDARD);
        rideRequest.setPickupLocation("Location A");
        rideRequest.setDriverLatitude(40.7128);
        rideRequest.setDriverLongitude(-74.0060);
        rideRequest.setDropoffLocation("Location B");
        rideRequest.setRideStatus("CONFIRMED"); // 假设状态为已确认
        rideRequest.setDistance(10.0);
    }

    @Test
    void testSubmitRequest() {
        rideRequest.setRideRequestId(1); // 假设 rideRequestId 由数据库自动生成
        rideRequest.setRideStatus("PENDING"); // 设置乘车状态
        // 设置 passengerService.submitRequest 的返回值
        RideRequest mockedResponse = new RideRequest();
        mockedResponse.setPassengerId(1);
        mockedResponse.setRideType(RideType.STANDARD);
        mockedResponse.setPickupLocation("Location A");
        mockedResponse.setDropoffLocation("Location B");
        mockedResponse.setDistance(10.0);

        when(passengerService.submitRequest(
                anyInt(),
                any(RideType.class),
                anyString(),
                anyString(),
                anyDouble()
        )).thenReturn(mockedResponse);

        // 调用 controller 方法并验证
        ResponseEntity<RideRequest> response = passengerController.submitRequest(rideRequest);

        // 断言返回的内容
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(rideRequest.getPassengerId(), response.getBody().getPassengerId());
        assertEquals(rideRequest.getRideType(), response.getBody().getRideType());
        assertEquals(rideRequest.getPickupLocation(), response.getBody().getPickupLocation());
        assertEquals(rideRequest.getDropoffLocation(), response.getBody().getDropoffLocation());
        assertEquals(rideRequest.getDistance(), response.getBody().getDistance());
    }

    @Test
    void testTrackRide() {
        rideRequest.setRideStatus("PENDING");
        rideRequest.setDistance(10.0);

        // 模拟 passengerService.findRequestId 方法的返回值
        when(passengerService.findRequestId(1)).thenReturn(rideRequestId);

        // 模拟 passengerService.trackRide 方法的返回值
        when(passengerService.trackRide(rideRequestId)).thenReturn(rideRequest);

        // 调用 controller 方法并验证
        ResponseEntity<RideRequest> response = passengerController.trackRide(1);

        // 断言返回的内容
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(rideRequestId, response.getBody().getRideRequestId());
        assertEquals(rideRequest.getPassengerId(), response.getBody().getPassengerId());
        assertEquals(rideRequest.getDriverId(), response.getBody().getDriverId());
        assertEquals(rideRequest.getRideType(), response.getBody().getRideType());
        assertEquals(rideRequest.getPickupLocation(), response.getBody().getPickupLocation());
        assertEquals(rideRequest.getDriverLatitude(), response.getBody().getDriverLatitude());
        assertEquals(rideRequest.getDriverLongitude(), response.getBody().getDriverLongitude());
        assertEquals(rideRequest.getDropoffLocation(), response.getBody().getDropoffLocation());
        assertEquals(rideRequest.getRideStatus(), response.getBody().getRideStatus());
        assertEquals(rideRequest.getDistance(), response.getBody().getDistance());
    }

    @Test
    void testConfirmRide() {
        // 模拟 passengerService.confirmRide 方法的返回值
        when(passengerService.confirmRide(rideRequestId)).thenReturn(rideRequest);

        ResponseEntity<RideRequest> response = passengerController.confirmRide(rideRequestId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(rideRequestId, response.getBody().getRideRequestId());
        assertEquals(rideRequest.getPassengerId(), response.getBody().getPassengerId());
        assertEquals(rideRequest.getDriverId(), response.getBody().getDriverId());
        assertEquals(rideRequest.getRideType(), response.getBody().getRideType());
        assertEquals(rideRequest.getPickupLocation(), response.getBody().getPickupLocation());
        assertEquals(rideRequest.getDriverLatitude(), response.getBody().getDriverLatitude());
        assertEquals(rideRequest.getDriverLongitude(), response.getBody().getDriverLongitude());
        assertEquals(rideRequest.getDropoffLocation(), response.getBody().getDropoffLocation());
        assertEquals(rideRequest.getRideStatus(), response.getBody().getRideStatus());
        assertEquals(rideRequest.getDistance(), response.getBody().getDistance());
    }

    @Test
    void testCompleteRide() {
        // 模拟 passengerService.completeRide 方法的返回值
        when(passengerService.completeRide(rideRequestId)).thenReturn(rideRequest);

        // 调用 controller 方法并验证
        ResponseEntity<RideRequest> response = passengerController.completeRide(rideRequestId);

        // 断言返回的内容
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(rideRequestId, response.getBody().getRideRequestId());
        assertEquals(rideRequest.getPassengerId(), response.getBody().getPassengerId());
        assertEquals(rideRequest.getDriverId(), response.getBody().getDriverId());
        assertEquals(rideRequest.getRideType(), response.getBody().getRideType());
        assertEquals(rideRequest.getPickupLocation(), response.getBody().getPickupLocation());
        assertEquals(rideRequest.getDriverLatitude(), response.getBody().getDriverLatitude());
        assertEquals(rideRequest.getDriverLongitude(), response.getBody().getDriverLongitude());
        assertEquals(rideRequest.getDropoffLocation(), response.getBody().getDropoffLocation());
        assertEquals(rideRequest.getRideStatus(), response.getBody().getRideStatus());
        assertEquals(rideRequest.getDistance(), response.getBody().getDistance());
    }

    @Test
    void testGetBillList() {
        Integer passengerId=1;
        // 设置数据
        Bill bill1 = new Bill();
        bill1.setRideRequestId(101);
        bill1.setPrice(50.0);

        Bill bill2 = new Bill();
        bill2.setRideRequestId(102);
        bill2.setPrice(30.0);
        List<Bill> billList = Arrays.asList(bill1, bill2);

        // 模拟 restTemplate.exchange 方法的返回值
        when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.isNull(),
                Mockito.any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(billList));

        // 调用 controller 方法并验证
        ResponseEntity<List<Bill>> response = passengerController.getBillList(passengerId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals(billList.get(0).getRideRequestId(), response.getBody().get(0).getRideRequestId());
        assertEquals(billList.get(1).getPrice(), response.getBody().get(1).getPrice());
    }
}

