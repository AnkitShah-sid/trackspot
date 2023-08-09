package com.trackspot.scheduler;

import com.trackspot.entities.EmulatorDetails;
import com.trackspot.entities.enums.ActivityStatus;
import com.trackspot.services.emulator.EmulatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class UserStatusScheduler {

    @Autowired
    private EmulatorService emulatorService; // Replace with your actual UserRepository

    @Scheduled(fixedRate = 30000) // Run every half-minute, adjust the rate as per your requirements
    public void markUsersAsOffline() {
        int inactivityDurationSeconds = 30; // Define the duration of inactivity after which a user is considered offline
        Date thresholdTimestamp = new Date(System.currentTimeMillis() - inactivityDurationSeconds * 1000);
        Optional<List<EmulatorDetails>> inactiveUsers = emulatorService.findByLastActivityTimestampBefore(thresholdTimestamp);
        if (inactiveUsers.isPresent()) {
            for (EmulatorDetails emulatorDetails : inactiveUsers.get()) {
                if(!emulatorDetails.getStatus().equals(ActivityStatus.OFFLINE)) {
                    System.out.println("MAKING INACTIVE: " + emulatorDetails.getEmulatorName());
                    emulatorDetails.setStatus(ActivityStatus.OFFLINE); // Set the appropriate status field in your user entity
                    emulatorService.saveOrUpdateEmulatorDetail(emulatorDetails);
                }
            }
        }
    }
}