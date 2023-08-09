package com.trackspot.entities.trip;

import com.trackspot.entities.EmulatorDetails;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NextTripPointResult {
    private EmulatorDetails emulatorDetails;
    private boolean success;
    private String message;

}