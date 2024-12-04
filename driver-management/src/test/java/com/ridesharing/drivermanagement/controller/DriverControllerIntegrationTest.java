package com.ridesharing.drivermanagement.controller;

import com.ridesharing.billing.pojo.Bill;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DriverControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCompleteRide() {
        Integer rideRequestId = 1; // 有效 rideRequestId

        String url = "/api/driver/complete-ride?rideRequestId=" + rideRequestId;

        // 执行 POST 请求到 /complete-ride
        ResponseEntity<Bill> response = restTemplate.postForEntity(url, null, Bill.class);

        // 验证返回的状态和数据是否符合预期
        assertEquals(200, response.getStatusCodeValue()); // 检查 HTTP 状态码
        assertNotNull(response.getBody()); // 确保返回的账单对象不为空

        // 进一步验证账单数据（根据实际情况自定义断言）
        Bill bill = response.getBody();
        assertEquals(rideRequestId, bill.getRideRequestId()); // 例如，验证账单与 rideRequestId 是否匹配
        // 如果账单中有其他属性，也可以根据实际数据进行验证
    }


}
