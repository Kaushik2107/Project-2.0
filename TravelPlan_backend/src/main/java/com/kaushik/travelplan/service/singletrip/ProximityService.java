package com.kaushik.travelplan.service.singletrip;

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
        if (places == null || places.size() <= 1) return (places == null) ? new ArrayList<>() : new ArrayList<>(places);

        // Check if we have any coordinates at all
        boolean anyCoords = (hotel != null && hotel.getLatitude() != 0 && hotel.getLongitude() != 0);
        if (!anyCoords) {
            for (Place p : places) {
                if (p.getLatitude() != 0 && p.getLongitude() != 0) {
                    anyCoords = true;
                    break;
                }
            }
        }

        if (!anyCoords) return new ArrayList<>(places); // No orientation possible

        List<Place> remaining = new ArrayList<>(places);
        List<Place> ordered = new ArrayList<>();

        double currentLat;
        double currentLng;

        // Start from hotel if possible, otherwise start from the first place that has coordinates
        if (hotel != null && hotel.getLatitude() != 0 && hotel.getLongitude() != 0) {
            currentLat = hotel.getLatitude();
            currentLng = hotel.getLongitude();
        } else {
            Place firstWithCoords = null;
            for (Place p : remaining) {
                if (p.getLatitude() != 0 && p.getLongitude() != 0) {
                    firstWithCoords = p;
                    break;
                }
            }
            if (firstWithCoords != null) {
                currentLat = firstWithCoords.getLatitude();
                currentLng = firstWithCoords.getLongitude();
                ordered.add(firstWithCoords);
                remaining.remove(firstWithCoords);
            } else {
                return new ArrayList<>(places); // Fallback
            }
        }

        // Nearest-neighbor algorithm
        while (!remaining.isEmpty()) {
            Place nearest = null;
            double minDist = Double.MAX_VALUE;

            for (Place p : remaining) {
                // If place has no coordinates, treat it as very far or handle at the end
                if (p.getLatitude() == 0 && p.getLongitude() == 0) continue;

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
            } else {
                // All remaining places have no coordinates
                ordered.addAll(remaining);
                break;
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

