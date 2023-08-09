package com.trackspot.services.messaging;

public interface FcmService {
    void sendSimpleMessage(String to, boolean isCustomLocation,  boolean isNewTrip, double latitude, double longitude) throws Exception;

}
