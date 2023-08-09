package com.trackspot.controllers;

import com.sun.jdi.InternalException;
import com.trackspot.Helper.EmulatorHelper;
import com.trackspot.entities.EmulatorDetails;
import com.trackspot.entities.DragEmulatorRequest;
import com.trackspot.entities.enums.ActivityStatus;
import com.trackspot.entities.enums.TripStatus;
import com.trackspot.entities.trip.*;
import com.trackspot.services.emulator.EmulatorService;
import com.trackspot.services.trip.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping("/emulator")
@CrossOrigin(value = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class EmulatorDetailController {

    @Autowired
    private EmulatorService emulatorDetailService;
    @Autowired
    private TripService tripService;

    @GetMapping
    public ResponseEntity<?> getAllEmulators() {
        try {
            return ResponseEntity.ok(emulatorDetailService.getAllEmulators());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createEmulator(@RequestBody EmulatorDetails emulator) {
        try {
            Optional<EmulatorDetails> existingEmulatorOptional = emulatorDetailService.getEmulatorDetailsBySsid(emulator.getEmulatorSsid());
            if (existingEmulatorOptional.isPresent()) {
                EmulatorDetails existingEmulator = existingEmulatorOptional.get();
                existingEmulator.setEmulatorName(emulator.getEmulatorName());
                existingEmulator.setFcmToken(emulator.getFcmToken());
                existingEmulator.setLastUpdatedTripPointTime(new Timestamp(System.currentTimeMillis()));
                if (emulator.getLatitude() != null) existingEmulator.setLatitude(emulator.getLatitude());
                if (emulator.getLongitude() != null) existingEmulator.setLongitude(emulator.getLongitude());
                return ResponseEntity.ok(emulatorDetailService.saveOrUpdateEmulatorDetail(existingEmulator));
            } else {
                try {
                    emulator.setStatus(ActivityStatus.OFFLINE);
                    emulator.setTripStatus(TripStatus.PAUSED);
                    double centerLatitude = 37.773972;
                    double centerLongitude = -122.431297;
                    double radiusMiles = 5.0;
                    double[] randomCoordinates = EmulatorHelper.generateRandomCoordinates(centerLatitude, centerLongitude, radiusMiles);
                    System.out.println("Random Latitude: " + randomCoordinates[0]);
                    System.out.println("Random Longitude: " + randomCoordinates[1]);
                    emulator.setLatitude(randomCoordinates[0]);
                    emulator.setLongitude(randomCoordinates[1]);
                    EmulatorDetails createdEmulator = emulatorDetailService.saveOrUpdateEmulatorDetail(emulator);
                    return ResponseEntity.ok(createdEmulator);
                } catch (DataIntegrityViolationException ex) {
                    // Duplicate entry exception
                    System.out.println("Error : " + ex);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
                }
            }
        } catch (DataIntegrityViolationException ex) {
            System.out.println("Error : " + ex);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicate entry");
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateEmulator(@RequestBody EmulatorDetails emulator) {
        try {
            Optional<EmulatorDetails> existingEmulatorOptional = emulatorDetailService.getEmulatorDetailsBySsid(emulator.getEmulatorSsid());
            if (existingEmulatorOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Emulator not found");
            }
            EmulatorDetails existingEmulator = existingEmulatorOptional.get();
            emulator.setId(existingEmulator.getId());
            emulator.setUser(existingEmulator.getUser());
            return ResponseEntity.ok(emulatorDetailService.saveOrUpdateEmulatorDetail(emulator));
        } catch (DataIntegrityViolationException ex) {
            System.out.println("Error : " + ex);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicate entry");
        }
    }

    @GetMapping("/{emulatorId}")
    public ResponseEntity<?> getEmulatorDetail(@PathVariable("emulatorId") long emulatorId) throws Exception {
        Optional<EmulatorDetails> existingEmulatorOptional = emulatorDetailService.getEmulatorDetailById(emulatorId);
        if (existingEmulatorOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Emulator not found");
        }
        return ResponseEntity.ok(existingEmulatorOptional.get());
    }

    @PostMapping("/updateLatLng")
    public ResponseEntity<?> updateLatLng(@RequestBody EmulatorDetails emulator) {
        try {
            Optional<EmulatorDetails> existingEmulatorOptional = emulatorDetailService.getEmulatorDetailsBySsid(emulator.getEmulatorSsid());
            if (existingEmulatorOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Emulator not found");
            }
            EmulatorDetails existingEmulator = existingEmulatorOptional.get();
            existingEmulator.setLatitude(emulator.getLatitude());
            existingEmulator.setLongitude(emulator.getLongitude());
            existingEmulator.setStatus(ActivityStatus.ACTIVE);
            return ResponseEntity.ok(emulatorDetailService.saveOrUpdateEmulatorDetail(existingEmulator));
        } catch (DataIntegrityViolationException ex) {
            System.out.println("Error : " + ex);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicate entry");
        }
    }

    @PostMapping("/updatePhoneNumber")
    public ResponseEntity<?> updatePhoneNumber(@RequestBody EmulatorDetails emulator) {
        try {
            Optional<EmulatorDetails> existingEmulatorOptional = emulatorDetailService.getEmulatorDetailById(emulator.getId());
            if (existingEmulatorOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Emulator not found");
            }
            EmulatorDetails existingEmulator = existingEmulatorOptional.get();
            existingEmulator.setTelephone(emulator.getTelephone());
            return ResponseEntity.ok(emulatorDetailService.saveOrUpdateEmulatorDetail(existingEmulator));
        } catch (DataIntegrityViolationException ex) {
            System.out.println("Error : " + ex);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicate entry");
        }
    }

    @PostMapping("/updateNextTripPointIndex")
    public ResponseEntity<?> updateNextTripPointIndex(@RequestBody NextTripPointRequest nextTripPointRequest) {
        try {
            NextTripPointResult result = emulatorDetailService.updateNextTripPointIndex(nextTripPointRequest);
            return ResponseEntity.ok(result);
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex);
        } catch (IllegalArgumentException e) {
            System.out.println("updateNextTripPointIndex Error : " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        } catch (InternalException ex) {
            return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(ex);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    @GetMapping("/trip/{emulatorSsid}")
    public ResponseEntity<?> getTripPoints(@PathVariable("emulatorSsid") String emulatorId) {
        try {
            TripPointEmulatorResponse tripPointEmulatorResponse = tripService.getSyncedDatabaseOfEmulatorSsId(emulatorId);
            return ResponseEntity.ok(tripPointEmulatorResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    @GetMapping("/sync/{emulatorSsid}")
    public ResponseEntity<?> syncDatabaseWithEmulator(@PathVariable("emulatorSsid") String emulatorSsid) {
        try {
            TripPointEmulatorResponse tripPointEmulatorResponse = tripService.getSyncedDatabaseOfEmulatorSsId(emulatorSsid);
            return ResponseEntity.ok(tripPointEmulatorResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    @DeleteMapping("/{emulatorId}")
    public ResponseEntity<?> deleteEmulatorsByEmulatorId(@PathVariable("emulatorId") long emulatorId) {
        try {
            emulatorDetailService.deleteEmulatorsByEmulatorId(emulatorId);
            return ResponseEntity.ok("Successfully Deleted !");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteTrip/{emulatorId}")
    public ResponseEntity<?> deleteTripByEmulatorId(@PathVariable("emulatorId") long emulatorId) {
        try {
            emulatorDetailService.deleteTripByEmulatorId(emulatorId);
            return ResponseEntity.ok("Successfully Deleted !");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PostMapping("/dragEmulator")
    public ResponseEntity<?> dragEmulatorAndChangelatlng(@RequestBody DragEmulatorRequest dragEmulatorRequest) {
        try {
            return ResponseEntity.ok(emulatorDetailService.dragEmulatorAndChangelatlng(dragEmulatorRequest));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}