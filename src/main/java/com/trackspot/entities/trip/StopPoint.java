package com.trackspot.entities.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class StopPoint {
    private int tripPointIndex;
    private double lat;
    private double lng;
    private double bearing;
    private double distance;
    private AddressComponent[] address;
    private AddressComponent[] gasStation;
    private List<TripPoint> tripPoints;
}
