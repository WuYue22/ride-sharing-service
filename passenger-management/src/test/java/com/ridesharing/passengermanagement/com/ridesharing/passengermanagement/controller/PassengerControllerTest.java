package com.ridesharing.passengermanagement.com.ridesharing.passengermanagement.controller;

import com.ridesharing.common.pojo.RideRequest;
import com.ridesharing.common.pojo.RideStatus;
import com.ridesharing.common.pojo.RideType;
import com.ridesharing.drivermanagement.pojo.Driver;
import com.ridesharing.passengermanagement.controller.PassengerController;
import com.ridesharing.passengermanagement.pojo.Passenger;
import com.ridesharing.passengermanagement.service.PassengerService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import java.util.List;
//@ContextConfiguration(classes= PassengerController.class)
@AutoConfigureMockMvc
@SpringBootTest
public class PassengerControllerTest {

    @Autowired
    private MockMvc mockMvc;  // 用于模拟 HTTP 请求和响应

    @Autowired
    private PassengerService passengerService;
    @Autowired
    private RestTemplate restTemplate;  // 模拟 RestTemplate 服务

    @Value("${driver-service.base-url}")
    private String driverServiceBaseUrl;

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
        mockMvc.perform(get("/api/passenger/search")
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
        mockMvc.perform(post("/api/passenger/choose-ride")
                        .param("passengerId", "1")
                        .param("rideType", "STANDARD")
                        .param("pickupLocation", "Location A")
                        .param("dropoffLocation", "Location B"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rideStatus").value(RideStatus.PENDING.name()));
    }
    @Test
    public void testGetDriverInfo() throws Exception {
        Integer driverId = 1;
        Double expectedDriverLongtitude= -74.0060;
        Double expectedDriverLatitude = 40.7128;
        String expectedDriverName = "John Doe";
        RideType expectedRideType = RideType.STANDARD;

        // 创建一个模拟的 Driver 对象
        Driver mockDriver = new Driver();
        mockDriver.setId(driverId);
        mockDriver.setLongitude(expectedDriverLongtitude);
        mockDriver.setLatitude(expectedDriverLatitude);
        mockDriver.setUsername(expectedDriverName);
        mockDriver.setRideType(expectedRideType);

        // 模拟 RestTemplate 的行为，当调用 getForObject 时返回 mockDriver
        when(restTemplate.getForObject(eq(driverServiceBaseUrl + "/api/driver/" + driverId), eq(Driver.class)))
                .thenReturn(mockDriver);

        // 执行 GET 请求
        MvcResult result = mockMvc.perform(get("/api/passenger/driver/{driverId}", driverId))
                .andExpect(status().isOk())  // 验证 HTTP 状态码为 200
                .andExpect(jsonPath("$.username").value(expectedDriverName))  // 验证返回 JSON 中的字段
                .andExpect(jsonPath("$.rideType").value(expectedRideType.name()))// 验证返回 JSON 中的字段
                .andExpect(jsonPath("$.longitude").value(expectedDriverLongtitude))
                .andReturn();
    }
    @Test
    void testTrackRide() throws Exception {
        // 测试数据
        Integer rideRequestId = 1;
        Integer driverId = 1;
        Double expectedDriverLatitude = 40.7128;
        Double expectedDriverLongitude = -74.0060;
        // 模拟的 Driver 对象
        Driver mockDriver = new Driver();
        mockDriver.setId(driverId);
        mockDriver.setLatitude(expectedDriverLatitude);
        mockDriver.setLongitude(expectedDriverLongitude);

        // 创建一个模拟的 RideRequest 对象
        RideRequest mockRideRequest = new RideRequest();
        mockRideRequest.setDriverId(1);
        mockRideRequest.setDriverLatitude(expectedDriverLatitude);
        mockRideRequest.setDriverLongitude(expectedDriverLongitude);
        //passengerService.trackRide(rideRequestId);

        // 模拟 RestTemplate 的 getForObject 行为，返回 mockDriver 对象
        when(restTemplate.getForObject(eq(driverServiceBaseUrl +"/api/driver/" + driverId), eq(Driver.class)))
                .thenReturn(mockDriver);
        // 模拟 PassengerService 的 trackRide 方法
        when(passengerService.trackRide(rideRequestId)).thenReturn(mockRideRequest);

        // 执行 POST 请求，并验证响应
        mockMvc.perform(post("/api/passenger/track-ride")
                        .param("rideRequestId", rideRequestId.toString()))
                .andExpect(status().isOk())  // 验证返回的 HTTP 状态是 200 OK
                .andExpect(jsonPath("$.driverLatitude").value(expectedDriverLatitude))  // 验证返回的 JSON 中包含 driverLatitude
                .andExpect(jsonPath("$.driverLongitude").value(expectedDriverLongitude));  // 验证返回的 JSON 中包含 driverLongitude
    }
}
