package com.ridesharing.passengermanagement.com.ridesharing.passengermanagement.controller;

import com.ridesharing.common.pojo.RideRequest;
import com.ridesharing.common.pojo.RideStatus;
import com.ridesharing.common.pojo.RideType;
import com.ridesharing.passengermanagement.controller.PassengerController;
import com.ridesharing.passengermanagement.pojo.Passenger;
import com.ridesharing.passengermanagement.service.PassengerService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
//@ContextConfiguration(classes= PassengerController.class)
@AutoConfigureMockMvc
@SpringBootTest
public class PassengerControllerTest {

    @Autowired
    private MockMvc mockMvc;  // 用于模拟 HTTP 请求和响应

    @Autowired
    private PassengerService passengerService;

    private Passenger passenger;

    @BeforeEach
    void setUp() {
        passenger = new Passenger();
        passenger.setPassengerId(1);
        passenger.setPassengerName("testPassenger");
        passenger.setPassengerPassword("password");
    }

    @Test
    void testSearchRide() throws Exception {
        // 模拟服务层返回数据
        //when(passengerService.searchRide("Location A", "Location B"))
          //      .thenReturn(List.of(new RideRequest()));
        passengerService.searchRide("Location A", "Location B");
        // 模拟 HTTP 请求并验证响应
        mockMvc.perform(get("/passenger/search")
                        .param("pickupLocation", "Location A")
                        .param("dropoffLocation", "Location B"))
                .andExpect(status().isOk())  // 验证返回状态码是 200
                .andExpect(jsonPath("$").isArray())  // 验证返回是一个数组
                .andExpect(jsonPath("$[0].pickupLocation").value("Location A"));
    }

    @Test
    void testChooseRide() throws Exception {
        // 模拟服务层返回数据
        RideRequest rideRequest = new RideRequest();
        rideRequest.setRideStatus(RideStatus.PENDING.name());

        passengerService.chooseRideType(1, RideType.STANDARD, "Location A", "Location B");

        // 模拟 HTTP 请求并验证响应
        mockMvc.perform(post("/passenger/choose-ride")
                        .param("passengerId", "1")
                        .param("rideType", "STANDARD")
                        .param("pickupLocation", "Location A")
                        .param("dropoffLocation", "Location B"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rideStatus").value(RideStatus.PENDING.name()));
    }
}
