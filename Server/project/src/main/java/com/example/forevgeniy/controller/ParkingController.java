package com.example.forevgeniy.controller;


import com.example.forevgeniy.dao.entities.Parking;
import com.example.forevgeniy.dao.entities.Sensor;
import com.example.forevgeniy.service.ParkingService;
import com.example.forevgeniy.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parkings")
public class ParkingController {

    private final ParkingService parkingService;

    @Autowired
    public ParkingController(ParkingService parkingService){
        this.parkingService=parkingService;
    }

    @GetMapping("/nearest")
    @ResponseStatus(HttpStatus.OK)
    public Parking findNearest(@RequestBody Parking parking){
        Parking nearestParking=parkingService.findNearest(parking);
        return nearestParking;
    }

    @GetMapping("/{name}")
    @ResponseStatus(HttpStatus.OK)
    public Parking findByName(@PathVariable(value = "name") String name){
        Parking parking=parkingService.findByName(name);
        return parking;
    }


    @GetMapping("/getAll")
    @ResponseStatus(HttpStatus.OK)
    public List<Parking> findAll(){
        List<Parking> allParking = parkingService.findAll();
        return allParking;
    }

    @GetMapping("/singup")
    @ResponseStatus(HttpStatus.OK)
    public Parking singup(@RequestBody Parking parking){
        Parking newParking = parkingService.signup(parking);
        return newParking;
    }



}
