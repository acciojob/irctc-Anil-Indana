package com.driver.services;

import com.driver.EntryDto.AddTrainEntryDto;
import com.driver.EntryDto.SeatAvailabilityEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Station;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.TrainRepository;
import com.driver.transformers.TrainTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrainService {

    @Autowired
    TrainRepository trainRepository;

    public Integer addTrain(AddTrainEntryDto trainEntryDto){

        //Add the train to the trainRepository
        //and route String logic to be taken from the Problem statement.
        //Save the train and return the trainId that is generated from the database.
        //Avoid using the lombok library
        Train train = TrainTransformer.trainDtoToTrain(trainEntryDto);

        return trainRepository.save(train).getTrainId();
    }

    public Integer calculateAvailableSeats(SeatAvailabilityEntryDto seatAvailabilityEntryDto){

        //Calculate the total seats available
        //Suppose the route is A B C D
        //And there are 2 seats avaialble in total in the train
        //and 2 tickets are booked from A to C and B to D.
        //The seat is available only between A to C and A to B. If a seat is empty between 2 station it will be counted to our final ans
        //even if that seat is booked post the destStation or before the boardingStation
        //Inshort : a train has totalNo of seats and there are tickets from and to different locations
        //We need to find out the available seats between the given 2 stations.
        Train train = trainRepository.findById(seatAvailabilityEntryDto.getTrainId()).get();
        Station fromStation = seatAvailabilityEntryDto.getFromStation();
        Station toStation = seatAvailabilityEntryDto.getToStation();
        int totalSeats  = train.getNoOfSeats();
        List<Ticket> tickets = train.getBookedTickets();
        for(Ticket ticket : tickets){
            if(ticket.getFromStation().equals(fromStation) && ticket.getToStation().equals(toStation)) {
                totalSeats -= ticket.getPassengersList().size();
            }
        }
       return totalSeats;
    }

    public Integer calculatePeopleBoardingAtAStation(Integer trainId,Station station) throws Exception{

        //We need to find out the number of people who will be boarding a train from a particular station
        //if the trainId is not passing through that station
        //throw new Exception("Train is not passing from this station");
        //  in a happy case we need to find out the number of such people.
        Train train = trainRepository.findById(trainId).get();
        String route = train.getRoute();
        if(!route.contains(station.toString())){
            throw new Exception("Train is not passing from this station");
        }
        List<Ticket> tickets = train.getBookedTickets();
        int cnt = 0;
        for(Ticket ticket : tickets){
            if(ticket.getFromStation().equals(station.toString())) {
                cnt += ticket.getPassengersList().size();
            }
        }
        return cnt;
    }

    public Integer calculateOldestPersonTravelling(Integer trainId){

        //Throughout the journey of the train between any 2 stations
        //We need to find out the age of the oldest person that is travelling the train
        //If there are no people travelling in that train you can return 0
        Train train = trainRepository.findById(trainId).get();
        List<Ticket> tickets = train.getBookedTickets();
        int age = Integer.MIN_VALUE;
        for(Ticket ticket : tickets){
            if(ticket.getTrain() == train){
                for(Passenger passenger : ticket.getPassengersList()){
                    if(passenger.getAge() > age ) age = passenger.getAge();
                }
            }
        }
        return age==Integer.MIN_VALUE?0:age;
    }

    public List<Integer> trainsBetweenAGivenTime(Station station, LocalTime startTime, LocalTime endTime){

        //When you are at a particular station you need to find out the number of trains that will pass through a given station
        //between a particular time frame both start time and end time included.
        //You can assume that the date change doesn't need to be done ie the travel will certainly happen with the same date (More details
        //in problem statement)
        //You can also assume the seconds and milli seconds value will be 0 in a LocalTime format.
        List<Integer> ans = new ArrayList<>();
        List<Train> trains = trainRepository.findAll();
        for(Train train : trains){
            if(train.getRoute().contains(station.toString())){
                if(train.getDepartureTime().equals(endTime) && (train.getDepartureTime().compareTo(startTime)) > 0){
                    ans.add(train.getTrainId());
                }
            }
        }
        return ans;
    }
}
