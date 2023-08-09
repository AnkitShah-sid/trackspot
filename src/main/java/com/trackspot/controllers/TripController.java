package com.trackspot.controllers;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.trackspot.entities.EmulatorDetails;
import com.trackspot.entities.trip.*;
import com.trackspot.maps.GoogleMapsService;
import com.trackspot.services.trip.TripDetailsHistoryService;
import com.trackspot.services.trip.TripService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/trip")
@CrossOrigin(value = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class TripController {
    private static final Logger logger = LoggerFactory.getLogger(TripController.class);

    @Autowired
    private TripService tripService;
    @Autowired
    private TripDetailsHistoryService tripDetailsHistoryService;

    // WITH CUSTOM DESERIALIZER FOR PARSING case_insensitive ENUMS at AddressComponent -> AddressComponentType
    @PostMapping("/create")
    public ResponseEntity<?> createTrip(HttpServletRequest request) {
        try {
            ObjectMapper objectMapper = JsonMapper.builder()
                    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                    .build();
            Trip trip = objectMapper.readValue(request.getReader(), Trip.class);
            Trip createdTrip = tripService.createTrip(trip);
            return ResponseEntity.ok(createdTrip);
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException : " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error : " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error : " + e.getMessage());
        }
    }

    @GetMapping("/{emulatorId}")
    public ResponseEntity<?> getTripFromEmulatorId(@PathVariable("emulatorId") long emulatorId) throws Exception {
        try {
            Trip trip = tripService.getTripOfEmulatorId(emulatorId);
            return ResponseEntity.ok(trip.getTripDetails());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error : " + e.getMessage());
        }
    }

    @GetMapping("/trip-points/{emulatorId}")
    public ResponseEntity<?>  getTripPointFromEmulatorId(@PathVariable("emulatorId") long emulatorId) throws Exception {
        Trip trip = tripService.getTripOfEmulatorId(emulatorId);
        try {
            return ResponseEntity.ok(trip.getTripDetails().getTripPoints());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error : " + e.getMessage());
        }
    }

    @GetMapping("/stops/{emulatorId}")
    public ResponseEntity<?> getFullTripFromEmulatorId(@PathVariable("emulatorId") long emulatorId) throws Exception {
        Trip trip = tripService.getTripOfEmulatorId(emulatorId);
        try {
            return ResponseEntity.ok(trip.getTripDetails().getStops());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error : " + e.getMessage());
        }
    }

    @GetMapping("/toggle/{emulatorId}")
    public ResponseEntity<?> toggleTrip(@PathVariable("emulatorId") long emulatorId) throws Exception {
        try {
            return ResponseEntity.ok(tripService.toggleTripFromEmId(emulatorId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error : " + e.getMessage());
        }
    }

    @GetMapping("/history/{emulatorId}")
    public ResponseEntity<?> getEmulatorTripHistory(@PathVariable("emulatorId") long emulatorId) {
        Optional<List<TripDetailsHistoryEntity>> list = tripDetailsHistoryService.getTripDetailsHistoryFromEmulatorId(emulatorId);
        if (list.isPresent()) {
            return ResponseEntity.ok(list.get());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Can't Find History");
        }
    }
}
