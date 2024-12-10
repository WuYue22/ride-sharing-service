package com.ridesharing.passengermanagement.repository;


import com.ridesharing.passengermanagement.pojo.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Integer> {
    Optional<Passenger> findByPassengerName(String username);

    boolean existsByPassengerName(String username);
}

