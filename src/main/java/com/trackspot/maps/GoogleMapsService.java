package com.trackspot.maps;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.PlacesApi;
import com.google.maps.model.*;
import com.trackspot.entities.trip.StopPoint;
import com.trackspot.entities.trip.TripDetailsEntity;
import com.trackspot.entities.trip.TripDetailsHistoryEntity;
import com.trackspot.entities.trip.TripPoint;
import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GoogleMapsService {
    private static final Logger logger = LoggerFactory.getLogger(GoogleMapsService.class);
    private final static String API_KEY = "AIzaSyAOJ2QPH1vPWF7wXqdHMGFR54Vzlb13M1E"; // Your Google Maps API key
    private final static double STOP_DISTANCE_THRESHOLD = 600;
    private final static int MAX_STOP_DISTANCE_METERS = 8046; // Your Google Maps API key

    public TripDetailsEntity calculateRoutePointsAndVelocity(double startLat,
                                                             double startLng,
                                                             double endLat,
                                                             double endLng,
                                                             int truckSpeed,
                                                             TripDetailsHistoryEntity historyEntity) throws Exception {


        try {
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey(API_KEY)
                    .build();

            LatLng origin = new LatLng(startLat, startLng);
            LatLng destination = new LatLng(endLat, endLng);

            // Perform directions request
            DirectionsResult directionsResult = DirectionsApi.newRequest(context)
                    .origin(origin)
                    .destination(destination)
                    .mode(TravelMode.DRIVING)
                    .units(Unit.IMPERIAL)
                    .departureTimeNow()
                    .await();

            // Extract the route and its points
            List<StopPoint> stopPoints = new ArrayList<>();
            List<TripPoint> routePoints = new ArrayList<>();
            List<TripPoint> routePointsHistory = new ArrayList<>();
            double totalTripDistance = 0;
            if (directionsResult.routes.length > 0) {
                DirectionsRoute tripRoute = directionsResult.routes[0];
                double prevLat = startLat;
                double prevLng = startLng;
                List<LatLng> decodePath = tripRoute.overviewPolyline.decodePath();
                double distanceToPrevStopPoint = 0;

                int tripPointIndex = 0;
                int allTripPointIndex = 0;
                int allTripPointIndexHistory = 0;
                while (tripPointIndex < decodePath.size()) {
                    LatLng point = decodePath.get(tripPointIndex);
                    double latitude = point.lat;
                    double longitude = point.lng;
                    double bearing = calculateBearing(prevLat, prevLng, latitude, longitude);
                    double distanceToPrevTripPoint = calculateDistance(prevLat, prevLng, latitude, longitude);
                    double newDistanceToPrevTripPoint = distanceToPrevTripPoint;
                    // Calculate the number of additional trip points required to keep the distance under 1 mile
                    int additionalTripPointsCount = (int) Math.ceil(distanceToPrevTripPoint);

                    // If the distance exceeds 1 mile, add additional trip points
                    if (additionalTripPointsCount > 0) {
                        double latStep = (latitude - prevLat) / (additionalTripPointsCount + 1);
                        double lngStep = (longitude - prevLng) / (additionalTripPointsCount + 1);

                        for (int i = 1; i <= additionalTripPointsCount; i++) {
                            double additionalLat = prevLat + latStep * i;
                            double additionalLng = prevLng + lngStep * i;
                            double additionalBearing = calculateBearing(prevLat, prevLng, additionalLat, additionalLng);
                            TripPoint additionalTripPoint = new TripPoint(allTripPointIndex++, additionalLat, additionalLng, additionalBearing, 1.0); // Assuming 1 mile distance for additional points
                            newDistanceToPrevTripPoint -= 1.0;
                            routePoints.add(additionalTripPoint);
                        }
                    }

                    TripPoint currentTripPoint = new TripPoint(allTripPointIndex++, latitude, longitude, bearing, newDistanceToPrevTripPoint);
                    TripPoint currentTripPointHistory = new TripPoint(allTripPointIndexHistory++, latitude, longitude, bearing, newDistanceToPrevTripPoint);
                    routePoints.add(currentTripPoint);
                    routePointsHistory.add(currentTripPointHistory);
                    prevLat = latitude;
                    prevLng = longitude;
                    //STOP calculation
                    distanceToPrevStopPoint += distanceToPrevTripPoint;
                    totalTripDistance += distanceToPrevTripPoint;
                    if (distanceToPrevStopPoint >= STOP_DISTANCE_THRESHOLD) {
                        StopPoint currentStop = searchAndCalculateStopRoute(currentTripPoint, allTripPointIndex, distanceToPrevStopPoint, context);
                        stopPoints.add(currentStop);
                        distanceToPrevStopPoint = 0;
                    }
                    tripPointIndex++;
                }
            }
            TripDetailsEntity tripDetails = new TripDetailsEntity("", truckSpeed, totalTripDistance, null, null, routePoints, stopPoints);
            historyEntity.setVelocity(truckSpeed);
            historyEntity.setDistance(totalTripDistance);
            historyEntity.setTripPoints(routePointsHistory);
            historyEntity.setStops(stopPoints);
            return tripDetails;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private StopPoint searchAndCalculateStopRoute(TripPoint currentTripPointAsStop, int tripPointIndex, double distanceToStopPoint, GeoApiContext context) throws Exception {
        logger.info("Searching and Calculating Stop Route...");
        //Get Stop's AddressComponent
        GeocodingResult[] stopAddressResults = GeocodingApi.newRequest(context)
                .latlng(new com.google.maps.model.LatLng(currentTripPointAsStop.getLat(), currentTripPointAsStop.getLng()))
                .await();
        if (stopAddressResults.length == 0) {
            throw new Exception("Unable to find an address for trip point as stop");
        }
        AddressComponent[] stopAddressComponentsByGoogle = stopAddressResults[0].addressComponents;
        com.trackspot.entities.trip.AddressComponent[] stopAddress =
                Arrays.stream(stopAddressComponentsByGoogle).map(com.trackspot.entities.trip.AddressComponent::new)
                        .toArray(com.trackspot.entities.trip.AddressComponent[]::new);
        logger.info("Searching for the nearest gas station for stop #" + currentTripPointAsStop.getTripPointIndex());
        logger.info("Also the index set is, tripPointIndex #" + tripPointIndex);
        // Search for the nearest gas station
        LatLng origin = new LatLng(currentTripPointAsStop.getLat(), currentTripPointAsStop.getLng());
        PlacesSearchResult[] gasStationSearchResults = null;
        int distanceMultiplier = 1;
        int retries = 0;
        // Search for the nearest gas station
        while ((gasStationSearchResults == null || gasStationSearchResults.length == 0) && retries < 5) {
            gasStationSearchResults = PlacesApi.nearbySearchQuery(context, origin)
                    .type(PlaceType.GAS_STATION)
                    .radius(MAX_STOP_DISTANCE_METERS * distanceMultiplier)
                    .await()
                    .results;
            distanceMultiplier++;
            retries++;
        }
        if (gasStationSearchResults == null) {
            throw new Exception("No Gas Station Found");
        }
        if (gasStationSearchResults.length > 0) {
            int resultIndex = 0;
            PlaceDetails gasStationDetails = null;
            // Some places do not match the JSON format of placeDetails, so iterating all gas stations till we get the proper gasStationDetails object.
            while (gasStationDetails == null && resultIndex < gasStationSearchResults.length) {
                String placeId = gasStationSearchResults[resultIndex].placeId;
                try {
                    gasStationDetails = getPlaceDetailsWithPlaceId(context, placeId);
                } catch (Exception e) {
                    resultIndex++;
                    System.out.println("Got Error while fetching place details. Retrying " + resultIndex + "/" + gasStationSearchResults.length + " \n Error thrown was : " + e);
                }
            }
            if (gasStationDetails == null) {
                throw new Exception("Gas Station Details Not Found");
            }
            LatLng destination = gasStationDetails.geometry.location;
            //create the gasStationAddressComponent array
            com.trackspot.entities.trip.AddressComponent[] gasStationAddressComponent = Arrays.stream(gasStationDetails.addressComponents)
                    .map(com.trackspot.entities.trip.AddressComponent::new)
                    .toArray(com.trackspot.entities.trip.AddressComponent[]::new);

            // Calculate the route from the origin to the nearest gas station or truck resting area
            DirectionsResult directionsResult = DirectionsApi.newRequest(context)
                    .origin(origin)
                    .destination(destination)
                    .mode(TravelMode.DRIVING)
                    .await();

            if (directionsResult.routes.length > 0 && directionsResult.routes[0].legs.length > 0) {
                List<TripPoint> stopRouteTripPoints = new ArrayList<>();
                DirectionsRoute routeToGasStation = directionsResult.routes[0];
                double prevLat = currentTripPointAsStop.getLat();
                double prevLng = currentTripPointAsStop.getLng();
                List<LatLng> decodePathToGasStation = routeToGasStation.overviewPolyline.decodePath();
                for (int gasTripPointIndex = 0; gasTripPointIndex < decodePathToGasStation.size(); gasTripPointIndex++) {
                    LatLng point = decodePathToGasStation.get(gasTripPointIndex);
                    double latitude = point.lat;
                    double longitude = point.lng;
                    double bearing = calculateBearing(prevLat, prevLng, latitude, longitude);
                    double distance = calculateDistance(prevLat, prevLng, latitude, longitude);
                    stopRouteTripPoints.add(new TripPoint(gasTripPointIndex, latitude, longitude, bearing, distance));
                    prevLat = latitude;
                    prevLng = longitude;
                }
                return new StopPoint(
                        tripPointIndex,
                        currentTripPointAsStop.getLat(),
                        currentTripPointAsStop.getLng(),
                        currentTripPointAsStop.getBearing(),
                        distanceToStopPoint,
                        stopAddress,
                        gasStationAddressComponent,
                        stopRouteTripPoints
                );
            }
        }

        throw new Exception("Could Not Create Stop Point...");
    }

    private PlaceDetails getPlaceDetailsWithPlaceId(GeoApiContext context, String placeId) throws Exception {
        try {
            return PlacesApi.placeDetails(context, placeId).await();
        } catch (Exception e) {
            throw new Exception("Error Getting place details : " + e);
        }
    }


    private double calculateBearing(double startLat, double startLng, double endLat, double endLng) {
        double startLatRad = Math.toRadians(startLat);
        double startLngRad = Math.toRadians(startLng);
        double endLatRad = Math.toRadians(endLat);
        double endLngRad = Math.toRadians(endLng);

        double y = Math.sin(endLngRad - startLngRad) * Math.cos(endLatRad);
        double x = Math.cos(startLatRad) * Math.sin(endLatRad) -
                Math.sin(startLatRad) * Math.cos(endLatRad) * Math.cos(endLngRad - startLngRad);

        double bearingRad = Math.atan2(y, x);
        double bearingDeg = Math.toDegrees(bearingRad);

        return (bearingDeg + 360) % 360;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        GeodesicData geodesicData = Geodesic.WGS84.Inverse(lat1, lon1, lat2, lon2);
        return geodesicData.s12 / 1609.34; // Convert meters to miles
    }
}
