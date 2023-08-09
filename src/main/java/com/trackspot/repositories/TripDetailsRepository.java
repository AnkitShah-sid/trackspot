package com.trackspot.repositories;

import com.trackspot.entities.trip.TripDetailsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TripDetailsRepository extends MongoRepository<TripDetailsEntity, String> {
    // Custom query methods or additional operations can be defined here
}