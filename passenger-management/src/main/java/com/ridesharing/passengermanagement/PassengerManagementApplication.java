package com.ridesharing.passengermanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
public class PassengerManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(PassengerManagementApplication.class, args);
    }

}
