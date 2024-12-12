package com.ridesharing.passengermanagement.controller;

import com.ridesharing.billing.pojo.Bill;
import com.ridesharing.common.pojo.AcceptRequest;
import com.ridesharing.common.pojo.RideRequest;
import com.ridesharing.common.repository.RideRequestRepository;
import com.ridesharing.passengermanagement.dto.RideNotification;
import com.ridesharing.passengermanagement.service.PassengerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/passenger")
public class PassengerNotificationController {
    private static final Logger logger = LoggerFactory.getLogger(PassengerNotificationController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PassengerService passengerService;
    @Value("${billing-service.base-url}")
    private String billingServiceBaseUrl;
    @Autowired
    private RideRequestRepository rideRequestRepository;

    //Driver在接单时向此站点发送了请求，预计此站点向前端发送WebSocket，但是失败了
    @PostMapping("/notifyPassenger")
    public ResponseEntity<?> notifyPassenger(@RequestBody AcceptRequest acceptRequest) {
        logger.info("Received ride request for passengerId: {}", acceptRequest.getDriverId());//意义上是PassengerId
        RideNotification notification=new RideNotification("ACCEPTED", acceptRequest.getRideRequestId());
        // 将信号通过 WebSocket 推送到前端
        messagingTemplate.convertAndSend("/topic/rideStatus/" + acceptRequest.getDriverId(),
                notification);
        logger.info("Sending WebSocket notification: {}", notification.getStatus());

        return ResponseEntity.ok("Notification sent to WebSocket");
    }

    @GetMapping("/notifyPassenger/{requestId}")
    public ResponseEntity<RideNotification> notifyPassenger(@PathVariable Integer requestId) {
        RideNotification notification=new RideNotification("Waiting for ride...", 0);
        RideRequest rideRequest=rideRequestRepository.findById(requestId).orElseThrow();
        if(rideRequest.getDriverId()!=null){
            notification.setStatus("ACCEPTED");
        }
        return ResponseEntity.ok(notification);
    }
    //结账（获取价格）
    @GetMapping("/price/{passengerId}")
    public ResponseEntity<Bill> getPrice(@PathVariable Integer passengerId) {
        Integer rideRequestId=passengerService.findRequestId(passengerId);
        String url=billingServiceBaseUrl+"/bill/price/"+rideRequestId;

        ResponseEntity<Bill> response = restTemplate.exchange(
                url,  // URL
                HttpMethod.GET,  // 请求方法
                null,  // 请求体
                new ParameterizedTypeReference<Bill>() {}
        );
        return ResponseEntity.ok(response.getBody());
    }
}

