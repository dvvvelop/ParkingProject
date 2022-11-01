package com.example.forevgeniy.repos;

import com.example.forevgeniy.dao.entities.Sensor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SensorRepository extends JpaRepository<Sensor, Integer> {

    Sensor findById(int id);

    List<Sensor> findAll();

}
