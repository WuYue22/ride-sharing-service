package com.ridesharing.apigateway;

import com.ridesharing.billing.controller.BillController;
import com.ridesharing.billing.pojo.Bill;
import com.ridesharing.billing.service.BillService;
import com.ridesharing.drivermanagement.controller.DriverController;
import com.ridesharing.drivermanagement.pojo.Driver;
import com.ridesharing.drivermanagement.service.DriverService;
import com.ridesharing.passengermanagement.controller.PassengerController;
import com.ridesharing.passengermanagement.service.PassengerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import java.util.ArrayList;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 使用 SpringBootTest 进行集成测试
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DriverService driverService;

    @MockBean
    private PassengerService passengerService;

    @MockBean
    private BillService billService;

    @Test
    public void testDriverServiceRoute() throws Exception {
        mockMvc.perform(get("/api/driver/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void testPassengerServiceRoute() throws Exception {
        mockMvc.perform(get("/api/passenger/search")
                        .param("pickupLocation", "location1")
                        .param("dropoffLocation", "location2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void testCompleteRide() throws Exception {
        mockMvc.perform(post("/api/passenger/complete-ride")
                        .param("rideRequestId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("completed"));
    }

    @Test
    public void testBillServiceRoute() throws Exception {
        mockMvc.perform(get("/bill/price/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(100.0));
    }

    @Test
    public void testBillingGenerationFromDriverCompletion() throws Exception {
        mockMvc.perform(post("/api/driver/complete-ride")
                        .param("rideRequestId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(100.0));
    }
}
