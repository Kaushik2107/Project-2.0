package com.kaushik.travelplan.service;

import com.kaushik.travelplan.entity.Hotel;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SERVICE 2: Hotel Scoring & Selection
 * Scoring: rating (40%) + budget-fit (30%) + type-bonus (30%)
 */
@Service
public class HotelScoringService {

    /**
     * Select the best hotel within budget using multi-criteria scoring.
     */
    public Hotel selectBestHotel(List<Hotel> hotels, int hotelBudget, int days) {
        double maxPerNight = (days > 0) ? (double) hotelBudget / days : hotelBudget;

        // Filter hotels that fit budget
        List<Hotel> affordable = hotels.stream()
                .filter(h -> h.getPricePerNight() <= maxPerNight)
                .collect(Collectors.toList());

        if (affordable.isEmpty()) {
            // Fallback: pick cheapest hotel even if over budget
            return hotels.stream()
                    .min(Comparator.comparingInt(Hotel::getPricePerNight))
                    .orElse(hotels.get(0));
        }

        // Score each affordable hotel
        double maxRating = affordable.stream().mapToDouble(Hotel::getRating).max().orElse(5.0);
        double maxPrice  = affordable.stream().mapToInt(Hotel::getPricePerNight).max().orElse(1);

        return affordable.stream()
                .max(Comparator.comparingDouble(h -> scoreHotel(h, maxRating, maxPrice, maxPerNight)))
                .orElse(affordable.get(0));
    }

    /**
     * Score a hotel on 3 criteria:
     * - Rating quality (40%)
     * - Budget utilization (30%) — prefer hotels that use budget well
     * - Star type bonus (30%)
     */
    public double scoreHotel(Hotel h, double maxRating, double maxPrice, double maxPerNight) {
        double ratingScore = (maxRating > 0) ? h.getRating() / maxRating : 0;
        
        // BUDGET UTILIZATION: we want to SPEND money. 
        // Higher cost (closer to maxPerNight) is BETTER now.
        double budgetUtilization = (maxPerNight > 0) ? (double)h.getPricePerNight() / maxPerNight : 0;
        
        double typeBonus = getTypeBonus(h.getType());
        
        // Crazy Strategy: 50% on utilization, 30% on rating, 20% on type
        return (budgetUtilization * 0.50) + (ratingScore * 0.30) + (typeBonus * 0.20);
    }

    private double getTypeBonus(String type) {
        if (type == null) return 0.3;
        switch (type.toLowerCase()) {
            case "5-star": return 1.0;
            case "4-star": return 0.8;
            case "3-star": return 0.6;
            case "2-star": return 0.4;
            case "1-star": return 0.2;
            default:       return 0.3;
        }
    }
}
