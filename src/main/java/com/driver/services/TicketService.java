package com.driver.services;


import com.driver.EntryDto.BookTicketEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.PassengerRepository;
import com.driver.repository.TicketRepository;
import com.driver.repository.TrainRepository;
import com.driver.transformers.TicketTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    PassengerRepository passengerRepository;


    public Integer bookTicket(BookTicketEntryDto bookTicketEntryDto)throws Exception{

        //Check for validity
        //Use bookedTickets List from the TrainRepository to get bookings done against that train
        // Incase the there are insufficient tickets
        // throw new Exception("Less tickets are available");
        //otherwise book the ticket, calculate the price and other details
        //Save the information in corresponding DB Tables
        //Fare System : Check problem statement
        //In case the train doesn't pass through the requested stations
        //throw new Exception("Invalid stations");
        //Save the bookedTickets in the train Object
        //Also in the passenger Entity change the attribute bookedTickets by using the attribute bookingPersonId.
       //And the end return the ticketId that has come from db
        Train train = trainRepository.findById(bookTicketEntryDto.getTrainId()).get();
        List<Ticket> tickets = train.getBookedTickets();
        int cnt = 0;
        for(Ticket ticket : tickets){
            cnt += ticket.getPassengersList().size();
        }
        if(cnt >= train.getNoOfSeats()) throw new Exception("Less tickets are available");
        String route = train.getRoute();
        if(!route.contains(bookTicketEntryDto.getFromStation().toString())
                || !route.contains(bookTicketEntryDto.getToStation().toString())){
            throw new Exception("Invalid stations");
        }
        Ticket bookedTicket = null;
        if(bookTicketEntryDto.getNoOfSeats() <= Math.abs(cnt - train.getNoOfSeats())){
            bookedTicket = TicketTransformer.ticketDtoToTicket(bookTicketEntryDto);
            bookedTicket.setTrain(train);
            bookedTicket.setTotalFare(totalFare(train,bookTicketEntryDto.getFromStation().toString(),bookTicketEntryDto.getToStation().toString()));
            List<Passenger> passengers = new ArrayList<>();
            for(Integer i : bookTicketEntryDto.getPassengerIds()){
                passengers.add(passengerRepository.findById(i).get());
            }
            bookedTicket.setPassengersList(passengers);
            train.getBookedTickets().add(bookedTicket);
            trainRepository.save(train);
        }
        return ticketRepository.save(bookedTicket).getTicketId();

    }
    public int totalFare(Train train,String from , String to){
        String route = train.getRoute();
        int i = 0,j = 0;
        List<String> list = new ArrayList<>();
        while(j < route.length()){
            if(route.charAt(j) != ',') j++;
            else if(route.charAt(j) == ','){
                list.add(route.substring(i,j));
                j++;
                i = j;
            }
        }
        int s = -1,e = -1;
        for(int ii=0; ii<list.size(); ii++){
            if(list.get(ii).equals(from)) s = ii;
            if(list.get(ii).equals(to)) e = ii;
        }
        int totalStations = e - s;
        return (e-s+1)*300;
    }
}
