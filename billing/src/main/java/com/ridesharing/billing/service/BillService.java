package com.ridesharing.billing.service;

import com.ridesharing.billing.pojo.Bill;
import com.ridesharing.billing.repository.BillRepository;
import com.ridesharing.common.pojo.RideRequest;
import com.ridesharing.common.pojo.RideStatus;
import com.ridesharing.common.pojo.RideType;
import com.ridesharing.common.repository.RideRequestRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@ComponentScan(basePackages = "com.ridesharing")
@Service
public class BillService {

    @Autowired
    private RideRequestRepository rideRequestRepository;
    @Autowired
    private BillRepository billRepository;

    //增加
    public Bill addBill(Integer passengerId, Integer driverId, Integer rideRequestId, double price){
        Bill bill=new Bill(rideRequestId,passengerId,driverId,price);
        return billRepository.save(bill);
    }

    //计算价格
    public Bill getPrice(Integer rideRequestId){
        RideRequest rideRequest=rideRequestRepository.findById(rideRequestId).orElseThrow(()
                -> new RuntimeException("RideRequest not found"));
        double price=rideRequest.getDistance()*rideRequest.getRideType().getPricePerKm();
        if(billRepository.existsById(rideRequestId)){
            return billRepository.findById(rideRequestId).orElseThrow(()-> new RuntimeException("bill not found"));
        }
        return addBill(rideRequest.getPassengerId(),rideRequest.getDriverId(),rideRequestId,price);
    }

    //乘客获取历史账单
    public List<Bill> getBillsByPassengerId(Integer passengerId){
        return billRepository.findByPassengerId(passengerId);
    }

}


