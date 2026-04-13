package com.kaushik.travelplan.service;

import com.kaushik.travelplan.dto.TripScore;
import com.kaushik.travelplan.entity.Hotel;
import com.kaushik.travelplan.entity.Place;
import com.kaushik.travelplan.entity.Restaurant;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * FEATURE 16: Trip Scoring Service
 * Scores the generated trip on multiple criteria (0-100 each).
 */
@Service
public class TripScoringService {

    /**
     * Score the trip on 5 dimensions and compute an overall score.
     */
    public TripScore scoreTrip(Hotel hotel, List<Place> places, List<Restaurant> restaurants,
                               int budget, int totalCost, int days) {

        TripScore score = new TripScore();

        // 1. Budget Utilization (0-100): how well was the budget used?
        //    Ideal: 85-95% utilization. Over-budget = penalty. Under 50% = low score.
        double utilization = (budget > 0) ? (double) totalCost / budget : 0;
        int budgetScore;
        if (utilization >= 0.85 && utilization <= 1.0) {
            budgetScore = 90 + (int)((1.0 - Math.abs(utilization - 0.92)) * 100);
            budgetScore = Math.min(100, budgetScore);
        } else if (utilization > 1.0) {
            budgetScore = Math.max(10, (int)(100 - (utilization - 1.0) * 200));
        } else {
            budgetScore = Math.max(20, (int)(utilization * 100));
        }
        score.setBudgetUtilization(Math.min(100, Math.max(0, budgetScore)));

        // 2. Diversity Score (0-100): variety of place categories
        Set<String> categories = new HashSet<>();
        if (places != null) {
            for (Place p : places) {
                categories.add(p.getCategory() != null ? p.getCategory().toLowerCase() : "general");
            }
        }
        int diversityScore = Math.min(100, categories.size() * 20);
        score.setDiversityScore(diversityScore);

        // 3. Hotel Quality (0-100): based on rating
        int hotelScore = 0;
        if (hotel != null) {
            hotelScore = (int)(hotel.getRating() / 5.0 * 100);
        }
        score.setHotelQuality(Math.min(100, hotelScore));

        // 4. Food Quality (0-100): based on restaurant ratings
        int foodScore = 50; // default if no restaurants
        if (restaurants != null && !restaurants.isEmpty()) {
            double avgRating = restaurants.stream()
                    .filter(Objects::nonNull)
                    .mapToDouble(Restaurant::getRating)
                    .average().orElse(3.0);
            foodScore = (int)(avgRating / 5.0 * 100);
        }
        score.setFoodQuality(Math.min(100, foodScore));

        // 5. Itinerary Balance (0-100): places per day balance
        int placesPerDay = days > 0 ? places.size() / days : 0;
        int balanceScore;
        if (placesPerDay >= 2 && placesPerDay <= 4) {
            balanceScore = 90; // ideal range
        } else if (placesPerDay == 1 || placesPerDay == 5) {
            balanceScore = 70;
        } else if (placesPerDay == 0) {
            balanceScore = 30;
        } else {
            balanceScore = 50; // too many
        }
        score.setItineraryBalance(balanceScore);

        // Overall: weighted average
        int overall = (int)(
                score.getBudgetUtilization() * 0.25 +
                score.getDiversityScore() * 0.20 +
                score.getHotelQuality() * 0.25 +
                score.getFoodQuality() * 0.15 +
                score.getItineraryBalance() * 0.15
        );
        score.setOverallScore(Math.min(100, Math.max(0, overall)));

        // Verdict
        if (overall >= 85) score.setVerdict("🏆 Excellent Trip!");
        else if (overall >= 70) score.setVerdict("👍 Good Trip");
        else if (overall >= 50) score.setVerdict("😐 Average Trip");
        else score.setVerdict("👎 Below Average — Consider increasing budget");

        return score;
    }
}
