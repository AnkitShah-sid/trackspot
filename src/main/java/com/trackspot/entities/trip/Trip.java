package com.trackspot.entities.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trip {
    private Double startLat;
    private Double startLong;
    private Double endLat;
    private Double endLong;
    private AddressComponent[] fromAddress;
    private AddressComponent[] toAddress;
    private int speed;
    private Long emulatorDetailsId;
    private TripDetailsEntity tripDetails;
}