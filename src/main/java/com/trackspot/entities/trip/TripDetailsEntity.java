package com.trackspot.entities.trip;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "trip_details")
public class TripDetailsEntity {
    @Id
    private String id;
    private double velocity;
    private double distance;
    private AddressComponent[]  fromAddress;
    private AddressComponent[]  toAddress;
    private List<TripPoint> tripPoints;
    private List<StopPoint> stops;
}
