package com.kaushik.travelplan.service.singletrip;

import com.kaushik.travelplan.dto.BudgetBreakdown;
import com.kaushik.travelplan.util.SeasonUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * SERVICE 1: Budget Distribution & Food Cost Calculation
 * Handles the 40/20/10/30 split and seasonal price adjustments.
 */
@Service
public class BudgetService {

    private static final double HOTEL_RATIO  = 0.40;
    private static final double FOOD_RATIO   = 0.20;
    private static final double TRAVEL_RATIO = 0.10;
    private static final double PLACES_RATIO = 0.30;

    /**
     * Calculate food cost per day based on food type selection.
     */
    public int getFoodCostPerDay(String foodType, int manualFoodBudget) {
        if (foodType == null) return 400;
        switch (foodType.toLowerCase()) {
            case "budget":  return 200;
            case "premium": return 800;
            case "luxury":  return 1500;
            case "manual":  return Math.max(50, manualFoodBudget);
            case "standard":
            default:        return 400;
        }
    }

    /**
     * Distribute budget into hotel/food/travel/places with seasonal adjustment.
     */
    public BudgetBreakdown distribute(int budget, int days, String foodType, String travelDate) {
        double multiplier = getSeasonMultiplier(travelDate);

        BudgetBreakdown bb = new BudgetBreakdown();
        // Adjust budget distribution slightly based on season if needed, or apply it to the final costs
        // Here we use it to calculate the 'target' budgets for each category.
        bb.setHotelBudget((int)(budget * HOTEL_RATIO / multiplier));
        bb.setFoodBudget((int)(budget * FOOD_RATIO));
        bb.setTravelBudget((int)(budget * TRAVEL_RATIO));
        bb.setPlacesBudget((int)(budget * PLACES_RATIO));

        return bb;
    }

    /**
     * Get seasonal price multiplier (PEAK=1.3, MODERATE=1.0, OFF_PEAK=0.8).
     */
    public double getSeasonMultiplier(String travelDate) {
        if (travelDate == null || travelDate.isEmpty()) return 1.0;
        try {
            LocalDate date = LocalDate.parse(travelDate);
            return SeasonUtil.getPriceMultiplier(date);
        } catch (Exception e) {
            return 1.0;
        }
    }

    /**
     * Get human-readable season label.
     */
    public String getSeasonLabel(String travelDate) {
        if (travelDate == null || travelDate.isEmpty()) return "📅 No travel date specified";
        try {
            LocalDate date = LocalDate.parse(travelDate);
            return SeasonUtil.getSeasonLabel(date);
        } catch (Exception e) {
            return "📅 Invalid date format";
        }
    }

    public double getHotelRatio()  { return HOTEL_RATIO; }
    public double getFoodRatio()   { return FOOD_RATIO; }
    public double getTravelRatio() { return TRAVEL_RATIO; }
    public double getPlacesRatio() { return PLACES_RATIO; }
}

