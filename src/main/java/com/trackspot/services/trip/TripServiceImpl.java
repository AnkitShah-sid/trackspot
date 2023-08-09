package com.trackspot.services.trip;

import com.trackspot.entities.EmulatorDetails;
import com.trackspot.entities.enums.TripStatus;
import com.trackspot.entities.trip.*;
import com.trackspot.repositories.EmulatorDetailsRepository;
import com.trackspot.services.emulator.EmulatorService;
import com.trackspot.services.messaging.FcmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
public class TripServiceImpl implements TripService {

    private static final Logger logger = LoggerFactory.getLogger(TripServiceImpl.class);

    @Autowired
    private EmulatorDetailsRepository emulatorDetailsRepo;
    @Autowired
    private EmulatorService emulatorService;
    @Autowired
    private FcmService fcmService;

    @Autowired
    private TripDetailsService tripDetailsService;


    @Override
    public Trip createTrip(Trip trip) throws Exception {

        if (trip.getEmulatorDetailsId() == null || emulatorDetailsRepo.findById(trip.getEmulatorDetailsId()).isEmpty()) {
            throw new IllegalArgumentException("Emulator Details not present");
        }
        if (emulatorDetailsRepo.findById(trip.getEmulatorDetailsId()).get().getUser() == null) {
            throw new IllegalArgumentException("Emulator Not yet assigned to any user");
        }

        EmulatorDetails existingEmulatorDetails = emulatorDetailsRepo.findById(trip.getEmulatorDetailsId()).orElse(null);
        if (existingEmulatorDetails == null) {
            throw new Exception("Emulator Details Not found");
        } else {
            logger.info("Saving Trip...");
            existingEmulatorDetails.setSpeed(trip.getSpeed());
            existingEmulatorDetails.setStartLat(trip.getStartLat());
            existingEmulatorDetails.setStartLong(trip.getStartLong());
            existingEmulatorDetails.setEndLat(trip.getEndLat());
            existingEmulatorDetails.setEndLong(trip.getEndLong());
            existingEmulatorDetails.setCurrentTripPointIndex(-1);
            existingEmulatorDetails.setTripStatus(TripStatus.RUNNING);
            TripDetailsEntity entity = tripDetailsService.saveTripDetails(existingEmulatorDetails, trip.getFromAddress(), trip.getToAddress());
            if (entity == null) {
                logger.info("Error creating Trip details");
                throw new Exception("Error creating Trip details");
            }
            trip.setTripDetails(entity);
            EmulatorDetails savedEmulator = emulatorDetailsRepo.save(existingEmulatorDetails);
            logger.info("saved Emulator's new Trip Info");
            trip.setEmulatorDetailsId(savedEmulator.getId());
            try {
                String to = savedEmulator.getFcmToken();
                boolean isCustomLocation = false;
                boolean isNewTrip = false;
                double latitude = trip.getStartLat();
                double longitude = trip.getStartLong();
                logger.info("Sending fcm message to emulator");
                fcmService.sendSimpleMessage(to, isCustomLocation, isNewTrip, latitude, longitude);
            } catch (Exception e) {
                logger.error("ERROR AT sendSimpleMessage : " + e.getMessage());
            }
            return trip;
        }
    }

    @Override
    public Trip getTripOfEmulatorId(long emulatorId) throws Exception {
        try {
            EmulatorDetails existingEmulatorDetails = emulatorDetailsRepo.findById(emulatorId).orElse(null);
            if (existingEmulatorDetails == null) {
                throw new Exception("Emulator Details Not found");
            } else {
                TripDetailsEntity tripDetails = tripDetailsService.getTripDetailsFromEmulatorId(emulatorId).orElse(null);
                if (tripDetails == null) {
                    throw new Exception("Emulator Does not have a trip route!!");
                } else {
                    return new Trip(existingEmulatorDetails.getStartLat(),
                            existingEmulatorDetails.getStartLong(),
                            existingEmulatorDetails.getEndLat(),
                            existingEmulatorDetails.getEndLong(),
                            tripDetails.getFromAddress(),
                            tripDetails.getToAddress(),
                            existingEmulatorDetails.getSpeed(),
                            existingEmulatorDetails.getId(),
                            tripDetails
                    );
                }
            }
        } catch (Exception e) {
            throw new Exception("Error : " + e);
        }
    }

    @Override
    public TripPointEmulatorResponse getSyncedDatabaseOfEmulatorId(long emulatorId) throws Exception {
        try {
            EmulatorDetails existingEmulatorDetails = emulatorDetailsRepo.findById(emulatorId).orElse(null);
            if (existingEmulatorDetails == null) {
                throw new Exception("Emulator Details Not found");
            }
            TripDetailsEntity tripDetails = tripDetailsService.getTripDetailsFromEmulatorId(emulatorId).orElse(null);
            if (tripDetails == null) {
                throw new Exception("Emulator Does not have a trip route!!");
            }

            return new TripPointEmulatorResponse(
                    tripDetails.getTripPoints(),
                    tripDetails.getStops(),
                    existingEmulatorDetails);
        } catch (Exception e) {
            throw new Exception("Error : " + e);
        }
    }

    @Override
    public TripPointEmulatorResponse getSyncedDatabaseOfEmulatorSsId(String emulatorId) throws Exception {
        try {
            List<TripPoint> tripPoints = null;
            List<StopPoint> stopPoints = null;
            EmulatorDetails existingEmulatorDetails = emulatorDetailsRepo.findByEmulatorSsid(emulatorId).orElse(null);
            if (existingEmulatorDetails == null) {
                throw new Exception("Emulator Details Not found");
            }
            TripDetailsEntity tripDetails = tripDetailsService.getTripDetailsFromEmulatorId(existingEmulatorDetails.getId()).orElse(null);
            if (tripDetails != null) {
                tripPoints = tripDetails.getTripPoints();
                stopPoints = tripDetails.getStops();
            }
            return new TripPointEmulatorResponse(
                    tripPoints,
                    stopPoints,
                    existingEmulatorDetails);
        } catch (Exception e) {
            throw new Exception("Error : " + e);
        }
    }

    @Override
    public EmulatorDetails toggleTripFromEmId(long emulatorId) throws Exception {
        try {
            EmulatorDetails existingEmulatorDetails = emulatorDetailsRepo.findById(emulatorId).orElse(null);
            if (existingEmulatorDetails == null) {
                throw new Exception("Emulator Details Not found");
            }
            if (existingEmulatorDetails.getTripStatus() == null) {
                existingEmulatorDetails.setTripStatus(TripStatus.PAUSED);
            } else if (existingEmulatorDetails.getTripStatus() == TripStatus.RUNNING) {
                existingEmulatorDetails.setTripStatus(TripStatus.PAUSED);
            } else {
                existingEmulatorDetails.setTripStatus(TripStatus.RUNNING);
            }
            try {
                String to = existingEmulatorDetails.getFcmToken();
                boolean isCustomLocation = false;
                boolean isNewTrip = false;
                double latitude = existingEmulatorDetails.getLatitude();
                double longitude = existingEmulatorDetails.getLongitude();
                fcmService.sendSimpleMessage(to, isCustomLocation, isNewTrip, latitude, longitude);
            } catch (Exception e) {
                System.out.println("ERROR at sendSimpleMessage : " + e);
            }
            return emulatorService.saveOrUpdateEmulatorDetail(existingEmulatorDetails);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    @Override
    public void deleteTrip(Long id) {

    }

    @Override
    public List<EmulatorDetails> getAllTrips() {
        return null;
    }

}
