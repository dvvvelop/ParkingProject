package com.example.forevgeniy.controller;


import com.example.forevgeniy.dao.entities.Parking;
import com.example.forevgeniy.dao.entities.ParkingPlace;
import com.example.forevgeniy.service.ParkingPlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

import static java.lang.Math.pow;

@RestController
@RequestMapping("/parkingPlaces")
@EnableScheduling
public class ParkingPlaceController {

    private final ParkingPlaceService parkingPlaceService;

    @Autowired
    private List<ParkingPlace> allParkingPlaces;

    @Autowired
    public ParkingPlaceController(ParkingPlaceService parkingPlaceService){
        this.parkingPlaceService = parkingPlaceService;
    }

    @GetMapping("/findAll")
    @ResponseStatus(HttpStatus.OK)
    public List<ParkingPlace> findAll(){
        List<ParkingPlace> parkingPlaces=parkingPlaceService.findAll();

        return parkingPlaces;
    }

    @GetMapping("/findByParking/{parking_id}")
    @ResponseStatus(HttpStatus.OK)
    public List<ParkingPlace> findAllByParkingId(@PathVariable(name="parking_id") int parking_id){
        return parkingPlaceService.findAllPlacesForParking(parking_id);
    }

    @GetMapping("/nearest2")
    public ParkingPlace findNearest2(@RequestParam double x, @RequestParam double y){

        Comparator<ParkingPlace> comparator = new Comparator<ParkingPlace>(){
            public int compare(ParkingPlace o1, ParkingPlace o2) {
                double r1=pow( o1.getX()- x,2)+pow(o1.getY()- y,2);

                double r2=pow( o2.getX()- x,2)+pow(o2.getY()- y,2);

                return (r1 < r2) ? -1 : ((r1 == r2) ? 0 : 1);
            }
        };

        allParkingPlaces.sort(comparator);

        for (ParkingPlace nearestParkingPlace:allParkingPlaces) {

            if(nearestParkingPlace.getSensor().getState()==1){
//                List<ParkingPlace> nearestParkingPlace=parkingRepository.findFreeParkingPlace(nearestParking.getId());
//                return nearestParkingPlace.get(0);
                return nearestParkingPlace;
            }

        }

        return null;
    }

    @Scheduled(fixedDelay = 5000)
    public void scheduleFixedDelayTask() {
        allParkingPlaces = parkingPlaceService.findAll();
    }

    @GetMapping("/get")
    @ResponseStatus(HttpStatus.OK)
    public List<ParkingPlace> addNewTest() {
        return allParkingPlaces;
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ParkingPlace findById(@PathVariable(value = "id") int id){
        ParkingPlace parkingPlace=parkingPlaceService.findById(id);

        return  parkingPlace;
    }

    @GetMapping("/signup")
    @ResponseStatus(HttpStatus.OK)
    public ParkingPlace signup(@RequestBody ParkingPlace parkingPlace){
        ParkingPlace newParkingPlace = parkingPlaceService.signup(parkingPlace);
        return newParkingPlace;
    }

    @GetMapping("/nearest")
    public ParkingPlace findNearest(@RequestParam double x, @RequestParam double y){
        ParkingPlace nearestParking=parkingPlaceService.findNearestParkingPlaceByCoordinates(x,y);
        return nearestParking;
    }
}
