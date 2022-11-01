package com.example.forevgeniy.controller;

import com.example.forevgeniy.dao.entities.Record;
import com.example.forevgeniy.service.StatsService;
import com.example.forevgeniy.dao.entities.StatsResponse;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class StatisticsController {

    @Autowired
    private StatsService statsService;


    @GetMapping("/statistics")
    public LinkedHashMap<String, StatsResponse>  getStatsAboutParkingAndPlace(@RequestParam int parking_id, @RequestParam int parking_place_id, @RequestParam String date, Model model){


        List<List<Record>> recordsOfParking = statsService.getRecordsAboutAllPlacesAtParking(parking_id);
        if (recordsOfParking == null)
            return null;

        int[] daysUsedForEveryHour = new int[24];
        int[] weeksUsedForEveryDayOfWeek = new int[7];
        int[] weekDayStart = new int[1];

        DateTimeFormatter clientServerFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.parse(date, clientServerFormatter);

        LinkedHashMap<String, ArrayList<Integer>> countPerHour = null;
        List<Double> avgCountPerHour = null;
        List<Integer> countPerHourAtCertainDate = null;
        LinkedHashMap<String, ArrayList<Double>> occupationPerHour = null;
        List<Double> avgOccupationPerHour = null;
        List<Double> occupationPerHourAtCertainDate = null;
        LinkedHashMap<String, Integer> countPerDayOfWeek = null;
        List<Double> avgCountPerDayOfWeek = null;
        LinkedHashMap<String, Double> occupationPerDayOfWeek = null;
        List<Double> avgOccupationPerDayOfWeek = null;

        List<Record> records = statsService.getRecordsByParkPlaceId(parking_place_id);
        boolean recordsIsNull = records == null;
        if (!recordsIsNull) {

            countPerHour = statsService.getCountPerHour(records, daysUsedForEveryHour);
            avgCountPerHour = statsService.getAvgCountPerHour(countPerHour, daysUsedForEveryHour);
            countPerHourAtCertainDate = statsService.getCountPerHourAtCertainDate(countPerHour, localDate);


            occupationPerHour = statsService.getOccupationPerHour(records, daysUsedForEveryHour);
            avgOccupationPerHour = statsService.getAvgOccupationPerHour(occupationPerHour, daysUsedForEveryHour);
            occupationPerHourAtCertainDate = statsService.getOccupationPerHourAtCertainDate(occupationPerHour, localDate);


            countPerDayOfWeek = statsService.getCountPerWeekDay(records, weeksUsedForEveryDayOfWeek, weekDayStart);
            avgCountPerDayOfWeek = statsService.getAvgCountPerWeekDay(countPerDayOfWeek, weeksUsedForEveryDayOfWeek, weekDayStart);


            occupationPerDayOfWeek = statsService.getOccupationPerWeekDay(records, weeksUsedForEveryDayOfWeek, weekDayStart);
            avgOccupationPerDayOfWeek = statsService.getAvgOccupationPerWeekDay(occupationPerDayOfWeek, weeksUsedForEveryDayOfWeek, weekDayStart);

        }




        LinkedHashMap<String, ArrayList<Integer>> countPerHourAtParking = statsService.getCountPerHourAtParking(recordsOfParking, daysUsedForEveryHour);
        List<Double> avgCountPerHourAtParking = statsService.getAvgCountPerHour(countPerHourAtParking, daysUsedForEveryHour);
        List<Integer> countPerHourAtCertainDateAtParking = statsService.getCountPerHourAtCertainDate(countPerHourAtParking, localDate);

        LinkedHashMap<String, ArrayList<Double>> occupationPerHourAtParking = statsService.getOccupationPerHourAtParking(recordsOfParking, daysUsedForEveryHour);
        List<Double> avgOccupationPerHourAtParking = statsService.getAvgOccupationPerHour(occupationPerHourAtParking, daysUsedForEveryHour);
        List<Double> occupationPerHourAtCertainDateAtParking = statsService.getOccupationPerHourAtCertainDate(occupationPerHourAtParking, localDate);


        LinkedHashMap<String, Integer> countPerWeekDayAtParking = statsService.getCountPerWeekDayAtParking(recordsOfParking, weeksUsedForEveryDayOfWeek, weekDayStart);
        List<Double> avgCountPerDayOfWeekAtParking = statsService.getAvgCountPerWeekDay(countPerWeekDayAtParking, weeksUsedForEveryDayOfWeek, weekDayStart);

        LinkedHashMap<String, Double> occupationPerDayOfWeekAtParking = statsService.getOccupationPerWeekDayAtParking(recordsOfParking, weeksUsedForEveryDayOfWeek, weekDayStart);
        List<Double> avgOccupationPerDayOfWeekAtParking = statsService.getAvgOccupationPerWeekDay(occupationPerDayOfWeekAtParking, weeksUsedForEveryDayOfWeek, weekDayStart);

        LinkedHashMap<String, StatsResponse> response = new LinkedHashMap<>();
        if (!recordsIsNull)
            response.put("aboutParkingPlace", new StatsResponse(countPerHourAtCertainDate, avgCountPerHour, occupationPerHourAtCertainDate,
                    avgOccupationPerHour, avgCountPerDayOfWeek, avgOccupationPerDayOfWeek));

        response.put("aboutParking", new StatsResponse(countPerHourAtCertainDateAtParking, avgCountPerHourAtParking, occupationPerHourAtCertainDateAtParking,
                avgOccupationPerHourAtParking, avgCountPerDayOfWeekAtParking, avgOccupationPerDayOfWeekAtParking));

        return response;
    }
}
