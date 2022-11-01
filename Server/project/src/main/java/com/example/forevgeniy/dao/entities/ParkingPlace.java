package com.example.forevgeniy.dao.entities;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table
public class ParkingPlace {
    private Integer id;
    private Double x;
    private Double y;

    private Parking parking;


    private Sensor sensor;

    public void setId(Integer id) {
        this.id = id;
    }

    @Id
    public Integer getId() {
        return id;
    }

    @Column(name = "x")
    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    @Column(name = "y")
    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    @ManyToOne
    @JoinColumn(name="parking_id", nullable=false)
    public Parking getParking() {
        return parking;
    }

    public void setParking(Parking parking) {
        this.parking = parking;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sensor_id")
    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }
}
