package com.trackspot.entities.trip;

import com.trackspot.entities.EmulatorDetails;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class TripPointEmulatorResponse {
    private List<TripPoint> tripPoints;
    private List<StopPoint> stopPoints;
    private EmulatorDetails emulatorDetails;
}
