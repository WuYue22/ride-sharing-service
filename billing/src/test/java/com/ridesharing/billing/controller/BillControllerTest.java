package com.ridesharing.billing.controller;

import com.ridesharing.billing.pojo.Bill;
import com.ridesharing.billing.service.BillService;
import com.ridesharing.common.pojo.RideRequest;
import com.ridesharing.common.repository.RideRequestRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class BillControllerTest {
    @Mock
    BillService billService;
    @Mock
    RideRequestRepository rideRequestRepository;

    @InjectMocks
    BillController billController;
    private List<Bill> mockBills;

    @BeforeEach
    public void setUp() {
        // 初始化模拟账单数据
        mockBills = Arrays.asList(
                new Bill(1,1,1,1.0),
                new Bill(2,1,1,150.0)
        );
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetBillList() throws Exception {
        Integer passengerId = 1;  // 假设测试乘客ID为1
        // 模拟 billService.getBillsByPassengerId 返回账单列表
        when(billService.getBillsByPassengerId(passengerId)).thenReturn(mockBills);
        // Act
        ResponseEntity<List<Bill>> response = billController.getBillList(passengerId);
        // Assert
        Assertions.assertEquals(200, response.getStatusCodeValue());  // 确保返回的状态码是200 OK
        Assertions.assertEquals(2, response.getBody().size());  // 确保返回的账单数量正确
        Assertions.assertEquals(1.0, response.getBody().get(0).getPrice());  // 确保第一个账单金额正确
        Assertions.assertEquals(150.0, response.getBody().get(1).getPrice());  // 确保第二个账单金额正确
    }


    @Test
    void testGetDriverBillList() {
        Integer driverId = 1;
        when(billService.getBillsByDriverId(driverId)).thenReturn(mockBills);
        // Act
        ResponseEntity<List<Bill>> response = billController.getDriverBillList(driverId);
        // Assert
        Assertions.assertEquals(200, response.getStatusCodeValue());  // 确保返回的状态码是200 OK
        Assertions.assertEquals(2, response.getBody().size());  // 确保返回的账单数量正确
        Assertions.assertEquals(1.0, response.getBody().get(0).getPrice());  // 确保第一个账单金额正确
        Assertions.assertEquals(150.0, response.getBody().get(1).getPrice());  // 确保第二个账单金额正确
    }

    @Test
    void testGetPrice() {
        Integer rideRequestId = 1;
        Bill priceBill = new Bill(1, 1,1, 50.0);
        when(billService.getPrice(rideRequestId)).thenReturn(priceBill);
        // Act
        ResponseEntity<Bill> response = billController.getPrice(rideRequestId);
        // Assert
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertEquals(50.0, response.getBody().getPrice());
    }

    @Test
    void testAddBill() {
        // Arrange
        Integer rideRequestId = 1;
        Integer passengerId = 1;
        Integer driverId = 2;
        Double price = 50.0;
        // 模拟RideRequest
        RideRequest rideRequest = new RideRequest();
        rideRequest.setPassengerId(passengerId);
        rideRequest.setDriverId(driverId);
        // 模拟rideRequestRepository的行为
        when(rideRequestRepository.findById(rideRequestId)).thenReturn(java.util.Optional.of(rideRequest));
        // 模拟billService.getPrice返回价格
        Bill priceBill = new Bill(1, passengerId, driverId,price);
        when(billService.getPrice(rideRequestId)).thenReturn(priceBill);
        // 模拟billService.addBill返回账单
        Bill newBill = new Bill(1, passengerId,driverId, price);
        when(billService.addBill(passengerId, driverId, rideRequestId, price)).thenReturn(newBill);
        // Act
        ResponseEntity<Bill> response = billController.addBill(rideRequestId);
        // Assert
        Assertions.assertEquals(200, response.getStatusCodeValue());  // 确保返回的状态码是200 OK
        Assertions.assertEquals(passengerId, response.getBody().getPassengerId());  // 确保返回的账单乘客ID正确
        Assertions.assertEquals(driverId, response.getBody().getDriverId());  // 确保返回的账单司机ID正确
        Assertions.assertEquals(price, response.getBody().getPrice());  // 确保返回的账单金额正确
    }
}
