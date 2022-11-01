package com.example.forevgeniy.repos;

import com.example.forevgeniy.dao.entities.Parking;

import com.example.forevgeniy.dao.entities.ParkingPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingRepository extends JpaRepository<Parking, Integer> {

    Parking findByName(String name);

    @Query(value = "select COUNT(parking_place.id) from parking join parking_place on :parking_id=parking_place.parking_id join sensor on sensor.id=parking_place.sensor_id where sensor.state=1", nativeQuery = true)
    Integer isFree(Integer parking_id);



}
