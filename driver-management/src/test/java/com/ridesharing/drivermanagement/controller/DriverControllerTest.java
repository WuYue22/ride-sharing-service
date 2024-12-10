package com.ridesharing.drivermanagement.controller;


import com.ridesharing.common.repository.RideRequestRepository;
import com.ridesharing.drivermanagement.pojo.Driver;
import com.ridesharing.drivermanagement.repository.DriverRepository;
import com.ridesharing.drivermanagement.service.DriverService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest
public class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DriverController driverController;
    @Autowired
    private DriverService driverService;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Test
    public void testRegister() throws Exception {
        String driverJson = "{\"username\":\"driver1\", \"password\":\"1234567890\", \"rideType\":\"STANDARD\"}";

        mockMvc.perform(post("/api/driver/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("driver1"))
                .andExpect(jsonPath("$.isAvailable").value(true));
    }

    @Test
    public void testUpdateDriverLocation() throws Exception {
        String locationJson = "{\"driverId\":1, \"latitude\":37.7749, \"longitude\":-122.4194}";

        mockMvc.perform(post("/api/driver/update-location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(locationJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latitude").value(37.7749))
                .andExpect(jsonPath("$.longitude").value(-122.4194));
    }

    @Test
    public void testAcceptRide() throws Exception {
        Driver driver = new Driver();
        driver.setId(1);
        driver.setIsAvailable(true);
        driverRepository.save(driver);
        /*RideRequest rideRequest=rideRequestRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Not found"));
        rideRequest.setRideStatus("PENDING");*/
        mockMvc.perform(post("/api/driver/accept-ride")
                        .param("driverId", "1")
                        .param("rideRequestId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rideStatus").value("IN_PROGRESS"));
    }
}

