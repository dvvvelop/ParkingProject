package com.example.forevgeniy.controller;

import com.example.forevgeniy.dao.entities.Parking;
import com.example.forevgeniy.dao.entities.ParkingPlace;
import com.example.forevgeniy.dao.entities.Sensor;
import com.example.forevgeniy.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sensors")
public class SensorController {

    private final SensorService sensorService;

    @Autowired
    public SensorController(SensorService sensorService){
        this.sensorService=sensorService;
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Sensor findById(@PathVariable(value = "id") int id){
        Sensor sensor=sensorService.findById(id);
        return sensor;
    }

    @GetMapping("/{id}/{state}")
    @ResponseStatus(HttpStatus.OK)
    public void updateById(@PathVariable(value ="id") int id,@PathVariable(value = "state") byte state){
        sensorService.updateById(id,state);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Sensor> findAll(){
        List<Sensor> sensors = sensorService.findAll();
        return sensors;
    }

    @GetMapping("/singup")
    @ResponseStatus(HttpStatus.OK)
    public void singup(@RequestBody Sensor sensor){
        sensorService.signup(sensor);
    }

}
