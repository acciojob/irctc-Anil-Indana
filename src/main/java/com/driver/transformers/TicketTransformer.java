package com.driver.transformers;

import com.driver.EntryDto.BookTicketEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Ticket;

import java.util.List;

public class TicketTransformer {
    public static Ticket ticketDtoToTicket(BookTicketEntryDto bookTicketEntryDto){
        Ticket ticket = new Ticket();
        ticket.setFromStation(bookTicketEntryDto.getFromStation());
        ticket.setToStation(bookTicketEntryDto.getToStation());
        return ticket;
    }
}
