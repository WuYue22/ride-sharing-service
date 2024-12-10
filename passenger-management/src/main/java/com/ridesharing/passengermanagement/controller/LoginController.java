package com.ridesharing.passengermanagement.controller;


import com.ridesharing.passengermanagement.dto.RegisterRequest;
import com.ridesharing.passengermanagement.pojo.LoginResponse;
import com.ridesharing.passengermanagement.service.PassengerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/passenger")
public class LoginController {
    private final PassengerService passengerService;

    public LoginController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody RegisterRequest loginRequest) {
        try {
            LoginResponse response = passengerService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body((LoginResponse) Map.of("error", e.getMessage()));
        }
    }
}
