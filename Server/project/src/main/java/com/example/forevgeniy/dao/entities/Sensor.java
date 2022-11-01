package com.example.forevgeniy.dao.entities;

import javax.persistence.*;

@Entity
public class Sensor {

    private Integer id;

    public Sensor() {
    }

    public Sensor(Integer id, Byte state) {
        this.id = id;
        this.state = state;
    }

    private Byte state; // 0 - busy, 1 - free, 2 - the sensor is missing
    public void setId(Integer id) {
        this.id = id;
    }

    @Id
    public Integer getId() {
        return id;
    }

    @Column(name = "state")
    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

}
