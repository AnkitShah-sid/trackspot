package com.trackspot.entities.trip;

import com.google.maps.model.LatLng;
import com.trackspot.entities.EmulatorDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TripPoint {
    private int tripPointIndex;
    private double lat;
    private double lng;
    private double bearing;
    private double distance;
}
