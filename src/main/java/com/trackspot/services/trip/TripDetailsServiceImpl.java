package com.trackspot.services.trip;

import com.trackspot.entities.EmulatorDetails;
import com.trackspot.entities.trip.AddressComponent;
import com.trackspot.entities.trip.Trip;
import com.trackspot.entities.trip.TripDetailsEntity;
import com.trackspot.entities.trip.TripDetailsHistoryEntity;
import com.trackspot.maps.GoogleMapsService;
import com.trackspot.repositories.EmulatorDetailsRepository;
import com.trackspot.repositories.TripDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class TripDetailsServiceImpl implements TripDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(TripDetailsServiceImpl.class);

    @Autowired
    private EmulatorDetailsRepository emulatorDetailsRepo;
    @Autowired
    private TripDetailsRepository tripDetailsRepository;
    @Autowired
    private TripDetailsHistoryService tripDetailsHistoryService;

    @Override
    public TripDetailsEntity saveTripDetails(EmulatorDetails emulatorDetails, AddressComponent[] fromAddress, AddressComponent[] toAddress) throws Exception {
        double startLat = emulatorDetails.getStartLat();
        double startLng = emulatorDetails.getStartLong();
        double endLat = emulatorDetails.getEndLat();
        double endLng = emulatorDetails.getEndLong();
        int speed = emulatorDetails.getSpeed();
        TripDetailsEntity savedTripDetails = null;
        try {
            // Use Google Maps API to calculate route points
            TripDetailsHistoryEntity historyEntity = new TripDetailsHistoryEntity();
            savedTripDetails = new GoogleMapsService().calculateRoutePointsAndVelocity(startLat, startLng, endLat, endLng, speed, historyEntity);
            savedTripDetails.setId(emulatorDetails.getId().toString());
            savedTripDetails.setFromAddress(fromAddress);
            savedTripDetails.setToAddress(toAddress);
            // Save trip details to MongoDB
            savedTripDetails = tripDetailsRepository.save(savedTripDetails);

            historyEntity.setTripDetailsId(emulatorDetails.getId().toString());
            historyEntity.setFromAddress(fromAddress);
            historyEntity.setToAddress(toAddress);
            historyEntity.setId(null);
            // Save trip details history to MongoDB
            tripDetailsHistoryService.saveTripDetailsHistory(historyEntity);
        } catch (Exception e) {
            logger.error("Error at saveTripDetails : " + e);
        }
        return savedTripDetails;
    }

    @Override
    public Optional<TripDetailsEntity> getTripDetailsFromEmulatorId(long emulatorId) {
        if (emulatorId == (int) emulatorId) {
            return tripDetailsRepository.findById(String.valueOf(emulatorId));
        } else {
            throw new IllegalArgumentException("Emulator Does not have a set Trip route");
        }
    }

    @Override
    public void deleteTripDetailsByEmulatorId(long emulatorId) {
        tripDetailsRepository.deleteById(String.valueOf(emulatorId));
    }

    @Override
    public List<TripDetailsEntity> getAllTripDetails() {
        return null;
    }

}
