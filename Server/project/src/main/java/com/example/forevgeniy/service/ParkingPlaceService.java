package com.example.forevgeniy.service;


import com.example.forevgeniy.dao.entities.Parking;
import com.example.forevgeniy.dao.entities.ParkingPlace;
import com.example.forevgeniy.repos.ParkingPlaceRepository;
import com.example.forevgeniy.repos.ParkingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static java.lang.Math.pow;

@Service
@AllArgsConstructor
public class ParkingPlaceService {

    private final ParkingPlaceRepository parkingPlaceRepository;
    private final ParkingRepository parkingRepository;

   public ParkingPlace findById(int id){
        ParkingPlace parkingPlace=parkingPlaceRepository.findById(id);

        return parkingPlace;
    }

    public List<ParkingPlace> findAll(){
       List<ParkingPlace> parkingPlaces=parkingPlaceRepository.findAll();
       return parkingPlaces;
    }

    public List<ParkingPlace> findAllPlacesForParking(int parking_id){
        return parkingPlaceRepository.findAllByParkingId(parking_id);
    }

    public ParkingPlace signup(ParkingPlace parkingPlace){
       ParkingPlace newParkingPlace=parkingPlaceRepository.save(parkingPlace);

      return newParkingPlace;
    }

    public ParkingPlace findNearestParkingPlaceByCoordinates(double x,double y){

        List<Parking> allParking= parkingRepository.findAll();

        Comparator<Parking> comparator = new Comparator<Parking>(){
            public int compare(Parking o1, Parking o2) {
                double r1=pow( o1.getX()- x,2)+pow(o1.getY()- y,2);

                double r2=pow( o2.getX()- x,2)+pow(o2.getY()- y,2);

                return (r1 < r2) ? -1 : ((r1 == r2) ? 0 : 1);
            }
        };

        allParking.sort(comparator);

        for (Parking nearestParking:allParking) {

            if(parkingRepository.isFree(nearestParking.getId())>0){
//                List<ParkingPlace> nearestParkingPlace=parkingRepository.findFreeParkingPlace(nearestParking.getId());
//                return nearestParkingPlace.get(0);
                List<ParkingPlace> nearestParkingPlace = parkingPlaceRepository.findParkingPlaceByParking_IdAndSensorState(nearestParking.getId(), (byte)1);
                return nearestParkingPlace.get(0);
            }

        }

        return null;
    }

}
