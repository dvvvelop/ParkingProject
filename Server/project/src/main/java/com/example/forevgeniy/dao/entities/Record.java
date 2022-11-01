package com.example.forevgeniy.dao.entities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class Record {
    private Integer id;
    private Timestamp timestamp;
    private Byte state;

    private ParkingPlace parkingPlace;

    public void setId(Integer id) {
        this.id = id;
    }

    @Id
    public Integer getId() {
        return id;
    }

    @Column(name = "timestamp")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Column(name = "state")
    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    @ManyToOne
    @JoinColumn(name="parking_place_id", nullable=false, referencedColumnName = "id")
    public ParkingPlace getParkingPlace() {
        return parkingPlace;
    }

    public void setParkingPlace(ParkingPlace parkingPlace) {
        this.parkingPlace = parkingPlace;
    }
}
