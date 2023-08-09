package com.trackspot.entities.trip;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "trip_details_history")
public class TripDetailsHistoryEntity {
    @Id
    private String id;
    private String tripDetailsId; // Reference to the original trip details which itself it Emulator Id...
    private double velocity;
    private double distance;
    private AddressComponent[] fromAddress;
    private AddressComponent[] toAddress;
    private List<TripPoint> tripPoints;
    private List<StopPoint> stops;
    private LocalDateTime createdAt = LocalDateTime.now(); // To store the timestamp when the historical record was saved

    public TripDetailsHistoryEntity(TripDetailsEntity tripDetails) {
        // Copy properties from the TripDetailsEntity
        this.velocity = tripDetails.getVelocity();
        this.distance = tripDetails.getDistance();
        this.fromAddress = tripDetails.getFromAddress();
        this.toAddress = tripDetails.getToAddress();
        this.tripPoints = tripDetails.getTripPoints();
        this.stops = tripDetails.getStops();
        this.tripDetailsId = tripDetails.getId();
        this.createdAt = LocalDateTime.now();
    }
}
