package com.ridesharing.passengermanagement.controller;

import com.ridesharing.passengermanagement.dto.LoginResponse;
import com.ridesharing.passengermanagement.dto.RegisterRequest;
import com.ridesharing.passengermanagement.service.PassengerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;

class LoginControllerTest {
    @Mock
    PassengerService passengerService;
    @InjectMocks
    LoginController loginController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin() {
        // Arrange: 设置模拟服务的行为
        LoginResponse mockLoginResponse = new LoginResponse();
        mockLoginResponse.setUserId(1); // 假设登录成功返回的用户ID为1
        mockLoginResponse.setMessage("Login successful");

        when(passengerService.login(any(RegisterRequest.class))).thenReturn(mockLoginResponse);

        // Act: 调用 controller 的 login 方法
        RegisterRequest loginRequest = new RegisterRequest("username", "password");
        ResponseEntity<LoginResponse> result = loginController.login(loginRequest);

        // Assert: 验证返回的 ResponseEntity
        Assertions.assertNotNull(result);
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode()); // HTTP 200
        Assertions.assertEquals(mockLoginResponse, result.getBody()); // 验证返回的 LoginResponse
        verify(passengerService, times(1)).login(loginRequest); // 确保 login 方法被调用了一次
    }
}

