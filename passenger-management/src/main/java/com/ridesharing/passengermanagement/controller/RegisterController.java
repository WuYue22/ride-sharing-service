package com.ridesharing.passengermanagement.controller;


import com.ridesharing.passengermanagement.dto.RegisterRequest;
import com.ridesharing.passengermanagement.service.PassengerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/passenger")
public class RegisterController {

    private final PassengerService passengerService;

    public RegisterController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        try {
            String message = passengerService.register(request);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
