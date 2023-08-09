package com.trackspot.entities;

import com.trackspot.entities.enums.ActivityStatus;
import com.trackspot.entities.enums.TripStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor(force = true)
public class EmulatorDetails extends Audit{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private Long id;

    @Column(nullable = false)
    @NonNull
    private String emulatorName;

    @Column(unique = true, nullable = false)
    @NonNull
    private String emulatorSsid;

    @Column(unique = true, nullable = false)
    @NonNull
    private String fcmToken;

    private Double latitude;

    private Double longitude;

    @NonNull
    private String telephone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NonNull
    private ActivityStatus status = ActivityStatus.OFFLINE;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="user_id")
    private User user;

    private String address = null;

    private Double startLat;
    private Double startLong;
    private Double endLat;
    private Double endLong;
    private int speed;

    @Column(nullable = false)
    @NonNull
    private Integer currentTripPointIndex = -1;

    private TripStatus tripStatus = TripStatus.STOP;

    private int tripTime = (int) System.currentTimeMillis();

    @Column(nullable = false)
    private Timestamp lastUpdatedTripPointTime = new Timestamp(System.currentTimeMillis());


    public void resetTrip(){
        currentTripPointIndex = -1;
        tripStatus = TripStatus.STOP;
        startLat = null;
        startLong = null;
        endLat = null;
        endLong = null;
        speed = 0;
    }
}

