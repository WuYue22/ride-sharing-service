package com.ridesharing.drivermanagement.repository;

import com.ridesharing.drivermanagement.pojo.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Integer> {

    boolean existsByUsername(String username);
}

