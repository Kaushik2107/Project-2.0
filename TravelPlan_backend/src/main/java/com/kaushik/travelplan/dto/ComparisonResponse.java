package com.kaushik.travelplan.dto;

import java.util.List;

public class ComparisonResponse {
    private List<TripResponse> plans;   // one per budget level
    private String recommendation;       // which budget level is best value

    public List<TripResponse> getPlans() { return plans; }
    public void setPlans(List<TripResponse> plans) { this.plans = plans; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
}
