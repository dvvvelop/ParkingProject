package com.example.forevgeniy;

import com.example.forevgeniy.dao.entities.ParkingPlace;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.annotation.ApplicationScope;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ForEvgeniyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForEvgeniyApplication.class, args);
    }

    @Bean
    @ApplicationScope
    public List<ParkingPlace> allParkingPlaces() {
        return new ArrayList<>();
    }

}
