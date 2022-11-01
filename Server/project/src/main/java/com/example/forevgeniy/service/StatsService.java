package com.example.forevgeniy.service;

import com.example.forevgeniy.dao.entities.ParkingPlace;
import com.example.forevgeniy.dao.entities.Record;
import com.example.forevgeniy.repos.StatsRepository;
import com.example.forevgeniy.service.forStats.StringComparator;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.*;

@Service
@AllArgsConstructor
public class StatsService {

    @Autowired
    private StatsRepository statsRepository;
    @Autowired
    private ParkingPlaceService parkingPlaceService;

    public List<Record> getRecordsByParkPlaceId(int parking_place_id){
        List<Record> records =  statsRepository.findAllRecordsByParkingPlaceId(parking_place_id);
        if (records.isEmpty())
            return null;
        return records;
    }

//    public List<Integer> getParkPlacesIdListAtParking(int parking_id){
//        List<ParkingPlace> parkingPlaceList = parkingPlaceService.findAllPlacesForParking(parking_id);
//        List<Integer> parkPlaceIdList = new ArrayList<>();
//        for (ParkingPlace parkingPlace : parkingPlaceList){
//            parkPlaceIdList.add(parkingPlace.getId());
//        }
//        return parkPlaceIdList;
//    }
//

    public List<List<Record>> getRecordsAboutAllPlacesAtParking (int parking_id){
        List<ParkingPlace> parkingPlaceList = parkingPlaceService.findAllPlacesForParking(parking_id);
        List<List<Record>> recordsAboutAllPlacesAtParking = new ArrayList<>();
        for (ParkingPlace parkingPlace : parkingPlaceList){
            List<Record> recordsOfParkPlace = getRecordsByParkPlaceId(parkingPlace.getId());
            if (recordsOfParkPlace != null)
                recordsAboutAllPlacesAtParking.add(recordsOfParkPlace);
        }
        if (recordsAboutAllPlacesAtParking.isEmpty())
            return null;
        return recordsAboutAllPlacesAtParking;
    }

    public LinkedHashMap<String, ArrayList<Integer>> getCountPerHour(@NotNull List<Record> records, int[] daysUsed){

        LinkedHashMap<String, ArrayList<Integer>>  dateTimeCountMap = new LinkedHashMap<>();

        if (records.isEmpty())
            return dateTimeCountMap;

        for (int hour = 0; hour < 24; ++hour)
            daysUsed[hour] = 0;


        LocalDateTime initialDateTime = records.get(0).getTimestamp().toLocalDateTime();
        LocalDate initialDate = initialDateTime.toLocalDate();
        LocalDate endDate = records.get(records.size() - 1).getTimestamp().toLocalDateTime().toLocalDate();


        int recordIndex = 0;
        byte currState = 1;
        byte nextState;
        boolean needToCheck = false;
        for (LocalDate currDate = initialDate.plusDays(0); !currDate.isAfter(endDate) ; currDate = currDate.plusDays(1)){
            ArrayList<Integer> countPerHour = new ArrayList<>();
            needToCheck = currDate.isEqual(initialDate) || currDate.isEqual(LocalDate.now()) ;
            if (records.get(recordIndex).getTimestamp().toLocalDateTime().toLocalDate().isEqual(currDate)){
                for (int currHour = 0; currHour < 24; ++currHour) {
                    if (needToCheck &&
                            ( currDate.isEqual(initialDate) && currHour < initialDateTime.getHour()
                                    || currDate.isEqual(LocalDate.now()) && currHour > LocalTime.now().getHour())){
                        countPerHour.add(Integer.MIN_VALUE);
                        continue;
                    }
                    ++daysUsed[currHour];

                    if (recordIndex >= records.size()
                            || currHour < records.get(recordIndex).getTimestamp().toLocalDateTime().toLocalTime().getHour()
                            || records.get(recordIndex).getTimestamp().toLocalDateTime().toLocalDate().isAfter(currDate))
                        countPerHour.add(currState == 0 ? 1 : 0);
                    else if (records.get(recordIndex).getTimestamp().toLocalDateTime().toLocalDate().isEqual(currDate)
                            && records.get(recordIndex).getTimestamp().toLocalDateTime().toLocalTime().getHour() == currHour)
                    {
                        int countOfStates = 1;
                        byte firstState;
                        nextState = records.get(recordIndex++).getState();

                        currState = nextState;
                        firstState = currState;

                        while (recordIndex < records.size()
                                && records.get(recordIndex).getTimestamp().toLocalDateTime().toLocalDate().isEqual(currDate)
                                && records.get(recordIndex).getTimestamp().toLocalDateTime().toLocalTime().getHour() == currHour)
                        {
                            nextState = records.get(recordIndex++).getState();
                            if (nextState == currState)
                                continue;
                            currState = nextState;
                            ++countOfStates;
                        }

                        int nPeople;
                        if (countOfStates % 2 == 0 && firstState == 0)
                            nPeople = countOfStates / 2;
                        else
                            nPeople = countOfStates / 2 + 1;

                        countPerHour.add(nPeople);
                    }

                }
            }else{
                for (int currHour = 0; currHour < 24; ++currHour) {
                    ++daysUsed[currHour];
                    countPerHour.add(currState == 0 ? 1 : 0);
                }

            }
            dateTimeCountMap.put(currDate + " " + currDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()), countPerHour);

        }


        if (endDate.isBefore(LocalDate.now()))
            for (LocalDate currDate = endDate.plusDays(1); !currDate.isAfter(LocalDate.now()); currDate = currDate.plusDays(1)) {
                ArrayList<Integer> countPerHour = new ArrayList<>();
                needToCheck = currDate.isEqual(LocalDate.now());
                for (int currHour = 0; currHour < 24; ++currHour) {
                    if (needToCheck && currHour > LocalTime.now().getHour()) {
                        countPerHour.add(Integer.MIN_VALUE);
                        continue;
                    }
                    ++daysUsed[currHour];
                    countPerHour.add(currState == 0 ? 1 : 0);
                }
                dateTimeCountMap.put(currDate + " " + currDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()), countPerHour);
            }
        return dateTimeCountMap;
    }//debugged full ready mega super
    public List<Integer> getCountPerHourAtCertainDate(@NotNull LinkedHashMap<String, ArrayList<Integer>> countPerHourAtAllDays, LocalDate certainDate){
        String strDate = certainDate + " " + certainDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        if (countPerHourAtAllDays.containsKey(strDate))
            return countPerHourAtAllDays.get(strDate);
        return new ArrayList<Integer>();
    }
    public List<Double> getAvgCountPerHour(LinkedHashMap<String, ArrayList<Integer>> dateTimeCountMap, int[] daysUsed){
        List<Double> avgCountPerHour = new ArrayList<>();
        if (dateTimeCountMap.isEmpty())
            return avgCountPerHour;

        for (int i = 0; i < 24; ++i)
            avgCountPerHour.add(0.0);

        for(Map.Entry<String, ArrayList<Integer> > mapEntry : dateTimeCountMap.entrySet()) {
            int hour = 0;
            for (int countPerHour : mapEntry.getValue()) {
                if (countPerHour != Integer.MIN_VALUE)
                    avgCountPerHour.set(hour, avgCountPerHour.get(hour) + countPerHour);
                ++hour;
            }
        }
        //!!3384049
        for (int hour = 0; hour < 24; ++hour)
            avgCountPerHour.set(hour, (double)avgCountPerHour.get(hour) / daysUsed[hour]);

        return avgCountPerHour;
    }

    public LinkedHashMap<String, Integer> getCountPerWeekDay(@NotNull List<Record> records, int[] weekDaysUsed, int[] weekDayStart){

        LinkedHashMap<String, Integer> countPerWeekDay = new LinkedHashMap<>();

        if (records.isEmpty())
            return null;

        LocalDate initialDate = records.get(0).getTimestamp().toLocalDateTime().toLocalDate();
        LocalDate endDate = records.get(records.size() - 1).getTimestamp().toLocalDateTime().toLocalDate();

        for (int weekday = 0; weekday < 7; ++weekday)
            weekDaysUsed[weekday] = 0;
        weekDayStart[0] = initialDate.getDayOfWeek().getValue() - 1;

        int recordIndex = 0;
        byte currState = 1;
        byte nextState;
        for (LocalDate iteratorDate = initialDate.plusDays(0);!iteratorDate.isAfter(endDate); iteratorDate = iteratorDate.plusDays(1)){
            ++weekDaysUsed[iteratorDate.getDayOfWeek().getValue() - 1];
            if (iteratorDate.isBefore(records.get(recordIndex).getTimestamp().toLocalDateTime().toLocalDate())){
                countPerWeekDay.put(iteratorDate + " " + iteratorDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()), currState == 0 ? 1 : 0);
            }
            else if (iteratorDate.isEqual(records.get(recordIndex).getTimestamp().toLocalDateTime().toLocalDate())){

                nextState = records.get(recordIndex++).getState();
//                if (nextState == currState)
//                    continue;
                currState = nextState;
                int firstState = currState;
                int nStates = 1;

                while (recordIndex < records.size()
                        && iteratorDate.isEqual(records.get(recordIndex).getTimestamp().toLocalDateTime().toLocalDate())) {

                    nextState = records.get(recordIndex++).getState();
                    if (nextState == currState)
                        continue;
                    currState = nextState;
                    ++nStates;
                }

                if (nStates % 2 == 0 && firstState == 0)
                    countPerWeekDay.put(iteratorDate + " " + iteratorDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()), nStates / 2);
                else
                    countPerWeekDay.put(iteratorDate + " " + iteratorDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()), nStates / 2 + 1);
            }
        }
        for (LocalDate currDate = endDate.plusDays(1); !currDate.isAfter(LocalDate.now()); currDate = currDate.plusDays(1)) {
            ++weekDaysUsed[currDate.getDayOfWeek().getValue() - 1];
            countPerWeekDay.put(currDate + " " + currDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()), currState == 0 ? 1 : 0);
        }


        return countPerWeekDay;
    }//debugged full ready mega super
    public List<Double> getOccupationPerHourAtCertainDate(@NotNull LinkedHashMap<String, ArrayList<Double>> occupationPerHourAtAllDays, LocalDate certainDate){
        String strDate = certainDate + " " + certainDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        if (occupationPerHourAtAllDays.containsKey(strDate))
            return occupationPerHourAtAllDays.get(strDate);
        return new ArrayList<Double>();
    }
    public List<Double> getAvgCountPerWeekDay(LinkedHashMap<String, Integer> countPerWeekDay, int[] weekDayUsed, int[] weekDayStart){
        List<Double> avgCountPerWeekDay = new ArrayList<>();

        if (countPerWeekDay.isEmpty())
            return avgCountPerWeekDay;

        for (int weekDay = 0; weekDay < 7; ++weekDay)
            avgCountPerWeekDay.add(0.0);

        int weekDay = weekDayStart[0];
        for(Map.Entry<String, Integer> mapEntry : countPerWeekDay.entrySet()){
            avgCountPerWeekDay.set(weekDay, avgCountPerWeekDay.get(weekDay) + mapEntry.getValue());
            weekDay = (weekDay + 1) % 7;

        }

        for (int i = 0; i < 7; ++i){
            avgCountPerWeekDay.set(i, avgCountPerWeekDay.get(i)/(double)weekDayUsed[i]);
        }

        return avgCountPerWeekDay;
    }


    public LinkedHashMap<String, Double> getOccupationPerWeekDay (@NotNull List<Record> records, int[] weekDaysUsed, int  [] weekDayStart){

        LinkedHashMap<String, Double> occupationPerWeekDay = new LinkedHashMap<>();

        if (records.isEmpty())
            return occupationPerWeekDay;

        LocalDate initialDate = records.get(0).getTimestamp().toLocalDateTime().toLocalDate();
        LocalDate endDate = records.get(records.size() - 1).getTimestamp().toLocalDateTime().toLocalDate();

        weekDayStart[0] = initialDate.getDayOfWeek().getValue() - 1;
        for (int weekday = 0; weekday < 7; ++weekday)
            weekDaysUsed[weekday] = 0;

        final int secondsOfDay = 24 * 3600;
        byte currState = 1;
        Timestamp startTime = null;
        int recordIndex = 0;

        for (LocalDate iteratorDate = initialDate.plusDays(0) ;!iteratorDate.isAfter(endDate);iteratorDate = iteratorDate.plusDays(1)){
            ++weekDaysUsed[iteratorDate.getDayOfWeek().getValue() - 1];
            if (iteratorDate.isBefore(records.get(recordIndex).getTimestamp().toLocalDateTime().toLocalDate()))
                occupationPerWeekDay.put(iteratorDate + " " + iteratorDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()), currState == 0 ? 100.0 : 0.0);
            else if (iteratorDate.isEqual(records.get(recordIndex).getTimestamp().toLocalDateTime().toLocalDate()))
            {
                int totalSecondsOccupation = 0;
                boolean isLastRecord = false;
                while (recordIndex < records.size()
                        && iteratorDate.isEqual(records.get(recordIndex).getTimestamp().toLocalDateTime().toLocalDate())) {

                    if (currState == 1) {
                        currState = records.get(recordIndex).getState();
                        if (currState == 1) {
                            ++recordIndex;
                            continue;
                        }
                        startTime = records.get(recordIndex).getTimestamp();
                        if (recordIndex == records.size() - 1
                                || !thereIsARecord(records, iteratorDate, (byte)1, recordIndex)){
                            isLastRecord = true;
                            occupationPerWeekDay.put(iteratorDate + " " + iteratorDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()),  (double)( totalSecondsOccupation + secondsOfDay - startTime.toLocalDateTime().toLocalTime().toSecondOfDay()) / secondsOfDay * 100);
                        }
                    } else {
                        currState = records.get(recordIndex).getState();
                        if (currState == 0) {
                            ++recordIndex;
                            continue;
                        }
                        LocalTime endTime = records.get(recordIndex).getTimestamp().toLocalDateTime().toLocalTime();
                        if (iteratorDate.isAfter(startTime.toLocalDateTime().toLocalDate()))
                            totalSecondsOccupation += endTime.toSecondOfDay();
                        else
                            totalSecondsOccupation += endTime.toSecondOfDay() - startTime.toLocalDateTime().toLocalTime().toSecondOfDay();
                    }
                    ++recordIndex;
                }
                if (!isLastRecord)
                    occupationPerWeekDay.put(iteratorDate + " " + iteratorDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()), (double)totalSecondsOccupation/secondsOfDay * 100);
            }
        }

        if (endDate.isBefore(LocalDate.now())) {
            for (LocalDate currDate = endDate.plusDays(1); currDate.isBefore(LocalDate.now()); currDate = currDate.plusDays(1)) {
                ++weekDaysUsed[currDate.getDayOfWeek().getValue() - 1];
                occupationPerWeekDay.put(currDate + " " + currDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()), currState == 0 ? 100.0 : 0.0);
            }

            ++weekDaysUsed[LocalDate.now().getDayOfWeek().getValue() - 1];
            occupationPerWeekDay.put(LocalDate.now() + " " + LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()), currState == 0 ? (double) LocalTime.now().toSecondOfDay() / secondsOfDay * 100.0 : 0.0);
        }
        return occupationPerWeekDay;
    }//debugged full ready mega super
    public List<Double> getAvgOccupationPerWeekDay(LinkedHashMap<String, Double> occupationPerWeekDay, int[] weekDaysUsed, int[] weekDayStart){
        List<Double> avgOccupationPerWeekDay = new ArrayList<>();



        for (int i = 0; i < 7; ++i)
            avgOccupationPerWeekDay.add(0.0);

        int weekDay = weekDayStart[0];

        for (Map.Entry<String, Double> mapEntry : occupationPerWeekDay.entrySet()){
            avgOccupationPerWeekDay.set(weekDay, avgOccupationPerWeekDay.get(weekDay) + mapEntry.getValue());
            weekDay = (weekDay + 1) % 7;
        }

        for (int i = 0; i < 7; ++i)
            avgOccupationPerWeekDay.set(i, avgOccupationPerWeekDay.get(i) / weekDaysUsed[i]);

        return avgOccupationPerWeekDay;
    }


    public LinkedHashMap<String, ArrayList<Double>> getOccupationPerHour(@NotNull List<Record> records, int[] daysUsed){
        LinkedHashMap<String, ArrayList<Double>> occupationPerHour = new LinkedHashMap<>();
        for (int hour = 0; hour < 24; ++hour)
            daysUsed[hour] = 0;

        LocalDateTime initialDateTime = records.get(0).getTimestamp().toLocalDateTime();
        LocalDate initialDate = initialDateTime.toLocalDate();
        LocalDate endDate = records.get(records.size() - 1).getTimestamp().toLocalDateTime().toLocalDate();

        final int secondsInHour = 3600;

        int recordIndex = 0;
        byte currState = 1;
        Timestamp startTime = null;
        boolean needToCheck = false;

        for (LocalDate currDate = initialDate; !currDate.isAfter(endDate); currDate = currDate.plusDays(1)){
            ArrayList<Double> hoursADay = new ArrayList<>();
            needToCheck = currDate.isEqual(initialDate) || currDate.isEqual(LocalDate.now());
            for (int currHour = 0; currHour < 24; ++currHour){
                if (needToCheck &&
                        (currDate.isEqual(initialDate) && currHour < initialDateTime.getHour()
                                || currDate.isEqual(LocalDate.now()) && currHour > LocalTime.now().getHour())){
                    hoursADay.add(Double.NEGATIVE_INFINITY);
                    continue;
                }
                ++daysUsed[currHour];
                if (recordIndex == records.size()
                        || currHour < records.get(recordIndex).getTimestamp().toLocalDateTime().toLocalTime().getHour()
                        || currDate.isBefore(records.get(recordIndex).getTimestamp().toLocalDateTime().toLocalDate()))
                {
                    hoursADay.add(currState == 1 ? 0.0 : 100.0);
                }
                else {
                    int totalSecondsOfHour = 0;
                    boolean isLastRecordOfHour = false;
                    while (recordIndex != records.size()
                            && (currHour == records.get(recordIndex).getTimestamp().toLocalDateTime().getHour()
                            && currDate.isEqual(records.get(recordIndex).getTimestamp().toLocalDateTime().toLocalDate())) )
                    {
                        if (currState == 1){
                            currState = records.get(recordIndex).getState();
                            if (currState == 1){
                                ++recordIndex;
                                continue;
                            }
                            startTime = records.get(recordIndex).getTimestamp();
                            if (recordIndex == records.size() - 1
                                    || !thereIsARecord(records, currDate, currHour, (byte)1, recordIndex))
                            {
                                isLastRecordOfHour = true;
                                int timeToEndOfHour = secondsInHour - startTime.toLocalDateTime().getMinute()* 60 + startTime.toLocalDateTime().getSecond();
                                hoursADay.add((double)(totalSecondsOfHour + timeToEndOfHour)/secondsInHour * 100);
                            }
                        }
                        else {
                            currState = records.get(recordIndex).getState();
                            if (currState == 0){
                                ++recordIndex;
                                continue;
                            }
                            LocalDateTime endTime = records.get(recordIndex).getTimestamp().toLocalDateTime();
                            if (endTime.getHour() > startTime.toLocalDateTime().getHour()
                                    || endTime.toLocalDate().isAfter(startTime.toLocalDateTime().toLocalDate()))
                                totalSecondsOfHour += endTime.getMinute() * 60 + endTime.getSecond();
                            else
                                totalSecondsOfHour += (endTime.getMinute() - startTime.toLocalDateTime().getMinute()) * 60 + endTime.getSecond() - startTime.toLocalDateTime().getSecond();
                        }
                        ++recordIndex;
                    }
                    if (!isLastRecordOfHour)
                        hoursADay.add(((double)totalSecondsOfHour / secondsInHour) * 100);
                }
            }

            occupationPerHour.put(currDate + " " + currDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()), hoursADay);

        }

        if (endDate.isBefore(LocalDate.now()))
            for (LocalDate currDate = endDate.plusDays(1); !currDate.isAfter(LocalDate.now()); currDate = currDate.plusDays(1)) {
                ArrayList<Double> hoursADay = new ArrayList<>();
                needToCheck = currDate.isEqual(LocalDate.now());
                for (int currHour = 0; currHour < 24; ++currHour) {
                    if (needToCheck && currHour > LocalTime.now().getHour()) {
                        hoursADay.add(Double.NEGATIVE_INFINITY);
                        continue;
                    }
                    else if (needToCheck && currHour == LocalTime.now().getHour()){
                        ++daysUsed[currHour];
                        hoursADay.add(currState == 0 ? (double) (LocalTime.now().getSecond() + LocalTime.now().getMinute() * 60) / 3600 * 100: 0.0);
                        continue;
                    }
                    ++daysUsed[currHour];
                    hoursADay.add(currState == 0 ? 100.0 : 0.0);
                }
                occupationPerHour.put(currDate + " " + currDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()), hoursADay);
            }

        return occupationPerHour;
    }//debugged full ready mega super
    public List<Double> getAvgOccupationPerHour (LinkedHashMap<String, ArrayList<Double>> occupationPerHour, int[] daysUsed){
        List<Double> avgCountPerHour = new ArrayList<>();

        for (int i = 0; i < 24; ++i)
            avgCountPerHour.add(0.0);

        for(Map.Entry<String, ArrayList<Double> > mapEntry : occupationPerHour.entrySet()) {
            int hour = 0;
            for (double countPerHour : mapEntry.getValue()) {
                if (Double.isFinite(countPerHour))
                    avgCountPerHour.set(hour, avgCountPerHour.get(hour) + countPerHour);
                ++hour;
            }
        }
        //!!3384049
        for (int hour = 0; hour < 24; ++hour)
            avgCountPerHour.set(hour, (double)avgCountPerHour.get(hour) / daysUsed[hour]);

        return avgCountPerHour;
    }



    private boolean thereIsARecord(@NotNull List<Record> records, LocalDate date, byte state, int recordIndex){

        while (recordIndex < records.size()) {
            if (date.isEqual(records.get(recordIndex).getTimestamp().toLocalDateTime().toLocalDate())
                    && state == records.get(recordIndex).getState())
                return true;
            ++recordIndex;
        }
        return false;
    }

    private boolean thereIsARecord(@NotNull List<Record> records, LocalDate date, int hour, byte state, int recordIndex){
        while (recordIndex < records.size()) {
            if (date.isEqual(records.get(recordIndex).getTimestamp().toLocalDateTime().toLocalDate())
                    && hour == records.get(recordIndex).getTimestamp().toLocalDateTime().getHour()
                    && state == records.get(recordIndex).getState())
                return true;
            ++recordIndex;
        }
        return false;
    }

    private int[] getArrayWithMaxElements(List<int[]> weeksUsedList, int size){
        int[] weeksUsed = new int[size];
        for (int weekDay = 0; weekDay < size; ++weekDay){
            int maxDaysUsed = weeksUsedList.get(0)[weekDay];
            for (int[] weekDaysUsedInList : weeksUsedList)
                maxDaysUsed = Math.max(maxDaysUsed, weekDaysUsedInList[weekDay]);
            weeksUsed[weekDay] = maxDaysUsed;
        }
        return weeksUsed;
    }




    public LinkedHashMap<String, Integer> getCountPerWeekDayAtParking(@NotNull List<List<Record>> records, int[] weeksUsed, int[] weekDayStart){

        LinkedHashMap<String, Integer> countPerWeekDayAtParking = new LinkedHashMap<>();
        List<int[]> weeksUsedList = new ArrayList<>();
        List<String> firstDates = new ArrayList<>();

        for (List<Record> record : records) {
            LinkedHashMap<String, Integer> countPerWeekDayForOneParking = getCountPerWeekDay(record, weeksUsed, weekDayStart);
            for (Map.Entry<String, Integer> mapEntry : countPerWeekDayForOneParking.entrySet()) {
                firstDates.add(mapEntry.getKey());
                break;
            }
        }
        firstDates.sort(new StringComparator());
        for (String firstDate : firstDates)
            countPerWeekDayAtParking.put(firstDate, 0);

        weeksUsedList.add(weeksUsed.clone());

        for (List<Record> record : records) {
            LinkedHashMap<String, Integer> countPerWeekDayForOneParking = getCountPerWeekDay(record, weeksUsed, weekDayStart);
            weeksUsedList.add(weeksUsed.clone());
            for (Map.Entry<String, Integer> mapEntry : countPerWeekDayForOneParking.entrySet()) {
                if (countPerWeekDayAtParking.containsKey(mapEntry.getKey()))
                    countPerWeekDayAtParking.put(mapEntry.getKey(), countPerWeekDayAtParking.get(mapEntry.getKey()) + mapEntry.getValue());
                else
                    countPerWeekDayAtParking.put(mapEntry.getKey(), mapEntry.getValue());
            }
        }

        weeksUsed = getArrayWithMaxElements(weeksUsedList, 7);

        String strMinDate = firstDates.get(0);
        weekDayStart[0] = LocalDate.parse(strMinDate.split("\\s", 0)[0]).getDayOfWeek().getValue() - 1;

        return countPerWeekDayAtParking;
    }


    public LinkedHashMap<String, Double> getOccupationPerWeekDayAtParking(@NotNull List<List<Record>> records, int[] weeksUsed, int[] weekDayStart){


        LinkedHashMap<String, Double> occupationPerWeekDayAtParking = new LinkedHashMap<>();
        List<int[]> weeksUsedList = new ArrayList<>();
        List<String> firstDates = new ArrayList<>();

        for (List<Record> record : records) {
            LinkedHashMap<String, Double> occupationPerWeekDayForOnePlace = getOccupationPerWeekDay(record, weeksUsed, weekDayStart);
            for (Map.Entry<String, Double> mapEntry : occupationPerWeekDayForOnePlace.entrySet()) {
                firstDates.add(mapEntry.getKey());
                break;
            }
        }
        firstDates.sort(new StringComparator());
        for (String firstDate : firstDates)
            occupationPerWeekDayAtParking.put(firstDate, 0.0);

        weeksUsedList.add(weeksUsed.clone());

        for (List<Record> record : records) {
            LinkedHashMap<String, Double> occupationPerWeekDayForOnePlace = getOccupationPerWeekDay(record, weeksUsed, weekDayStart);
            weeksUsedList.add(weeksUsed.clone());
            for (Map.Entry<String, Double> mapEntry : occupationPerWeekDayForOnePlace.entrySet()) {
                if (occupationPerWeekDayAtParking.containsKey(mapEntry.getKey()))
                    occupationPerWeekDayAtParking.put(mapEntry.getKey(), occupationPerWeekDayAtParking.get(mapEntry.getKey()) + mapEntry.getValue());
                else
                    occupationPerWeekDayAtParking.put(mapEntry.getKey(), mapEntry.getValue());
            }
        }

        weeksUsed = getArrayWithMaxElements(weeksUsedList, 7);

        String strMinDate = firstDates.get(0);
        weekDayStart[0] = LocalDate.parse(strMinDate.split("\\s", 0)[0]).getDayOfWeek().getValue() - 1;

        for (Map.Entry<String, Double> mapEntry : occupationPerWeekDayAtParking.entrySet())
            occupationPerWeekDayAtParking.put(mapEntry.getKey(), mapEntry.getValue() / records.size());

        return occupationPerWeekDayAtParking;
    }

    public LinkedHashMap<String, ArrayList<Integer>> getCountPerHourAtParking(@NotNull List<List<Record>> records, int[] daysUsed){
        LinkedHashMap<String, ArrayList<Integer>> countPerHourAtParking = new LinkedHashMap<>();
        List<int[]> daysUsedList = new ArrayList<>();
        List<String> firstDates = new ArrayList<>();

        for(List<Record> records1 : records){
            LinkedHashMap<String, ArrayList<Integer>> countPerHourForOnePlace = getCountPerHour(records1, daysUsed);
            for (Map.Entry<String, ArrayList<Integer>> mapEntry : countPerHourForOnePlace.entrySet()){
                firstDates.add(mapEntry.getKey());
                break;
            }

        }

        firstDates.sort(new StringComparator());
        ArrayList<Integer> zeroCounts = new ArrayList<>();
        for (int i = 0; i < 24; ++i)
            zeroCounts.add(Integer.MIN_VALUE);

        for (String firstDate : firstDates)
            countPerHourAtParking.put(firstDate, zeroCounts);

        for (List<Record> records1 : records){
            LinkedHashMap<String, ArrayList<Integer>> countPerHourForOnePlace = getCountPerHour(records1, daysUsed);
            daysUsedList.add(daysUsed);
            for (Map.Entry<String, ArrayList<Integer>> mapEntry : countPerHourForOnePlace.entrySet()){
                ArrayList<Integer> hoursPerDayForPlace = mapEntry.getValue();
                if (countPerHourAtParking.containsKey(mapEntry.getKey())) {
                    ArrayList<Integer> hoursPerDayForParking = (ArrayList<Integer>) countPerHourAtParking.get(mapEntry.getKey()).clone();
                    for (int i = 0; i < 24; ++i) {
                        if (hoursPerDayForParking.get(i) == Integer.MIN_VALUE && hoursPerDayForPlace.get(i) == Integer.MIN_VALUE)
                        {}
                        else if (hoursPerDayForParking.get(i) == Integer.MIN_VALUE)
                            hoursPerDayForParking.set(i, hoursPerDayForPlace.get(i));
                        else if (hoursPerDayForPlace.get(i) != Integer.MIN_VALUE)
                            hoursPerDayForParking.set(i, hoursPerDayForParking.get(i) + hoursPerDayForPlace.get(i));
                    }

                    countPerHourAtParking.put(mapEntry.getKey(), hoursPerDayForParking);
                }
                else
                    countPerHourAtParking.put(mapEntry.getKey(), hoursPerDayForPlace);
            }
        }
        daysUsed = getArrayWithMaxElements(daysUsedList, 24);
        return countPerHourAtParking;
    }

    public LinkedHashMap<String, ArrayList<Double>> getOccupationPerHourAtParking(@NotNull List<List<Record>> records, int[] daysUsed){
        LinkedHashMap<String, ArrayList<Double>> occupationPerHourAtParking = new LinkedHashMap<>();
        List<int[]> daysUsedList = new ArrayList<>();
        List<String> firstDates = new ArrayList<>();

        for(List<Record> records1 : records){
            LinkedHashMap<String, ArrayList<Double>> occupationPerHourForOnePlace = getOccupationPerHour(records1, daysUsed);
            for (Map.Entry<String, ArrayList<Double>> mapEntry : occupationPerHourForOnePlace.entrySet()){
                firstDates.add(mapEntry.getKey());
                break;
            }
        }

        firstDates.sort(new StringComparator());
        ArrayList<Double> zeroCounts = new ArrayList<>();
        for (int i = 0; i < 24; ++i)
            zeroCounts.add(Double.NEGATIVE_INFINITY);

        for (String firstDate : firstDates)
            occupationPerHourAtParking.put(firstDate, zeroCounts);

        for (List<Record> records1 : records){
            LinkedHashMap<String, ArrayList<Double>> occupationPerHourForOnePlace = getOccupationPerHour(records1, daysUsed);
            daysUsedList.add(daysUsed);
            for (Map.Entry<String, ArrayList<Double>> mapEntry : occupationPerHourForOnePlace.entrySet()){
                ArrayList<Double> hoursPerDayForPlace = mapEntry.getValue();
                if (occupationPerHourAtParking.containsKey(mapEntry.getKey())) {
                    ArrayList<Double> hoursPerDayForParking = (ArrayList<Double>) occupationPerHourAtParking.get(mapEntry.getKey()).clone();
                    for (int i = 0; i < 24; ++i) {
                        if (hoursPerDayForParking.get(i) == Double.NEGATIVE_INFINITY && hoursPerDayForPlace.get(i) == Double.NEGATIVE_INFINITY)
                        {}
                        else if (hoursPerDayForParking.get(i) == Double.NEGATIVE_INFINITY)
                            hoursPerDayForParking.set(i, hoursPerDayForPlace.get(i));
                        else if (hoursPerDayForPlace.get(i) != Double.NEGATIVE_INFINITY)
                            hoursPerDayForParking.set(i, hoursPerDayForParking.get(i) + hoursPerDayForPlace.get(i));
                    }

                    occupationPerHourAtParking.put(mapEntry.getKey(), hoursPerDayForParking);
                }
                else
                    occupationPerHourAtParking.put(mapEntry.getKey(), hoursPerDayForPlace);
            }
        }
        daysUsed = getArrayWithMaxElements(daysUsedList, 24);
        for (Map.Entry<String, ArrayList<Double>> mapEntry : occupationPerHourAtParking.entrySet()){
            ArrayList<Double> occupationADay = (ArrayList<Double>) mapEntry.getValue().clone();
            for (int i = 0; i < 24; ++i)
                occupationADay.set(i, occupationADay.get(i) / records.size());

            occupationPerHourAtParking.put(mapEntry.getKey(), occupationADay);
        }

        return occupationPerHourAtParking;
    }

}
