
package com.trackspot.services.emulator;


import com.trackspot.entities.EmulatorCount;
import com.trackspot.entities.EmulatorDetails;
import com.trackspot.entities.DragEmulatorRequest;
import com.trackspot.entities.trip.NextTripPointRequest;
import com.trackspot.entities.trip.NextTripPointResult;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EmulatorService {

    Iterable<EmulatorDetails> listAllEmulatorDetails();

    Optional<EmulatorDetails> getEmulatorDetailById(Long id);

    EmulatorDetails saveOrUpdateEmulatorDetail(EmulatorDetails EmulatorDetail);

    void deleteEmulatorsByEmulatorId(Long id) throws Exception;

    List<EmulatorDetails> getAllEmulators() throws Exception;

    Optional<EmulatorDetails> getEmulatorDetailsBySsid(String emulatorSsid);
    Optional<EmulatorDetails> findById(Long emulatorSsid);

    Optional<List<EmulatorDetails>> findByLastActivityTimestampBefore(Date thresholdTimestamp);

    EmulatorCount getActiveAndAllEmulatorsByUserId(Long id);

    NextTripPointResult updateNextTripPointIndex(NextTripPointRequest nextTripPointRequest) throws Exception;


    EmulatorDetails deleteTripByEmulatorId(Long emulatorId);

    EmulatorDetails dragEmulatorAndChangelatlng(DragEmulatorRequest dragEmulatorRequest);


}