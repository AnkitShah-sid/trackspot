package com.trackspot.entities;

import lombok.Data;

@Data
public class DragEmulatorRequest {

    private Long emulatorId;
    private Double latitude;
    private Double longitude;
    private Boolean cancelTrip;
    private Integer newTripIndex;
}
