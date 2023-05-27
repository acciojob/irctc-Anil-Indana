package com.driver.transformers;

import com.driver.EntryDto.AddTrainEntryDto;
import com.driver.model.Station;
import com.driver.model.Train;

import java.util.List;

public class TrainTransformer {
    public static Train trainDtoToTrain(AddTrainEntryDto addTrainEntryDto){
        Train train = new Train();
        train.setRoute(listToString(addTrainEntryDto.getStationRoute()));
        train.setNoOfSeats(addTrainEntryDto.getNoOfSeats());
        train.setDepartureTime(addTrainEntryDto.getDepartureTime());
        return train;
    }
    public static  String listToString(List<Station> stations){
        StringBuilder sb = new StringBuilder();
        for(Station s : stations){
            sb.append(s.toString());
            sb.append(",");
        }
        return sb.toString();
    }
}
