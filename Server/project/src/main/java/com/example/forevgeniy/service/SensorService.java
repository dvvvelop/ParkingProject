package com.example.forevgeniy.service;


import com.example.forevgeniy.dao.entities.Parking;
import com.example.forevgeniy.dao.entities.ParkingPlace;
import com.example.forevgeniy.dao.entities.Sensor;
import com.example.forevgeniy.repos.ParkingRepository;
import com.example.forevgeniy.repos.SensorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SensorService {

    private final SensorRepository sensorRepository;


    public Sensor findById(int id){
       Sensor sensor = sensorRepository.findById(id);
       return sensor;
    }

    public void updateById(int id, byte state) {
        sensorRepository.save(new Sensor(id,state));
    }

    public List<Sensor> findAll(){
        List<Sensor> sensor=sensorRepository.findAll();
        return sensor;
    }



    public void signup(Sensor sensor) {
        sensorRepository.save(sensor);
    }


}
