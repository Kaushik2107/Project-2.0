package com.kaushik.travelplan.service.singletrip;

import com.kaushik.travelplan.entity.Restaurant;
import com.kaushik.travelplan.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * FEATURE 7: Restaurant Recommendation Service
 * Recommends real restaurants based on budget, cuisine preference, and rating.
 * Uses a knapsack-like approach to maximize meal quality within food budget.
 */
@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepo;

    /**
     * Get recommended restaurants within food budget.
     * Strategy: pick top-rated restaurants that fit per-meal budget.
     *
     * @param city            Target city
     * @param foodBudget      Total food budget for the trip
     * @param days            Number of days
     * @param travelers       Number of travelers
     * @param dietPreference  "veg", "nonveg", "both"
     * @param cuisinePreference Preferred cuisine (nullable)
     * @return List of recommended restaurants
     */
    public List<Restaurant> recommendRestaurants(String city, int foodBudget, int days,
                                                  int travelers, String dietPreference,
                                                  String cuisinePreference) {

        List<Restaurant> allRestaurants = restaurantRepo.findByCityIgnoreCase(city);
        if (allRestaurants.isEmpty()) return Collections.emptyList();

        // Filter by diet preference
        if (dietPreference != null && !dietPreference.equalsIgnoreCase("both")) {
            List<Restaurant> filtered = allRestaurants.stream()
                    .filter(r -> r.getCategory() != null &&
                            (r.getCategory().equalsIgnoreCase(dietPreference) ||
                             r.getCategory().equalsIgnoreCase("both")))
                    .collect(Collectors.toList());
            if (!filtered.isEmpty()) allRestaurants = filtered;
        }

        // Filter by cuisine preference if specified
        if (cuisinePreference != null && !cuisinePreference.isEmpty()) {
            List<Restaurant> cuisineFiltered = allRestaurants.stream()
                    .filter(r -> r.getCuisine() != null &&
                            r.getCuisine().toLowerCase().contains(cuisinePreference.toLowerCase()))
                    .collect(Collectors.toList());
            if (!cuisineFiltered.isEmpty()) allRestaurants = cuisineFiltered;
        }

        // Budget per meal per person: 3 meals/day
        int mealsTotal = days * 3;
        int budgetPerMealPerPerson = foodBudget / (mealsTotal * travelers);
        if (budgetPerMealPerPerson <= 0) budgetPerMealPerPerson = 100;

        // Filter by affordable meal price and sort by rating
        final int maxMealPrice = budgetPerMealPerPerson;
        List<Restaurant> affordable = allRestaurants.stream()
                .filter(r -> r.getPricePerMeal() <= maxMealPrice * 1.2) // allow 20% flex
                .sorted(Comparator.comparingDouble(Restaurant::getRating).reversed())
                .collect(Collectors.toList());

        // If nothing affordable, return the cheapest available to prevent massive budget overruns
        if (affordable.isEmpty()) {
            return allRestaurants.stream()
                    .sorted(Comparator.comparingInt(Restaurant::getPricePerMeal))
                    .limit(6)
                    .collect(Collectors.toList());
        }

        // Select diverse restaurants: try to get breakfast, lunch, dinner variety
        Set<String> selectedMealTypes = new HashSet<>();
        List<Restaurant> selected = new ArrayList<>();

        // First: one per meal type
        for (Restaurant r : affordable) {
            String mt = r.getMealType() != null ? r.getMealType().toLowerCase() : "all";
            if (!selectedMealTypes.contains(mt) || mt.equals("all")) {
                selected.add(r);
                selectedMealTypes.add(mt);
                if (selected.size() >= 6) break;
            }
        }

        // Fill up to 6
        for (Restaurant r : affordable) {
            if (!selected.contains(r) && selected.size() < 6) {
                selected.add(r);
            }
        }

        return selected;
    }

    /**
     * Calculate total restaurant cost for the trip.
     */
    public int calculateRestaurantCost(List<Restaurant> restaurants, int days, int travelers) {
        if (restaurants.isEmpty()) return 0;

        // Average cost per meal from selected restaurants
        int avgMealCost = (int) restaurants.stream()
                .mapToInt(Restaurant::getPricePerMeal)
                .average().orElse(300);

        return avgMealCost * 3 * days * travelers; // 3 meals per day per person
    }

    public List<Restaurant> getAllByCity(String city) {
        return restaurantRepo.findByCityIgnoreCase(city);
    }
}

