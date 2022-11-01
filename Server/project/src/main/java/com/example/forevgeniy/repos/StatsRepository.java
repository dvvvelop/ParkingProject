package com.example.forevgeniy.repos;

import com.example.forevgeniy.dao.entities.ParkingPlace;
import com.example.forevgeniy.dao.entities.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatsRepository extends CrudRepository<Record, Integer> {

//    @Query("select state from Record where Date(Record.timestamp) = :date and park_place_id = :parking_place_id and hour(timestamp) = hour")
//    List<Byte> findByDateAndParkingPlaceId(int parking_place_id, Time);


    @Query(value = "select * from record where parking_place_id = :parking_place_id && record.state != 2 order by timestamp", nativeQuery = true)
    List<Record> findAllRecordsByParkingPlaceId(int parking_place_id);


}
