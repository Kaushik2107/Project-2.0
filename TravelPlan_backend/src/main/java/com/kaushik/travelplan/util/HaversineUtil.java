package com.kaushik.travelplan.util;

/**
 * Utility class for calculating distances between geographic coordinates
 * using the Haversine formula (great-circle distance on a sphere).
 */
public class HaversineUtil {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private HaversineUtil() {}

    /**
     * Calculate the distance in kilometers between two lat/lng points.
     *
     * @param lat1 Latitude of point 1
     * @param lng1 Longitude of point 1
     * @param lat2 Latitude of point 2
     * @param lng2 Longitude of point 2
     * @return Distance in kilometers
     */
    public static double distanceKm(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calculate total route distance given a list of coordinates in order.
     *
     * @param latitudes  Array of latitudes
     * @param longitudes Array of longitudes
     * @return Total distance in km
     */
    public static double totalRouteDistance(double[] latitudes, double[] longitudes) {
        if (latitudes.length != longitudes.length || latitudes.length < 2) return 0;
        double total = 0;
        for (int i = 0; i < latitudes.length - 1; i++) {
            total += distanceKm(latitudes[i], longitudes[i], latitudes[i + 1], longitudes[i + 1]);
        }
        return total;
    }
}
