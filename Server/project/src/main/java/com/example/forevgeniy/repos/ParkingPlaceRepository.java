package com.example.forevgeniy.repos;

import com.example.forevgeniy.dao.entities.ParkingPlace;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkingPlaceRepository extends JpaRepository<ParkingPlace, Integer> {

     ParkingPlace findById(int id);

     List<ParkingPlace> findAllByParkingId(int parking_id);

     List<ParkingPlace> findParkingPlaceByParking_IdAndSensorState(int parking_id, byte state);

}
