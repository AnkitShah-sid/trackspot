package com.trackspot.controllers;

import com.trackspot.entities.EmulatorDetails;
import com.trackspot.entities.UserEmulatorAssignment;
import com.trackspot.services.UserEmulatorAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user-assign-emulator")
@CrossOrigin(value = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class UserAssignEmulatorController {

    @Autowired
    private UserEmulatorAssignmentService userEmulatorAssignmentService;

    @PostMapping
    public ResponseEntity<?> saveUserEmulatorAssignment(@RequestBody UserEmulatorAssignment userEmulatorAssignment) {
        try {
            EmulatorDetails savedUserEmulatorAssignment = userEmulatorAssignmentService.saveUserEmulatorAssignment(userEmulatorAssignment);
            return ResponseEntity.ok(savedUserEmulatorAssignment);
        } catch (Exception e) {
            System.out.println("Error : "+e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> updateUserEmulatorAssignment(@PathVariable("userId") Long userId, @RequestBody UserEmulatorAssignment user) {
        try {
            EmulatorDetails updatedEmulatorDetails = userEmulatorAssignmentService.updateUserEmulatorAssignment(userId, user);
            return ResponseEntity.ok(updatedEmulatorDetails);
        } catch (Exception e) {
            System.out.println("Error : " + e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getEmulatorsByUserId(@PathVariable Long userId) {
        try {
            Optional<List<EmulatorDetails>> emulatorsOptional = userEmulatorAssignmentService.getEmulatorsByUserId(userId);
            return ResponseEntity.ok(emulatorsOptional.get());
        } catch (Exception e) {
            System.out.println("Error : " + e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{emulatorId}")
    public ResponseEntity<String> releaseEmulator(@PathVariable("emulatorId") Long emulatorId) {
        try {
            userEmulatorAssignmentService.releaseEmulator(emulatorId);
            return ResponseEntity.ok("Emulator released successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update emulator: " + e.getMessage());
        }
    }
}

