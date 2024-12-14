package com.ridesharing.billing.service;

import com.ridesharing.billing.pojo.Bill;
import com.ridesharing.billing.repository.BillRepository;
import com.ridesharing.common.pojo.RideRequest;
import com.ridesharing.common.pojo.RideType;
import com.ridesharing.common.repository.RideRequestRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class BillServiceTest {
    @Mock
    RideRequestRepository rideRequestRepository;
    @Mock
    BillRepository billRepository;
    @InjectMocks
    BillService billService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddBill() {
        //when(rideRequestRepository.save(any(RideRequest.class))).thenReturn(new RideRequest());
        Bill expectedBill = new Bill(0, 0, 0, 0d);
        when(billRepository.save(any(Bill.class))).thenReturn(expectedBill);

        Bill result = billService.addBill(Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), 0d);
        Assertions.assertEquals(new Bill(Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Double.valueOf(0)), result);
    }

    @Test
    void testGetPrice() {
        // 创建一个 RideRequest 对象，用于模拟数据库中的数据
        RideRequest rideRequest = new RideRequest();
        rideRequest.setDistance(10.0);  // 假设距离是 10 公里
        rideRequest.setRideType(RideType.STANDARD);  // 每公里价格是 0.2
        rideRequest.setPassengerId(1);  // 假设乘客 ID 为 1
        rideRequest.setDriverId(2);  // 假设司机 ID 为 2

        // 当查找时，返回一个有效的 RideRequest
        when(rideRequestRepository.findById(any(Integer.class))).thenReturn(Optional.of(rideRequest));
        // 模拟账单库中没有该账单
        when(billRepository.existsById(any(Integer.class))).thenReturn(false);
        // 模拟 addBill 方法返回一个新的账单
        Bill newBill = new Bill(1, 1, 2, 2.0);  // 新账单：乘客 ID = 1，司机 ID = 2，价格 = 20.0
        newBill.setTimestamp(LocalDateTime.now());
        when(billRepository.save(any(Bill.class))).thenReturn(newBill);
        //when(billService.addBill(anyInt(), anyInt(), anyInt(), anyDouble())).thenReturn(newBill);

        // 调用 getPrice 方法
        Bill result = billService.getPrice(Integer.valueOf(1));
        // 验证 billRepository.existsById() 被调用
        verify(billRepository, times(1)).existsById(any(Integer.class));
        // 验证返回的账单是否为新账单
        Assertions.assertEquals(newBill, result);
    }


    @Test
    void testGetBillsByPassengerId() {
        when(billRepository.findByPassengerId(anyInt())).thenReturn(List.of(new Bill(Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Double.valueOf(0))));

        List<Bill> result = billService.getBillsByPassengerId(Integer.valueOf(0));
        Assertions.assertEquals(List.of(new Bill(Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Double.valueOf(0))), result);
    }

    @Test
    void testGetBillsByDriverId() {
        when(billRepository.findByDriverId(anyInt())).thenReturn(List.of(new Bill(Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Double.valueOf(0))));

        List<Bill> result = billService.getBillsByDriverId(Integer.valueOf(0));
        Assertions.assertEquals(List.of(new Bill(Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Double.valueOf(0))), result);
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme