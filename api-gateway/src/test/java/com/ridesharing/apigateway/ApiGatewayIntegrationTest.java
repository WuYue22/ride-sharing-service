package com.ridesharing.apigateway;
import com.ridesharing.billing.pojo.Bill;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiGatewayIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testDriverServiceRoute() throws Exception {

        ResponseEntity<Bill> response = restTemplate.getForEntity(
                "/api/driver/1",  // URL
                null,  // 请求体
                Bill.class
        );
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }
    @Test
    void testCompleteRide() {
        Integer rideRequestId = 1;

        // 访问网关，模拟请求
        String url = "/api/driver/complete-ride?rideRequestId=" + rideRequestId;

        ResponseEntity<Bill> response = restTemplate.postForEntity(
                url,  // URL  // 请求方法
                null,  // 请求体
                Bill.class
        );
        // 验证返回的状态码和数据
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }
    @Test
    void testGetBillList() {

        Integer passengerId = 1;

        // 执行 GET 请求到 /bill/passenger/{passengerId}
        ResponseEntity<List<Bill>> response = restTemplate.getForEntity(
                "/api/passenger/bill/" + passengerId,
                null, new ParameterizedTypeReference<List<Bill>>() {});

        // 验证返回的状态和数据是否符合预期
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}

