package com.ridesharing.billing.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.ridesharing.billing.pojo.Bill;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BillServiceTest {

    @Autowired
    private BillService billService;
    @Test
    void addBill() {
        Bill bill= billService.addBill(1,1,1,1.0);
        assertNotNull(bill);
        assertEquals(1,bill.getPassengerId());

    }


}
