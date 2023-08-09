package com.trackspot.services.emulator;

import com.trackspot.Helper.TripHelper;
import com.trackspot.entities.*;
import com.trackspot.entities.enums.ActivityStatus;
import com.trackspot.entities.enums.TripStatus;
import com.trackspot.entities.trip.NextTripPointRequest;
import com.trackspot.entities.trip.NextTripPointResult;
import com.trackspot.entities.trip.StopPoint;
import com.trackspot.entities.trip.TripDetailsEntity;
import com.trackspot.repositories.EmulatorDetailsRepository;
import com.trackspot.services.ClientService;
import com.trackspot.services.trip.TripDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class EmulatorDetailServiceImpl implements EmulatorService {
    private static final Logger logger = LoggerFactory.getLogger(EmulatorDetailServiceImpl.class);

    @Autowired
    private EmulatorDetailsRepository emulatorDetailsRepo;

    @Autowired
    private ClientService clientService;
    @Autowired
    private TripDetailsService tripDetailsService;

    @Override
    public List<EmulatorDetails> getAllEmulators() throws Exception {
        try {
            if (clientService.getCurrentClient() instanceof Admin) {
                return emulatorDetailsRepo.findAll();
            } else {
                User user = (User) clientService.getCurrentClient();
                if (user != null && user.getId() != null) {
                    Optional<List<EmulatorDetails>> userOptionalList = emulatorDetailsRepo.findByUser_Id(user.getId());
                    return userOptionalList.orElseGet(ArrayList::new);
                } else {
                    throw new IllegalArgumentException("User ID is not found in the token: ");
                }
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    @Override
    public Optional<EmulatorDetails> getEmulatorDetailsBySsid(String emulatorSsid) {
        return emulatorDetailsRepo.findByEmulatorSsid(emulatorSsid);
    }

    @Override
    public Optional<EmulatorDetails> findById(Long id) {
        return emulatorDetailsRepo.findById(id);
    }

    @Override
    public Optional<List<EmulatorDetails>> findByLastActivityTimestampBefore(Date thresholdTimestamp) {
        return emulatorDetailsRepo.findByUpdatedAtBefore(thresholdTimestamp);
    }

    @Override
    public EmulatorCount getActiveAndAllEmulatorsByUserId(Long id) {

        EmulatorCount emulatorCount = new EmulatorCount();
        emulatorCount.setActiveEmulatorsCount(emulatorDetailsRepo.countByUser_IdAndStatus(id, ActivityStatus.ACTIVE));
        emulatorCount.setAllEmulatorsCount(emulatorDetailsRepo.countByUser_Id(id));
        return emulatorCount;
    }

    @Override
    public NextTripPointResult updateNextTripPointIndex(NextTripPointRequest nextTripPointRequest) {
        int nextTripPointIndex = nextTripPointRequest.getNextTripPointIndex();
        Optional<EmulatorDetails> existingEmulatorOptional = getEmulatorDetailById(nextTripPointRequest.getEmulatorId());
        if (existingEmulatorOptional.isEmpty()) {
            throw new IllegalArgumentException("Emulator not found with ID : " + nextTripPointIndex);
        }
        EmulatorDetails existingEmulator = existingEmulatorOptional.get();
        logger.info("updateNextTripPointIndex SSID : " + existingEmulator.getEmulatorSsid() + " prev=>next point : " + existingEmulator.getCurrentTripPointIndex() + "=>" + nextTripPointIndex);
        try {
            Optional<TripDetailsEntity> tripDetailsEntityOptional = tripDetailsService.getTripDetailsFromEmulatorId(existingEmulator.getId());
            if (tripDetailsEntityOptional.isEmpty()) {
                throw new IllegalArgumentException("Trip Not Found!");
            }
            TripDetailsEntity tripDetailsEntity = tripDetailsEntityOptional.get();
            int diff = nextTripPointIndex - existingEmulator.getCurrentTripPointIndex();
            if (diff != 1 && diff != 0) { // 1 for next trip point. 0 for last point being sent again and again...
                throw new IllegalArgumentException("Skipped many trip points. Diff : " + diff + ", req : " + nextTripPointRequest + ", currentTripPointIndex : " + existingEmulator.getCurrentTripPointIndex());
            }
            if (tripDetailsEntity.getTripPoints().size() - 1 < nextTripPointIndex) {
                throw new IllegalArgumentException("Next Trip Point index exceeds total tripPoints");
            } else if (tripDetailsEntity.getTripPoints().size() - 1 == nextTripPointIndex) {
                existingEmulator.setTripStatus(TripStatus.FINISHED);
            }
            // Calculate time required to reach the next trip point based on speed and distance.
            double emulatorSpeed = existingEmulator.getSpeed();
            if (emulatorSpeed == 0) {
                throw new InternalError("Emulator Speed is set 0");
            }
            double nextTripDistance = tripDetailsEntity.getTripPoints().get(nextTripPointIndex).getDistance(); // Implement this method to calculate the distance.
            Timestamp requiredTime = TripHelper.calculateTimeToNextTripPoint(emulatorSpeed, nextTripDistance);
            Timestamp lastTripTime = existingEmulator.getLastUpdatedTripPointTime();
            // Unless the emulator is RESTING, we can continue to change current index to requested index.
            if (existingEmulator.getTripStatus() == TripStatus.RESTING) {
                Timestamp timeToRestTwelveHoursTs = new Timestamp(12 * 60 * 60 * 1000);
                if (TripHelper.isTimeElapsedSinceLastTripChange(lastTripTime, timeToRestTwelveHoursTs)) {
                    existingEmulator.setTripStatus(TripStatus.RUNNING);
                } else {
                    throw new IllegalArgumentException("Emulator Is Resting");
                }
            }
            if (TripHelper.isTimeElapsedSinceLastTripChange(lastTripTime, requiredTime)) {
                // The required time has elapsed, so you should set the next trip point now.
                existingEmulator.setLatitude(nextTripPointRequest.getLatitude());
                existingEmulator.setLongitude(nextTripPointRequest.getLongitude());
                existingEmulator.setAddress(nextTripPointRequest.getAddress());
                existingEmulator.setCurrentTripPointIndex(nextTripPointIndex);
                existingEmulator.setLastUpdatedTripPointTime(new Timestamp(System.currentTimeMillis()));
                for (StopPoint stop : tripDetailsEntity.getStops()) {
                    if (nextTripPointIndex == stop.getTripPointIndex()) {
                        existingEmulator.setTripStatus(TripStatus.RESTING);
                        break;
                    }
                }
                logger.info("Time is Elapsed Since Last Trip Change, prev/next point : " + nextTripPointIndex);
            } else {
                logger.info("Time is NOT Elapsed Since Last Trip Change" + lastTripTime + requiredTime);
                logger.info("lastTripTime : " + lastTripTime);
                logger.info("requiredTime : " + requiredTime);
            }
        } catch (IllegalArgumentException e) {
            logger.error("Error at updateNextTripPointIndex : " + e);
        } finally {
            // Handle Weird Errors somewhere
            if (existingEmulator.getTripStatus() != TripStatus.RUNNING) {
                existingEmulator.setStatus(ActivityStatus.INACTIVE);
            } else {
                existingEmulator.setStatus(ActivityStatus.ACTIVE);
            }
            saveOrUpdateEmulatorDetail(existingEmulator);
        }
        return new NextTripPointResult(existingEmulator, true, "HELLO");
    }


    @Override
    public Iterable<EmulatorDetails> listAllEmulatorDetails() {
        return emulatorDetailsRepo.findAll();
    }

    @Override
    public Optional<EmulatorDetails> getEmulatorDetailById(Long id) {
        return emulatorDetailsRepo.findById(id);
    }

    @Override
    public EmulatorDetails saveOrUpdateEmulatorDetail(EmulatorDetails emulatorDetail) {
        Optional<EmulatorDetails> optionalEmulatorDetails = emulatorDetailsRepo.findByEmulatorSsid(emulatorDetail.getEmulatorSsid());
        optionalEmulatorDetails.ifPresent(emulatorDetails -> emulatorDetail.setId(emulatorDetails.getId()));
        return emulatorDetailsRepo.save(emulatorDetail);
    }

    @Override
    public void deleteEmulatorsByEmulatorId(Long emulatorId) throws Exception {
        if (emulatorDetailsRepo.findById(emulatorId).isPresent()) {
            try {
                emulatorDetailsRepo.deleteById(emulatorId);
            } catch (Exception e) {
                logger.error("Could Not Delete Trip Details while deleting emulator : " + e);
                throw new Exception("Emulator could not be deleted : " + e.getMessage());
            }
            try {
                tripDetailsService.deleteTripDetailsByEmulatorId(emulatorId);
            } catch (Exception e) {
                logger.error("Could Not Delete Trip Details while deleting emulator : " + emulatorId);
            }
        } else {
            throw new IllegalArgumentException("Emulator Not Found");
        }
    }

    @Override
    public EmulatorDetails deleteTripByEmulatorId(Long emulatorId) {
        Optional<EmulatorDetails> existingEmulatorOptional = emulatorDetailsRepo.findById(emulatorId);
        if (existingEmulatorOptional.isEmpty()) {
            throw new IllegalArgumentException("Emulator not found");
        }
        EmulatorDetails existingEmulator = existingEmulatorOptional.get();
        tripDetailsService.deleteTripDetailsByEmulatorId(emulatorId);
        existingEmulator.resetTrip();

        return emulatorDetailsRepo.save(existingEmulator);
    }

    @Override
    public EmulatorDetails dragEmulatorAndChangelatlng(DragEmulatorRequest dragEmulatorRequest) {
        Optional<EmulatorDetails> existingEmulatorOptional = getEmulatorDetailById(dragEmulatorRequest.getEmulatorId());
        if (existingEmulatorOptional.isEmpty()) {
            throw new IllegalArgumentException("Emulator not found");
        }
        EmulatorDetails existingEmulator = existingEmulatorOptional.get();
        if (dragEmulatorRequest.getCancelTrip()) {
            existingEmulator = deleteTripByEmulatorId(dragEmulatorRequest.getEmulatorId());
        } else if (dragEmulatorRequest.getNewTripIndex() != null) {
            existingEmulator.setCurrentTripPointIndex(dragEmulatorRequest.getNewTripIndex());
        } else {
            Optional<TripDetailsEntity> trip = tripDetailsService.getTripDetailsFromEmulatorId(existingEmulator.getId());
            if (trip.isPresent()) {
                throw new RuntimeException("Emulator has a Trip!");
            }
        }
        existingEmulator.setLatitude(dragEmulatorRequest.getLatitude());
        existingEmulator.setLongitude(dragEmulatorRequest.getLongitude());
        return emulatorDetailsRepo.save(existingEmulator);
    }
}