package com.ridesharing.passengermanagement.controller;

import com.ridesharing.passengermanagement.dto.RegisterRequest;
import com.ridesharing.passengermanagement.service.PassengerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;

class RegisterControllerTest {
    @Mock
    PassengerService passengerService;
    @InjectMocks
    RegisterController registerController;
    private RegisterRequest validRegisterRequest;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 初始化测试数据
        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setUsername("validUsername");
        validRegisterRequest.setPassword("validPassword");
    }
    @Test
    void testRegisterSuccess() {
        // Arrange
        when(passengerService.register(validRegisterRequest)).thenReturn("User registered successfully");

        // Act
        ResponseEntity<String> response = registerController.register(validRegisterRequest);

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue()); // HTTP 200
        Assertions.assertEquals("User registered successfully", response.getBody());
        verify(passengerService, times(1)).register(validRegisterRequest);
    }

    @Test
    void testRegisterUserAlreadyExists() {
        // Arrange
        when(passengerService.register(validRegisterRequest)).thenThrow(new IllegalArgumentException("Username already exists"));

        // Act
        ResponseEntity<String> response = registerController.register(validRegisterRequest);

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.getStatusCodeValue()); // HTTP 400
        Assertions.assertEquals("Username already exists", response.getBody());
        verify(passengerService, times(1)).register(validRegisterRequest);
    }
}
