package com.example.forevgeniy.dao.entities;
import lombok.extern.jackson.Jacksonized;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.Entity;
import java.util.List;

public class StatsResponse {
    public List<Integer> countPerHourAtCertainDate;
    public List<Double> avgCountPerHour;
    public List<Double> occupationPerHourAtCertainDate;
    public List<Double> avgOccupationPerHour;
    public List<Double> avgCountPerWeekDay;
    public List<Double> avgOccupationPerWeekDay;

    public StatsResponse(List<Integer> countPerHourAtCertainDate, List<Double> avgCountPerHour, List<Double> occupationPerHourAtCertainDate, List<Double> avgOccupationPerHour, List<Double> avgCountPerWeekDay, List<Double> avgOccupationPerWeekDay) {
        this.countPerHourAtCertainDate = countPerHourAtCertainDate;
        this.avgCountPerHour = avgCountPerHour;
        this.occupationPerHourAtCertainDate = occupationPerHourAtCertainDate;
        this.avgOccupationPerHour = avgOccupationPerHour;
        this.avgCountPerWeekDay = avgCountPerWeekDay;
        this.avgOccupationPerWeekDay = avgOccupationPerWeekDay;
    }

    public StatsResponse() {
    }
}