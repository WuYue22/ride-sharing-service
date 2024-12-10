package com.ridesharing.common.repository;

import com.ridesharing.common.pojo.RideRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRequestRepository extends JpaRepository<RideRequest, Integer> {
    List<RideRequest> findByPickupLocationAndDropoffLocation(String pickupLocation, String dropoffLocation);

    List<RideRequest> findByPassengerId(Integer passengerId);
}
