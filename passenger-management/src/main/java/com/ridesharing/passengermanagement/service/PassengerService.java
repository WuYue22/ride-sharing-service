package com.ridesharing.passengermanagement.service;


import com.ridesharing.common.pojo.*;
import com.ridesharing.common.pojo.RideType;
import com.ridesharing.common.pojo.Driver;
import com.ridesharing.passengermanagement.dto.LoginResponse;
import com.ridesharing.passengermanagement.dto.PassengerDto;
import com.ridesharing.passengermanagement.dto.RegisterRequest;
import com.ridesharing.passengermanagement.pojo.*;
import com.ridesharing.passengermanagement.repository.PassengerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import com.ridesharing.common.repository.RideRequestRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
@ComponentScan(basePackages = "com.ridesharing")
@Service
public class PassengerService implements IPassengerService {
    @Autowired
    PassengerRepository passengerRepository;
    @Autowired
    RideRequestRepository rideRequestRepository;
    @Autowired
    RestTemplate restTemplate;
    @Value("${driver-service.base-url}")
    private String driverServiceBaseUrl;
    @Override
    public Passenger add(PassengerDto passenger) {
        Passenger passengerPojo = new Passenger();
        BeanUtils.copyProperties(passenger, passengerPojo);
        return passengerRepository.save(passengerPojo);
    }

    @Override
    public Passenger add(String passengerName, String passengerPassword) {
        Passenger passenger = new Passenger();
        passenger.setPassengerName(passengerName);
        passenger.setPassengerPassword(passengerPassword);
        return passengerRepository.save(passenger);
    }
    @Override
    public Passenger get(Integer passengerId) {
        return passengerRepository.findById(passengerId).orElseThrow(() -> {
            throw new IllegalArgumentException("Passenger Not Found");
        });
    }
    @Override
    public Passenger edit(PassengerDto passenger) {
        Passenger passengerPojo = new Passenger();
        BeanUtils.copyProperties(passenger, passengerPojo);
        return passengerRepository.save(passengerPojo);
    }

    @Override
    public void delete(Integer passengerId) {
        passengerRepository.deleteById(passengerId);
    }

    public String register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (passengerRepository.existsByPassengerName(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // 创建 Passenger 实体并保存到数据库
        Passenger passenger = new Passenger();
        passenger.setPassengerName(request.getUsername());
        passenger.setPassengerPassword(request.getPassword());

        passengerRepository.save(passenger);

        return "User registered successfully";
    }

    // 搜索乘车
    public List<RideRequest> searchRide(String pickupLocation, String dropoffLocation) {
        // 查找匹配的乘车请求
        return rideRequestRepository.findByPickupLocationAndDropoffLocation(pickupLocation, dropoffLocation);
    }

    // 1) 提交乘车请求
    public RideRequest submitRequest(Integer passengerId, RideType rideType, String pickupLocation, String dropoffLocation,Double distance) {
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException("Passenger not found"));

        RideRequest rideRequest = new RideRequest();
        rideRequest.setPassengerId(passengerId);
        rideRequest.setRideType(rideType);
        rideRequest.setPickupLocation(pickupLocation);
        rideRequest.setDropoffLocation(dropoffLocation);
        rideRequest.setDistance(distance);
        rideRequest.setRideStatus(RideStatus.PENDING.name());
        return rideRequestRepository.save(rideRequest);
    }

    // 跟踪乘车位置
    @Transactional
    public RideRequest trackRide(Integer rideRequestId) {
        // 获取乘车请求
        RideRequest rideRequest = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new RuntimeException("RideRequest not found"));

        // 获取司机信息
        Integer driverId = rideRequest.getDriverId();
        Driver driver = null;
        try {
            driver = restTemplate.getForObject(
                    driverServiceBaseUrl + "/api/driver/" + driverId,
                    Driver.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to get driver", e);
        }
        try {
            // 更新乘车请求中的司机位置
            rideRequest.setDriverLatitude(driver.getLatitude());
            rideRequest.setDriverLongitude(driver.getLongitude());
        }catch(Exception e)
        {throw new RuntimeException("Failed to get driver location", e);}
        // 保存更新后的乘车请求
        return rideRequestRepository.save(rideRequest);
    }
    // 4) 确认乘车
    public RideRequest confirmRide(Integer rideRequestId) {
        RideRequest rideRequest = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new RuntimeException("RideRequest not found"));

        rideRequest.setRideStatus(RideStatus.IN_PROGRESS.name());
        return rideRequestRepository.save(rideRequest);
    }

    // 5) 完成乘车
    public RideRequest completeRide(Integer rideRequestId) {
        RideRequest rideRequest = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new RuntimeException("RideRequest not found"));

        rideRequest.setRideStatus(RideStatus.COMPLETED.name());
        return rideRequestRepository.save(rideRequest);
    }

    public LoginResponse login(RegisterRequest loginRequest) {
        String username = loginRequest.getUsername();

        Passenger passenger = passengerRepository.findByPassengerName(username).orElseThrow(() ->
                new IllegalArgumentException("Passenger not found"));
        if(!passenger.getPassengerPassword().equals(loginRequest.getPassword())){
            throw new IllegalArgumentException("Password is incorrect");
        }
        //Passenger currentUser = new Passenger(passenger.getPassengerId(), username,passenger.getPassengerPassword());
        //GlobalUser.getInstance().setUser(currentUser);
        LoginResponse response=new LoginResponse();
        response.setUserId(passenger.getPassengerId());
        response.setMessage("Login successful");
        return response;
    }

    //根据passengerId找最新的rideRequest的Id
    public Integer findRequestId(Integer passengerId) {
        List<RideRequest> rideRequests= rideRequestRepository.findByPassengerId(passengerId);
        RideRequest rideRequest= rideRequests.get(rideRequests.size()-1);
        return rideRequest.getRideRequestId();
    }
}
