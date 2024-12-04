package com.ridesharing.billing.controller;

import com.ridesharing.billing.pojo.Bill;
import com.ridesharing.billing.service.BillService;
import com.ridesharing.common.pojo.RideRequest;
import com.ridesharing.common.repository.RideRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bill")
public class BillController {

    @Autowired
    private BillService billService;
    @Autowired
    private RideRequestRepository rideRequestRepository;

    //获取乘客账单
    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<Bill>> getBillList(@PathVariable Integer passengerId) {
        return ResponseEntity.ok(billService.getBillsByPassengerId(passengerId));
    }

    //告知乘客价格
    @GetMapping("/price/{rideRequestId}")
    public ResponseEntity<Bill> getPrice(@PathVariable Integer rideRequestId) {
        return ResponseEntity.ok(billService.getPrice(rideRequestId));
    }

    //新增账单
    @GetMapping("/add/{rideRequestId}")
    public ResponseEntity<Bill> addBill(@PathVariable Integer rideRequestId) {
        RideRequest rideRequest= rideRequestRepository.findById(rideRequestId).orElseThrow(()
                -> new RuntimeException("Ride request not found"));
        return ResponseEntity.ok(billService.addBill( rideRequest.getPassengerId(),rideRequest.getDriverId(),
                rideRequestId, billService.getPrice(rideRequestId).getPrice()));
    }
}
