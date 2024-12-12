package com.ridesharing.drivermanagement.controller;

import com.ridesharing.billing.pojo.Bill;
import com.ridesharing.common.pojo.RideRequest;
import com.ridesharing.common.pojo.RideType;
import com.ridesharing.common.repository.RideRequestRepository;
import com.ridesharing.common.pojo.AcceptRequest;
import com.ridesharing.drivermanagement.dto.LoginResponse;
import com.ridesharing.drivermanagement.dto.RideTypedto;
import com.ridesharing.common.pojo.Driver;
import com.ridesharing.drivermanagement.pojo.DriverLocation;
import com.ridesharing.drivermanagement.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/driver")
public class DriverController {

    @Autowired
    private DriverService driverService;
    @Value("${billing-service.base-url}")
    private String billingServiceBaseUrl;
    @Value("${passenger-service.base-url}")
    private String passengerServiceBaseUrl;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    private RideRequestRepository rideRequestRepository;

    @GetMapping("/{driverId}")
    public ResponseEntity<Driver> getDriverById(@PathVariable Integer driverId) {
        //System.out.println("Received request for driverId: " + driverId); // 打印日志，确认请求进入
        return ResponseEntity.ok(driverService.getDriverById(driverId));
    }

    // 1) 注册司机（用户名 密码 rideType)
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Driver driver) {
        try {
            String message = driverService.register(driver);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //登录
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody Driver loginRequest) {
        try {
            LoginResponse response = driverService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body((LoginResponse) Map.of("error", e.getMessage()));
        }
    }

    // 2) 更新司机位置
    @PostMapping("/update-location")
    public ResponseEntity<Driver> updateDriverLocation(@RequestBody DriverLocation driverLocation) {
        Driver updatedDriver = driverService.updateDriverLocation(driverLocation.getDriverId(), driverLocation);
        return ResponseEntity.ok(updatedDriver);
    }

    // 3) 接单
    @PostMapping("/accept-ride")
    public ResponseEntity<RideRequest> acceptRide(@RequestBody AcceptRequest acceptRequest){
        Integer driverId=acceptRequest.getDriverId();
        Integer rideRequestId=acceptRequest.getRideRequestId();
        RideRequest updatedRideRequest = driverService.acceptRide(driverId, rideRequestId);
        AcceptRequest acceptRequestToPassenger=new AcceptRequest();
        acceptRequestToPassenger.setDriverId(rideRequestRepository.findById(rideRequestId).get().getPassengerId());
        acceptRequestToPassenger.setRideRequestId(rideRequestId);
        // 发送信号到 Passenger 微服务告知已接单
        String url = passengerServiceBaseUrl+"/api/passenger/notifyPassenger";
        restTemplate.postForEntity(url, acceptRequestToPassenger, Void.class);
        return ResponseEntity.ok(updatedRideRequest);
    }

    // 4) 完成行程，不生成bill，在乘客结账时生成账单（也可以这里生成账单，加一个是否已支付的状态）
    @PostMapping("/complete-ride/{rideRequestId}")
    public ResponseEntity<String> completeRide(@PathVariable Integer rideRequestId) {
        RideRequest completedRideRequest = driverService.completeRide(rideRequestId);
        // 调用 Billing 模块生成账单
        //String billingUrl = billingServiceBaseUrl + "/bill/add/" + rideRequestId;
        // 发送请求到 Billing 服务
        //ResponseEntity<Bill> billResponse = restTemplate.getForEntity(billingUrl, Bill.class);
        return ResponseEntity.ok("The trip has been completed.");
    }
    //获取请求列表
    @PostMapping("/get-ride-requests")
    public ResponseEntity<List<RideRequest>> getRideRequests(@RequestBody RideTypedto request) {
        RideType rideType=request.getRideType();
        List<RideRequest> response= rideRequestRepository.findByRideTypeAndRideStatus(rideType, "pending");
        return ResponseEntity.ok(response);
    }

    //获取历史账单
    @GetMapping("/bill/{driverId}")
    public ResponseEntity<List<Bill>> getBillList(@PathVariable Integer driverId) {
        String url=billingServiceBaseUrl+"/bill/passenger/"+driverId;
        //List<Bill> bills = restTemplate.getForObject(url, List.class);
        ResponseEntity<List<Bill>> response = restTemplate.exchange(
                url,  // URL
                HttpMethod.GET,  // 请求方法
                null,  // 请求体
                new ParameterizedTypeReference<List<Bill>>() {}
        );
        return ResponseEntity.ok(response.getBody());
    }


}

