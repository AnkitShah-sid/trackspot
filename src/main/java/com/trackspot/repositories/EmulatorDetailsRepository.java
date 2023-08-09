package com.trackspot.repositories;

import com.trackspot.entities.EmulatorDetails;
import com.trackspot.entities.enums.ActivityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EmulatorDetailsRepository extends JpaRepository<EmulatorDetails, Long> {
    Optional<EmulatorDetails> findByEmulatorSsid(String emulatorSsid);
    Optional<List<EmulatorDetails>> findByUpdatedAtBefore(Date emulatorSsid);
    Optional<List<EmulatorDetails>> findByUser_Id(Long userId);
    int countByUser_Id(@Param("userId") Long userId);
    int countByUser_IdAndStatus(Long id, ActivityStatus activityStatus);
}
