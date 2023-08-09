package com.trackspot.services.trip;


import com.trackspot.entities.EmulatorDetails;
import com.trackspot.entities.trip.AddressComponent;
import com.trackspot.entities.trip.Trip;
import com.trackspot.entities.trip.TripDetailsEntity;
import com.trackspot.entities.trip.TripDetailsHistoryEntity;

import java.util.List;
import java.util.Optional;

public interface TripDetailsHistoryService {
    TripDetailsHistoryEntity saveTripDetailsHistory(TripDetailsHistoryEntity tripDetailsHistoryEntity) throws Exception;
    void deleteTripHistory(Trip trip);
    List<TripDetailsHistoryEntity> getAllTripDetailsHistory();

    Optional<List<TripDetailsHistoryEntity>> getTripDetailsHistoryFromEmulatorId(long emulatorId);
}
