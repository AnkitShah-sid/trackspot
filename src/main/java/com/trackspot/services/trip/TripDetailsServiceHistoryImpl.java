package com.trackspot.services.trip;

import com.trackspot.entities.trip.Trip;
import com.trackspot.entities.trip.TripDetailsHistoryEntity;
import com.trackspot.repositories.TripDetailsHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class TripDetailsServiceHistoryImpl implements TripDetailsHistoryService {

    @Autowired
    private TripDetailsHistoryRepository tripDetailsHistoryRepository;

    @Override
    public TripDetailsHistoryEntity saveTripDetailsHistory(TripDetailsHistoryEntity tripDetailsHistoryEntity) throws Exception {
        try {
            return tripDetailsHistoryRepository.save(tripDetailsHistoryEntity);
        } catch (Exception e) {
            throw new Exception("Error : " + e);
        }
    }

    @Override
    public void deleteTripHistory(Trip trip) {

    }

    @Override
    public List<TripDetailsHistoryEntity> getAllTripDetailsHistory() {
        return null;
    }

    @Override
    public Optional<List<TripDetailsHistoryEntity>> getTripDetailsHistoryFromEmulatorId(long emulatorId) {
        return tripDetailsHistoryRepository.findByTripDetailsIdOrderByCreatedAtDesc(String.valueOf(emulatorId));
    }
}
