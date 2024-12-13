package com.ridesharing.billing.controller;

import com.ridesharing.billing.pojo.Bill;
import com.ridesharing.billing.service.BillService;
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

@WebMvcTest(BillController.class)
class BillControllerTest {
    @Mock
    BillService billService;
    @Mock
    RideRequestRepository rideRequestRepository;
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    BillController billController;
    private List<Bill> mockBills;
    @BeforeEach
    public void setUp() {
        // 初始化模拟账单数据
        mockBills = Arrays.asList(
                new Bill(1,1,1,1.0),
                new Bill(2,1,1,151.0)
        );
    }

    @Test
    public void testGetBillList_WhenBillsExist() throws Exception {
        // 模拟 billService.getBillsByPassengerId 返回账单列表
        when(billService.getBillsByPassengerId(1)).thenReturn(mockBills);

        // 执行GET请求并验证结果
        mockMvc.perform(get("/passenger/1"))
                .andExpect(status().isOk())  // 验证状态码200
                .andExpect(jsonPath("$.length()").value(mockBills.size()))  // 验证返回的账单数量
                .andExpect(jsonPath("$[0].amount").value(100.0))  // 验证第一个账单金额
                .andExpect(jsonPath("$[1].amount").value(150.0));  // 验证第二个账单金额
    }

    @Test
    public void testGetBillList_WhenNoBillsFound() throws Exception {
        // 模拟 billService.getBillsByPassengerId 返回一个空的列表
        when(billService.getBillsByPassengerId(2)).thenReturn(Arrays.asList());

        // 执行GET请求并验证结果
        mockMvc.perform(get("/passenger/2"))
                .andExpect(status().isOk())  // 验证状态码200
                .andExpect(jsonPath("$.length()").value(0));  // 验证返回的账单数量为0
    }

    @Test
    public void testGetBillList_WhenPassengerIdNotFound() throws Exception {
        // 模拟 billService.getBillsByPassengerId 抛出异常（如果需要）
        when(billService.getBillsByPassengerId(999)).thenThrow(new RuntimeException("Passenger not found"));

        // 执行GET请求并验证异常
        mockMvc.perform(get("/passenger/999"))
                .andExpect(status().isNotFound())  // 验证返回的状态码404
                .andExpect(jsonPath("$.message").value("Passenger not found"));
    }

    @Test
    void testGetDriverBillList() {
        when(billService.getBillsByDriverId(anyInt())).thenReturn(List.of(new Bill(Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Double.valueOf(0))));

        ResponseEntity<List<Bill>> result = billController.getDriverBillList(Integer.valueOf(0));
        Assertions.assertEquals(new ResponseEntity<List<Bill>>(List.of(new Bill(Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Double.valueOf(0))), null, 0), result);
    }

    @Test
    void testGetPrice() {
        when(billService.getPrice(anyInt())).thenReturn(new Bill(Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Double.valueOf(0)));

        ResponseEntity<Bill> result = billController.getPrice(Integer.valueOf(0));
        Assertions.assertEquals(new ResponseEntity<Bill>(new Bill(Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Double.valueOf(0)), null, 0), result);
    }

    @Test
    void testAddBill() {
        when(billService.addBill(anyInt(), anyInt(), anyInt(), anyDouble())).thenReturn(new Bill(null, null, null, Double.valueOf(0)));
        when(billService.getPrice(anyInt())).thenReturn(new Bill(null, null, null, Double.valueOf(0)));
        when(rideRequestRepository.findById(any(Integer.class))).thenReturn(null);

        ResponseEntity<Bill> result = billController.addBill(Integer.valueOf(0));
        Assertions.assertEquals(new ResponseEntity<Bill>(new Bill(null, null, null, Double.valueOf(0)), null, 0), result);
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme