package com.kaushik.travelplan.service.singletrip;

import com.kaushik.travelplan.dto.ComparisonRequest;
import com.kaushik.travelplan.dto.ComparisonResponse;
import com.kaushik.travelplan.dto.TripRequest;
import com.kaushik.travelplan.dto.TripResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * FEATURE 9: Trip Comparison Service
 * Compare 2-3 budget levels side-by-side.
 */
@Service
public class ComparisonService {

    @Autowired
    private TripPlannerService tripPlannerService;

    /**
     * Generate plans for multiple budget levels for comparison.
     */
    public ComparisonResponse compareBudgets(ComparisonRequest req) {
        ComparisonResponse response = new ComparisonResponse();
        List<TripResponse> plans = new ArrayList<>();

        for (int budget : req.getBudgets()) {
            TripRequest tripReq = new TripRequest();
            tripReq.setCity(req.getCity());
            tripReq.setDays(req.getDays());
            tripReq.setBudget(budget);
            tripReq.setFoodType(req.getFoodType());
            tripReq.setTravelers(req.getTravelers() > 0 ? req.getTravelers() : 1);
            tripReq.setTravelDate(req.getTravelDate());

            TripResponse plan = tripPlannerService.generatePlan(tripReq);
            plans.add(plan);
        }

        response.setPlans(plans);

        // Generate recommendation
        if (plans.size() >= 2) {
            TripResponse cheapest = plans.get(0);
            TripResponse expensive = plans.get(plans.size() - 1);

            int scoreDiff = 0;
            if (expensive.getTripScore() != null && cheapest.getTripScore() != null) {
                scoreDiff = expensive.getTripScore().getOverallScore() - cheapest.getTripScore().getOverallScore();
            }

            int priceDiff = expensive.getTotalCost() - cheapest.getTotalCost();

            if (scoreDiff > 15) {
                response.setRecommendation("💎 The higher budget (₹" + expensive.getTotalCost()
                        + ") gives a significantly better experience with +" + scoreDiff
                        + " quality score improvement.");
            } else if (scoreDiff > 5) {
                response.setRecommendation("👍 The mid-range budget offers good value. Higher budget gives marginal improvements.");
            } else {
                response.setRecommendation("✅ The lower budget is excellent value! Higher spending doesn't significantly improve the trip.");
            }
        }

        return response;
    }
}

