package com.ridesharing.drivermanagement.controller;

import com.ridesharing.billing.pojo.Bill;
import com.ridesharing.common.pojo.AcceptRequest;
import com.ridesharing.common.pojo.Driver;
import com.ridesharing.common.pojo.RideRequest;
import com.ridesharing.common.pojo.RideType;
import com.ridesharing.common.repository.RideRequestRepository;
import com.ridesharing.drivermanagement.dto.LoginResponse;
import com.ridesharing.drivermanagement.dto.RideTypedto;
import com.ridesharing.drivermanagement.pojo.DriverLocation;
import com.ridesharing.drivermanagement.repository.DriverRepository;
import com.ridesharing.drivermanagement.service.DriverService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class DriverControllerTest {
    @Mock
    DriverService driverService;
    @Mock
    RestTemplate restTemplate;
    @Mock
    RideRequestRepository rideRequestRepository;
    @Mock
    DriverRepository driverRepository;
    @InjectMocks
    DriverController driverController;
    @Value("${billing-service.base-url}")
    private String billingServiceBaseUrl;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDriverById() {
        // 模拟返回的Driver对象
        Driver mockDriver = new Driver();
        mockDriver.setId(1);
        mockDriver.setUsername("John Doe");

        // 设置mock行为
        when(driverService.getDriverById(1)).thenReturn(mockDriver);

        // 调用controller方法
        ResponseEntity<Driver> response = driverController.getDriverById(1);

        // 验证返回的结果
        Assertions.assertNotNull(response); // 确保返回结果不为null
        Assertions.assertEquals(200, response.getStatusCodeValue()); // 验证返回的状态码是200
        Assertions.assertEquals(mockDriver, response.getBody()); // 验证返回的Driver对象
        Assertions.assertEquals("John Doe", response.getBody().getUsername()); // 验证Driver的name字段

        // 验证driverService.getDriverById被调用一次
        verify(driverService, times(1)).getDriverById(1);
    }

    @Test
    void testRegisterDriver_Success() {
        // 创建一个模拟的Driver对象
        Driver driver = new Driver();
        driver.setUsername("john_doe");
        driver.setPassword("password123");
        driver.setRideType(RideType.STANDARD);

        // 模拟driverService.register返回的结果
        String expectedMessage = "Driver registered successfully!";
        when(driverService.register(driver)).thenReturn(expectedMessage);

        // 调用controller的register方法
        ResponseEntity<String> response = driverController.register(driver);

        // 验证返回的结果
        Assertions.assertNotNull(response); // 确保返回结果不为null
        Assertions.assertEquals(200, response.getStatusCodeValue()); // 确保返回状态码是200
        Assertions.assertEquals(expectedMessage, response.getBody()); // 确保返回的消息正确

        // 验证driverService.register方法被调用一次
        verify(driverService, times(1)).register(driver);
    }

    @Test
    void testRegisterDriver_Failure() {
        // 创建一个模拟的Driver对象
        Driver driver = new Driver();
        driver.setUsername("john_doe");
        driver.setPassword("password123");
        driver.setRideType(RideType.STANDARD);

        // 模拟driverService.register抛出IllegalArgumentException异常
        String errorMessage = "Username already exists!";
        when(driverService.register(driver)).thenThrow(new IllegalArgumentException(errorMessage));

        // 调用controller的register方法
        ResponseEntity<String> response = driverController.register(driver);

        // 验证返回的结果
        Assertions.assertNotNull(response); // 确保返回结果不为null
        Assertions.assertEquals(400, response.getStatusCodeValue()); // 确保返回状态码是400 (Bad Request)
        Assertions.assertEquals(errorMessage, response.getBody()); // 确保返回的错误消息正确

        // 验证driverService.register方法被调用一次
        verify(driverService, times(1)).register(driver);
    }

    @Test
    void testLogin() {
        // 创建一个模拟的Driver对象
        Driver loginRequest = new Driver();
        loginRequest.setUsername("john_doe");
        loginRequest.setPassword("password123");

        // 创建模拟的LoginResponse
        LoginResponse mockResponse = new LoginResponse();
        mockResponse.setMessage("Login successful");
        mockResponse.setRideType(RideType.STANDARD);

        // 模拟driverService.login返回的结果
        when(driverService.login(loginRequest)).thenReturn(mockResponse);

        // 调用controller的login方法
        ResponseEntity<LoginResponse> response = driverController.login(loginRequest);

        // 验证返回的结果
        Assertions.assertNotNull(response); // 确保返回结果不为null
        Assertions.assertEquals(200, response.getStatusCodeValue()); // 确保返回状态码是200
        Assertions.assertEquals(mockResponse, response.getBody()); // 确保返回的LoginResponse与预期相同

        // 验证driverService.login方法被调用一次
        verify(driverService, times(1)).login(loginRequest);
    }


    @Test
    void testUpdateDriverLocation() {
        // 创建一个模拟的DriverLocation对象
        DriverLocation driverLocation = new DriverLocation();
        driverLocation.setDriverId(1);
        driverLocation.setLatitude(40.7128);
        driverLocation.setLongitude(-74.0060);

        // 创建一个模拟的更新后的Driver对象
        Driver updatedDriver = new Driver();
        updatedDriver.setId(1);
        updatedDriver.setUsername("John Doe");
        updatedDriver.setLatitude(driverLocation.getLatitude());
        updatedDriver.setLongitude(driverLocation.getLongitude());

        // 模拟driverService.updateDriverLocation返回更新后的Driver对象
        when(driverService.updateDriverLocation(driverLocation.getDriverId(), driverLocation)).thenReturn(updatedDriver);

        // 调用controller的updateDriverLocation方法
        ResponseEntity<Driver> response = driverController.updateDriverLocation(driverLocation);

        // 验证返回的结果
        Assertions.assertNotNull(response); // 确保返回结果不为null
        Assertions.assertEquals(200, response.getStatusCodeValue()); // 确保返回状态码是200
        Assertions.assertEquals(updatedDriver, response.getBody()); // 验证返回的Driver对象与预期相同
        Assertions.assertEquals(1, response.getBody().getId()); // 验证Driver的id
        Assertions.assertEquals(driverLocation.getLatitude(), response.getBody().getLatitude());
        Assertions.assertEquals(driverLocation.getLongitude(), response.getBody().getLongitude());

        // 验证driverService.updateDriverLocation方法被调用一次
        verify(driverService, times(1)).updateDriverLocation(driverLocation.getDriverId(), driverLocation);
    }

    @Test
    void testAcceptRide() {
        // 创建一个模拟的AcceptRequest对象
        AcceptRequest acceptRequest = new AcceptRequest();
        acceptRequest.setDriverId(1);
        acceptRequest.setRideRequestId(100);

        // 创建一个模拟的RideRequest对象
        RideRequest updatedRideRequest = new RideRequest();
        updatedRideRequest.setRideRequestId(100);
        updatedRideRequest.setDriverId(1);
        updatedRideRequest.setRideStatus("Accepted");

        // 创建一个模拟的查询结果
        RideRequest rideRequest = new RideRequest();
        rideRequest.setRideRequestId(100);
        rideRequest.setPassengerId(2); // 模拟乘客ID为2

        // 模拟依赖的方法
        when(driverService.acceptRide(1, 100)).thenReturn(updatedRideRequest);
        when(rideRequestRepository.findById(100)).thenReturn(java.util.Optional.of(rideRequest));

        // 模拟restTemplate调用，返回一个成功的ResponseEntity
        ResponseEntity<Void> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.postForEntity(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(Void.class))
        ).thenReturn(mockResponse);

        // 调用controller的acceptRide方法
        ResponseEntity<RideRequest> response = driverController.acceptRide(acceptRequest);

        // 验证返回的结果
        Assertions.assertNotNull(response); // 确保返回结果不为null
        Assertions.assertEquals(200, response.getStatusCodeValue()); // 确保返回状态码是200
        Assertions.assertEquals(updatedRideRequest, response.getBody()); // 验证返回的RideRequest对象与预期相同

        // 验证依赖的方法是否被调用
        verify(driverService, times(1)).acceptRide(1, 100); // 验证acceptRide是否被调用一次
        verify(rideRequestRepository, times(1)).findById(100); // 验证findById是否被调用一次
        verify(restTemplate, times(1)).postForEntity(anyString(), any(), eq(Void.class)); // 验证restTemplate是否被调用一次
    }

    @Test
    void testCompleteRide() {
        // 创建模拟的RideRequest对象
        RideRequest rideRequest = new RideRequest();
        rideRequest.setRideRequestId(100);
        rideRequest.setDriverId(1); // 模拟司机ID为1
        rideRequest.setRideStatus("IN_PROGRESS");

        // 创建模拟的Driver对象
        Driver driver = new Driver();
        driver.setId(1);
        driver.setIsAvailable(false); // 模拟司机当前不可用

        // 模拟依赖的方法
        when(rideRequestRepository.findById(100)).thenReturn(java.util.Optional.of(rideRequest));
        when(driverRepository.findById(1)).thenReturn(java.util.Optional.of(driver));

        // 模拟rideRequestRepository.save和driverRepository.save的行为
        when(rideRequestRepository.save(rideRequest)).thenReturn(rideRequest);
        when(driverRepository.save(driver)).thenReturn(driver);

        // 调用controller的completeRide方法
        ResponseEntity<String> response = driverController.completeRide(100);

        // 验证返回的结果
        Assertions.assertNotNull(response); // 确保返回结果不为null
        Assertions.assertEquals(200, response.getStatusCodeValue()); // 确保返回状态码是200
        Assertions.assertEquals("The trip has been completed.", response.getBody()); // 验证返回的消息
    }

    @Test
    void testGetRideRequests() {
        // 创建模拟的RideTypedto对象
        RideTypedto rideTypedto = new RideTypedto();
        rideTypedto.setRideType(RideType.STANDARD); // 设置RideType为REGULAR

        // 创建模拟的RideRequest列表
        RideRequest rideRequest1 = new RideRequest();
        rideRequest1.setRideRequestId(1);
        rideRequest1.setRideType(RideType.STANDARD);
        rideRequest1.setRideStatus("pending");

        RideRequest rideRequest2 = new RideRequest();
        rideRequest2.setRideRequestId(2);
        rideRequest2.setRideType(RideType.STANDARD);
        rideRequest2.setRideStatus("pending");

        List<RideRequest> mockRideRequests = Arrays.asList(rideRequest1, rideRequest2);

        // 模拟rideRequestRepository.findByRideTypeAndRideStatus的行为
        when(rideRequestRepository.findByRideTypeAndRideStatus(RideType.STANDARD, "pending"))
                .thenReturn(mockRideRequests);

        // 调用controller的getRideRequests方法
        ResponseEntity<List<RideRequest>> response = driverController.getRideRequests(rideTypedto);

        // 验证返回的结果
        Assertions.assertNotNull(response); // 确保返回结果不为null
        Assertions.assertEquals(200, response.getStatusCodeValue()); // 确保返回状态码是200
        Assertions.assertEquals(mockRideRequests, response.getBody()); // 验证返回的RideRequest列表与预期相同

        // 验证rideRequestRepository.findByRideTypeAndRideStatus方法是否被正确调用
        verify(rideRequestRepository, times(1)).findByRideTypeAndRideStatus(RideType.STANDARD, "pending");
    }

    @Test
    void testGetBillList() {
        Integer driverId = 1; // 假设我们要获取司机ID为1的账单列表

        // 创建模拟的Bill对象
        Bill bill1 = new Bill();
        bill1.setRideRequestId(1);
        bill1.setPrice(100.0);
        bill1.setDriverId(driverId);

        Bill bill2 = new Bill();
        bill2.setRideRequestId(2);
        bill2.setPrice(150.0);
        bill2.setDriverId(driverId);

        List<Bill> mockBillList = Arrays.asList(bill1, bill2);

        // 模拟restTemplate.exchange的行为
        String url = billingServiceBaseUrl+"/bill/passenger/1";
        when(restTemplate.exchange(
                eq(url), eq(HttpMethod.GET), eq(null),
                any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(mockBillList));

        // 调用controller的getBillList方法
        ResponseEntity<List<Bill>> response = driverController.getBillList(driverId);

        // 验证返回的结果
        Assertions.assertNotNull(response); // 确保返回结果不为null
        Assertions.assertEquals(200, response.getStatusCodeValue()); // 确保返回状态码是200
        Assertions.assertEquals(mockBillList, response.getBody()); // 验证返回的账单列表与预期相同

        // 验证restTemplate.exchange方法是否被正确调用
        verify(restTemplate, times(1)).exchange(
                eq(url), eq(HttpMethod.GET), eq(null),
                any(ParameterizedTypeReference.class)
        );
    }
}
