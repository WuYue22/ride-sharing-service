package com.ridesharing.drivermanagement.service;

import com.ridesharing.billing.pojo.Bill;
import com.ridesharing.billing.repository.BillRepository;
import com.ridesharing.common.pojo.Driver;
import com.ridesharing.common.pojo.RideRequest;
import com.ridesharing.common.pojo.RideStatus;
import com.ridesharing.common.pojo.RideType;
import com.ridesharing.common.repository.RideRequestRepository;
import com.ridesharing.drivermanagement.dto.LoginResponse;
import com.ridesharing.drivermanagement.pojo.DriverLocation;
import com.ridesharing.drivermanagement.repository.DriverRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DriverServiceTest {
    @Mock
    DriverRepository driverRepository;
    @Mock
    RideRequestRepository rideRequestRepository;
    @InjectMocks
    DriverService driverService;
    private Driver mockDriver;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockDriver = new Driver("testDriver", "password", "STANDARD");
        mockDriver.setId(1);
        mockDriver.setLatitude(10.0);
        mockDriver.setLongitude(20.0);
    }

    @Test
    public void testGetDriverById_WhenDriverExists() {
        // 模拟 driverRepository.findById 返回一个包含驱动的Optional
        when(driverRepository.findById(1)).thenReturn(Optional.of(mockDriver));
        // 调用方法并验证结果
        Driver result = driverService.getDriverById(1);
        assertNotNull(result);
        Assertions.assertEquals(mockDriver.getId(), result.getId());
        Assertions.assertEquals(mockDriver.getUsername(), result.getUsername());
    }

    @Test
    public void testGetDriverById_WhenDriverNotFound() {
        // 模拟 driverRepository.findById 返回一个空的Optional
        when(driverRepository.findById(2)).thenReturn(Optional.empty());
        // 调用方法并验证抛出异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            driverService.getDriverById(2);
        });
        Assertions.assertEquals("Driver not found", exception.getMessage());
    }

    @Test
    void testUpdateDriverLocation() {
        DriverLocation driverLocation= new DriverLocation(30.0, 40.0);
        // 模拟 driverRepository.findById 返回一个 Driver
        when(driverRepository.findById(1)).thenReturn(Optional.of(mockDriver));
        // 模拟 driverRepository.save 返回更新后的 Driver
        when(driverRepository.save(any(Driver.class))).thenReturn(mockDriver);

        // 调用 updateDriverLocation 方法
        Driver updatedDriver = driverService.updateDriverLocation(1, driverLocation);

        // 验证结果
        assertNotNull(updatedDriver);
        Assertions.assertEquals(30.0, updatedDriver.getLatitude(), 0.001);
        Assertions.assertEquals(40.0, updatedDriver.getLongitude(), 0.001);

        // 验证 driverRepository 的交互
        verify(driverRepository, times(1)).findById(1);
        verify(driverRepository, times(1)).save(any(Driver.class));
    }

    @Test
    void testAcceptRide_Success() {
        RideRequest rideRequest=new RideRequest();
        rideRequest.setRideRequestId(1);
        rideRequest.setPassengerId(1);
        rideRequest.setRideStatus(RideStatus.PENDING.name()); // 初始状态为 PENDING
        // 模拟 driverRepository.findById 返回一个 Driver
        when(driverRepository.findById(1)).thenReturn(Optional.of(mockDriver));

        // 模拟 rideRequestRepository.findById 返回一个 RideRequest
        when(rideRequestRepository.findById(1)).thenReturn(Optional.of(rideRequest));

        // 模拟 driverRepository.save 返回更新后的 Driver
        when(driverRepository.save(any(Driver.class))).thenReturn(mockDriver);

        // 模拟 rideRequestRepository.save 返回更新后的 RideRequest
        when(rideRequestRepository.save(any(RideRequest.class))).thenReturn(rideRequest);

        // 调用 acceptRide 方法
        RideRequest updatedRideRequest = driverService.acceptRide(1, 1);

        // 验证 RideRequest 的状态是否更新为 IN_PROGRESS
        Assertions.assertEquals(RideStatus.IN_PROGRESS.name(), updatedRideRequest.getRideStatus());
        Assertions.assertEquals(1, updatedRideRequest.getDriverId());
        Assertions.assertEquals(10.0, updatedRideRequest.getDriverLatitude());
        Assertions.assertEquals(20.0, updatedRideRequest.getDriverLongitude());

        // 验证 Driver 的可用状态是否更新为 false
        Assertions.assertFalse(mockDriver.getIsAvailable());

        // 验证 driverRepository 的交互
        verify(driverRepository, times(1)).findById(1);
        verify(rideRequestRepository, times(1)).findById(1);
        verify(driverRepository, times(1)).save(mockDriver);
        verify(rideRequestRepository, times(1)).save(rideRequest);
    }

    @Test
    void testAcceptRide_DriverNotAvailable() {
        // 修改司机状态为不可用
        mockDriver.setIsAvailable(false);
        RideRequest rideRequest=new RideRequest();
        rideRequest.setRideRequestId(1);
        rideRequest.setPassengerId(1);
        rideRequest.setRideStatus(RideStatus.PENDING.name()); // 初始状态为 PENDING

        // 模拟 driverRepository.findById 返回一个 Driver
        when(driverRepository.findById(1)).thenReturn(Optional.of(mockDriver));

        // 模拟 rideRequestRepository.findById 返回一个 RideRequest
        when(rideRequestRepository.findById(1)).thenReturn(Optional.of(rideRequest));

        // 调用 acceptRide 方法，应该抛出异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            driverService.acceptRide(1, 1);
        });

        Assertions.assertEquals("Driver is not available", exception.getMessage());
    }

    @Test
    void testCompleteRide() {
        RideRequest rideRequest=new RideRequest();
        rideRequest = new RideRequest();
        rideRequest.setRideRequestId(1);
        rideRequest.setPassengerId(1);
        rideRequest.setDriverId(1);
        rideRequest.setRideStatus(RideStatus.IN_PROGRESS.name());
        when(rideRequestRepository.findById(1)).thenReturn(Optional.of(rideRequest));
        when(driverRepository.findById(1)).thenReturn(Optional.of(mockDriver));
        when(driverRepository.save(any(Driver.class))).thenReturn(mockDriver);
        when(rideRequestRepository.save(any(RideRequest.class))).thenReturn(rideRequest);

        RideRequest completedRideRequest = driverService.completeRide(1);

        // 验证 RideRequest 的状态是否更新为 COMPLETED
        Assertions.assertEquals(RideStatus.COMPLETED.name(), completedRideRequest.getRideStatus());

        // 验证 Driver 的可用状态是否更新为 true
        Assertions.assertTrue(mockDriver.getIsAvailable());

        // 验证 driverRepository 和 rideRequestRepository 的交互
        verify(rideRequestRepository, times(1)).findById(1);
        verify(driverRepository, times(1)).findById(1);
        verify(driverRepository, times(1)).save(mockDriver);
        verify(rideRequestRepository, times(1)).save(rideRequest);
    }

    @Test
    void testRegister_Success() {
        when(driverRepository.existsByUsername(mockDriver.getUsername())).thenReturn(false);
        when(driverRepository.save(any(Driver.class))).thenReturn(mockDriver);
        String result = driverService.register(mockDriver);

        Assertions.assertEquals("User registered successfully", result);

        verify(driverRepository, times(1)).existsByUsername(mockDriver.getUsername());
        verify(driverRepository, times(1)).save(any(Driver.class));
    }

    @Test
    public void testRegister_UsernameAlreadyExists() {
        // 模拟 driverRepository.existsByUsername 返回 true，表示用户名已存在
        when(driverRepository.existsByUsername(mockDriver.getUsername())).thenReturn(true);

        // 调用 register 方法，应该抛出 IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            driverService.register(mockDriver);
        });

        // 验证异常信息
        Assertions.assertEquals("Username already exists", exception.getMessage());

        // 验证 driverRepository 的交互
        verify(driverRepository, times(1)).existsByUsername(mockDriver.getUsername());
        verify(driverRepository, never()).save(any(Driver.class));  // 不会调用 save
    }
    @Test
    void testLogin_Success() {
        when(driverRepository.findByUsername(mockDriver.getUsername())).thenReturn(Optional.of(mockDriver));
        LoginResponse response = driverService.login(mockDriver);
        Assertions.assertEquals(1, response.getUserId());
        Assertions.assertEquals(RideType.STANDARD, response.getRideType());
        Assertions.assertEquals("Login successful", response.getMessage());

        verify(driverRepository, times(1)).findByUsername(mockDriver.getUsername());
    }
    @Test
    public void testLogin_PasswordIncorrect() {
        Driver loginRequest =new Driver();//不能直接=mockDriver，否则会一起改掉密码
        loginRequest.setId(1);
        loginRequest.setUsername("testDriver");
        // 修改 loginRequest 的密码，使其与原密码不匹配
        loginRequest.setPassword("wrongPassword");
        when(driverRepository.findByUsername(mockDriver.getUsername())).thenReturn(Optional.of(mockDriver));

        // 调用 login 方法，应该抛出异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            driverService.login(loginRequest);
        });

        // 验证异常信息
        Assertions.assertEquals("Password is incorrect", exception.getMessage());

        // 验证 driverRepository 的交互
        verify(driverRepository, times(1)).findByUsername(mockDriver.getUsername());
    }
}
