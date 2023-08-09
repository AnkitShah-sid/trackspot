package com.trackspot.Helper;

import java.sql.Timestamp;

public class TripHelper {
    // Helper method to calculate time in milliseconds required to reach the next trip point.
    public static Timestamp calculateTimeToNextTripPoint(double emulatorSpeed, double nextTripDistance) {
        // Convert emulatorSpeed to meters per second
        double speedInMps = emulatorSpeed * 0.44704; // 1 mph = 0.44704 m/s
        double nextTripDistanceInMeters = nextTripDistance * 1609.344;  // 1 mile = 1609.344 meters
        // Calculate time in seconds required to reach the next trip point
        double timeInSeconds = nextTripDistanceInMeters / speedInMps;
        // Convert time to milliseconds
        return new Timestamp(Math.toIntExact((long) (timeInSeconds * 1000)));
    }

    // Helper method to check if the required time has elapsed since the last trip point change.
    public static boolean isTimeElapsedSinceLastTripChange(Timestamp lastTripTime, Timestamp requiredTime) {
        long currentTime = System.currentTimeMillis();
        return currentTime - lastTripTime.getTime() >= requiredTime.getTime();
    }
}
