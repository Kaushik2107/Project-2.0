package com.kaushik.travelplan.service;

import com.kaushik.travelplan.entity.Hotel;
import com.kaushik.travelplan.entity.Place;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SERVICE 5: Suggestion Engine
 * "What if your budget increases by ₹2000?"
 */
@Service
public class SuggestionService {

    private static final int SUGGESTION_EXTRA_BUDGET = 2000;

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private HotelScoringService hotelScoringService;

    @Autowired
    private PlaceScoringService placeScoringService;

    /**
     * Build a suggestion message showing what improvements are possible with extra budget.
     */
    public String buildSuggestion(List<Hotel> allHotels, List<Place> allPlaces,
                                   int budget, int days, String foodType, int manualFoodBudget, int currentTotalCost) {
        
        int difference = budget - currentTotalCost;
        if (difference > 1000) {
           return "💡 You have a remaining budget of ₹" + difference + ". Consider upgrading your food preference to Premium or treating yourself to local shopping to maximize your trip!";
        }

        return "✅ Your budget is fully maximized for the ultimate premium experience within your constraints!";
    }
}
