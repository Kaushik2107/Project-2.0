package com.kaushik.travelplan.service;

import com.kaushik.travelplan.entity.Hotel;
import com.kaushik.travelplan.entity.Place;
import com.kaushik.travelplan.entity.Transport;
import com.kaushik.travelplan.repository.TransportRepository;
import com.kaushik.travelplan.util.HaversineUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * FEATURE 8: Smart Transport Routing Service
 * Calculates transport cost using Haversine distance.
 * Selects cheapest mode per trip considering group size.
 */
@Service
public class TransportService {

    @Autowired
    private TransportRepository transportRepo;

    private static final int DEFAULT_PRICE_PER_KM = 15; // fallback ₹/km

    /**
     * Calculate total distance for the trip route: Hotel → Place1 → Place2 → ... → Hotel.
     */
    public double calculateTotalDistance(Hotel hotel, List<Place> places) {
        if (hotel == null || places.isEmpty()) return 0;

        // Check if coordinates are available
        if (hotel.getLatitude() == 0 && hotel.getLongitude() == 0) {
            // Fallback: estimate 5km per place visit
            return places.size() * 5.0;
        }

        double totalDistance = 0;
        double prevLat = hotel.getLatitude();
        double prevLng = hotel.getLongitude();

        for (Place p : places) {
            if (p.getLatitude() != 0 || p.getLongitude() != 0) {
                totalDistance += HaversineUtil.distanceKm(prevLat, prevLng, p.getLatitude(), p.getLongitude());
                prevLat = p.getLatitude();
                prevLng = p.getLongitude();
            } else {
                totalDistance += 5.0; // fallback per place
            }
        }

        // Return to hotel
        if (hotel.getLatitude() != 0 || hotel.getLongitude() != 0) {
            totalDistance += HaversineUtil.distanceKm(prevLat, prevLng, hotel.getLatitude(), hotel.getLongitude());
        }

        return totalDistance;
    }

    /**
     * Select cheapest transport mode for the group and calculate total cost.
     *
     * @return Map with keys: "mode", "totalCost", "costPerPerson"
     */
    public Map<String, Object> calculateTransportCost(String city, double totalDistanceKm,
                                                        int days, int travelers) {
        List<Transport> transports = transportRepo.findByCityIgnoreCaseAndAvailable(city, true);

        Map<String, Object> result = new HashMap<>();

        if (transports.isEmpty()) {
            // Fallback: default cab pricing
            int totalCost = (int)(totalDistanceKm * DEFAULT_PRICE_PER_KM * days);
            // For groups: share the cab cost
            int vehiclesNeeded = (int) Math.ceil((double) travelers / 4);
            totalCost = totalCost * vehiclesNeeded;

            result.put("mode", "cab (estimated)");
            result.put("totalCost", totalCost);
            result.put("costPerPerson", totalCost / travelers);
            return result;
        }

        // Find cheapest option considering group size
        String bestMode = "cab";
        int cheapestCost = Integer.MAX_VALUE;

        for (Transport t : transports) {
            int vehiclesNeeded = (int) Math.ceil((double) travelers / t.getCapacity());
            int costForMode = (int)((t.getBaseFare() + t.getPricePerKm() * totalDistanceKm)
                              * vehiclesNeeded * days);

            if (costForMode < cheapestCost) {
                cheapestCost = costForMode;
                bestMode = t.getMode();
            }
        }

        result.put("mode", bestMode);
        result.put("totalCost", cheapestCost);
        result.put("costPerPerson", cheapestCost / travelers);
        return result;
    }

    public List<Transport> getAllByCity(String city) {
        return transportRepo.findByCityIgnoreCase(city);
    }
}
