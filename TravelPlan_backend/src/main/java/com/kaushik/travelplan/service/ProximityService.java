package com.kaushik.travelplan.service;

import com.kaushik.travelplan.entity.Hotel;
import com.kaushik.travelplan.entity.Place;
import com.kaushik.travelplan.util.HaversineUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * FEATURE 12: Proximity-Based Itinerary Optimizer
 * Reorders places using Nearest-Neighbor heuristic to minimize total travel distance.
 */
@Service
public class ProximityService {

    /**
     * Reorder places by proximity using nearest-neighbor starting from hotel.
     * This is a TSP approximation that reduces total travel distance.
     */
    public List<Place> optimizeRouteOrder(Hotel hotel, List<Place> places) {
        if (places.size() <= 1) return new ArrayList<>(places);

        // Check if we have coordinates
        boolean hasCoordinates = hotel != null && (hotel.getLatitude() != 0 || hotel.getLongitude() != 0);
        for (Place p : places) {
            if (p.getLatitude() != 0 || p.getLongitude() != 0) {
                hasCoordinates = true;
                break;
            }
        }

        if (!hasCoordinates) return new ArrayList<>(places); // no coords, return as-is

        List<Place> remaining = new ArrayList<>(places);
        List<Place> ordered = new ArrayList<>();

        double currentLat = hotel != null ? hotel.getLatitude() : 0;
        double currentLng = hotel != null ? hotel.getLongitude() : 0;

        // Nearest-neighbor algorithm
        while (!remaining.isEmpty()) {
            Place nearest = null;
            double minDist = Double.MAX_VALUE;

            for (Place p : remaining) {
                double dist = HaversineUtil.distanceKm(currentLat, currentLng,
                        p.getLatitude(), p.getLongitude());
                if (dist < minDist) {
                    minDist = dist;
                    nearest = p;
                }
            }

            if (nearest != null) {
                ordered.add(nearest);
                currentLat = nearest.getLatitude();
                currentLng = nearest.getLongitude();
                remaining.remove(nearest);
            }
        }

        return ordered;
    }

    /**
     * Calculate distance savings from route optimization.
     */
    public double calculateSavings(Hotel hotel, List<Place> original, List<Place> optimized) {
        double originalDist = calculateRouteDistance(hotel, original);
        double optimizedDist = calculateRouteDistance(hotel, optimized);
        return originalDist - optimizedDist;
    }

    private double calculateRouteDistance(Hotel hotel, List<Place> places) {
        if (hotel == null || places.isEmpty()) return 0;

        double total = 0;
        double prevLat = hotel.getLatitude();
        double prevLng = hotel.getLongitude();

        for (Place p : places) {
            total += HaversineUtil.distanceKm(prevLat, prevLng, p.getLatitude(), p.getLongitude());
            prevLat = p.getLatitude();
            prevLng = p.getLongitude();
        }

        return total;
    }
}
