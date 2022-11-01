package com.example.forevgeniy.service;


import com.example.forevgeniy.dao.entities.Parking;

import com.example.forevgeniy.repos.ParkingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static java.lang.Math.pow;

@Service
@AllArgsConstructor
public class ParkingService {

    private final ParkingRepository parkingRepository;

    public Parking findNearest(Parking parking){


        List<Parking> allParking= parkingRepository.findAll();



        Comparator<Parking> comparator = new Comparator<Parking>(){
            public int compare(Parking o1, Parking o2) {
                double r1=pow( o1.getX()- parking.getX(),2)+pow(o1.getY()- parking.getY(),2);

                double r2=pow( o2.getX()- parking.getX(),2)+pow(o2.getY()- parking.getY(),2);

                return (r1 < r2) ? -1 : ((r1 == r2) ? 0 : 1);
            }
        };

        allParking.sort(comparator);

        for (Parking nearestParking:allParking) {

           if(parkingRepository.isFree(nearestParking.getId())>0){
                return nearestParking;
            }

        }


        return null;
    }

    public Parking findByName(String name){
        Parking parking=parkingRepository.findByName(name);

        return parking;
    }

    public List<Parking> findAll(){
        List<Parking> allParking = parkingRepository.findAll();
        return allParking;
    }

    public Parking signup(Parking parking){
        Parking newParking = parkingRepository.save(parking);
        return newParking;
    }



}
