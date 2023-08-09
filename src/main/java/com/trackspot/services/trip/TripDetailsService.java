package com.trackspot.services.trip;


import com.trackspot.entities.EmulatorDetails;
import com.trackspot.entities.trip.AddressComponent;
import com.trackspot.entities.trip.Trip;
import com.trackspot.entities.trip.TripDetailsEntity;

import java.util.List;
import java.util.Optional;

public interface TripDetailsService {
    TripDetailsEntity saveTripDetails(EmulatorDetails emulatorDetails, AddressComponent[] fromAddress, AddressComponent[] toAddress) throws Exception;
    void deleteTripDetailsByEmulatorId(long emulatorId);
    List<TripDetailsEntity> getAllTripDetails();

    Optional<TripDetailsEntity> getTripDetailsFromEmulatorId(long emulatorId);
}
