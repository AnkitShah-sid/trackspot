package com.trackspot.repositories;

import com.trackspot.entities.trip.TripDetailsHistoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TripDetailsHistoryRepository extends MongoRepository<TripDetailsHistoryEntity, String> {
    // Custom query methods or additional operations can be defined here
    Optional<List<TripDetailsHistoryEntity>> findByTripDetailsIdOrderByCreatedAtDesc(String tripDetailsId);
}