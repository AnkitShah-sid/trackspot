package com.trackspot.services.trip;


import com.trackspot.entities.EmulatorDetails;
import com.trackspot.entities.trip.Trip;
import com.trackspot.entities.trip.TripPointEmulatorResponse;

import java.util.List;

public interface TripService {
    Trip createTrip(Trip trip) throws Exception;
    void deleteTrip(Long id);
    List<EmulatorDetails> getAllTrips();
    Trip getTripOfEmulatorId(long emulatorId) throws Exception;
    TripPointEmulatorResponse getSyncedDatabaseOfEmulatorId(long emulatorId) throws Exception;
    TripPointEmulatorResponse getSyncedDatabaseOfEmulatorSsId(String emulatorId) throws Exception;
    EmulatorDetails toggleTripFromEmId(long emulatorId) throws Exception;
}
