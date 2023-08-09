package com.trackspot.Helper;

import java.util.Random;
public class EmulatorHelper {
        public static double[] generateRandomCoordinates(double centerLat, double centerLong, double radiusMiles) {
            double radiusDegrees = radiusMiles / 69.0;

            Random random = new Random();
            double randomRadius = random.nextDouble() * radiusDegrees;
            double randomAngle = random.nextDouble() * 2 * Math.PI;

            double deltaLat = randomRadius * Math.cos(randomAngle);
            double deltaLong = randomRadius * Math.sin(randomAngle);

            double newLat = centerLat + deltaLat;
            double newLong = centerLong + deltaLong;

            return new double[]{newLat, newLong};
        }
    }
