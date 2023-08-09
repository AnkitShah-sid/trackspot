package com.trackspot.entities.trip;

import lombok.Data;

@Data
public class NextTripPointRequest {
    private Long emulatorId;
    private Double latitude;
    private Double longitude;
    private Integer nextTripPointIndex;
    private String address;

    @Override
    public String toString() {
        return "NextTripPointRequest{" +
                "emulatorId=" + emulatorId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", nextTripPointIndex=" + nextTripPointIndex +
                ", address='" + address + '\'' +
                '}';
    }

    // Getters and setters
}
