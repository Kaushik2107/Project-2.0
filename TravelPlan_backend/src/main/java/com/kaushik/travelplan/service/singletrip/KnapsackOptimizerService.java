package com.kaushik.travelplan.service.singletrip;

import com.kaushik.travelplan.entity.Place;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * FEATURE 13: 0/1 Knapsack Optimizer
 * Uses Dynamic Programming to maximize total place rating within budget constraint.
 */
@Service
public class KnapsackOptimizerService {

    /**
     * Select places to visit that maximize total rating within budget and time constraints.
     */
    public List<Place> optimizePlaces(List<Place> places, int budgetLimit, int days) {
        if (places.isEmpty() || budgetLimit <= 0 || days <= 0) return Collections.emptyList();

        // ── STEP 1: Budget-based Optimization (Standard 0/1 Knapsack) ──
        int n = places.size();
        int scale = 10;
        int capacity = budgetLimit / scale;
        if (capacity <= 0) {
            capacity = budgetLimit;
            scale = 1;
        }

        double[][] dp = new double[n + 1][capacity + 1];
        int[] scaledCosts = new int[n];

        for (int i = 0; i < n; i++) {
            scaledCosts[i] = Math.max(1, places.get(i).getEntryFee() / scale);
        }

        for (int i = 1; i <= n; i++) {
            int cost = scaledCosts[i - 1];
            double rating = places.get(i - 1).getRating();
            for (int w = 0; w <= capacity; w++) {
                dp[i][w] = dp[i - 1][w];
                if (cost <= w) {
                    dp[i][w] = Math.max(dp[i][w], dp[i - 1][w - cost] + rating);
                }
            }
        }

        List<Place> selected = new ArrayList<>();
        int w = capacity;
        for (int i = n; i >= 1; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                selected.add(places.get(i - 1));
                w -= scaledCosts[i - 1];
            }
        }

        // ── STEP 2: Realistic Constraints (Human Sanity Checks) ──
        
        // 1. Cap by total count (Max 7-8 places per day is highly active, more is impossible)
        int maxPlacesPossible = days * 8;
        if (selected.size() > maxPlacesPossible) {
            // Sort by rating to keep the best ones
            selected.sort(Comparator.comparingDouble(Place::getRating).reversed());
            selected = new ArrayList<>(selected.subList(0, maxPlacesPossible));
        }

        // 2. Cap by total duration (Max ~10 hours of activities per day)
        int maxDurationMinutes = days * 600; // 10 hours per day
        int currentDuration = selected.stream().mapToInt(Place::getDurationMinutes).sum();
        
        if (currentDuration > maxDurationMinutes) {
            // Sort by Value Density (Rating per Minute)
            selected.sort((p1, p2) -> {
                double v1 = p1.getRating() / Math.max(1, p1.getDurationMinutes());
                double v2 = p2.getRating() / Math.max(1, p2.getDurationMinutes());
                return Double.compare(v2, v1);
            });
            
            List<Place> cappedList = new ArrayList<>();
            int totalD = 0;
            for (Place p : selected) {
                if (totalD + p.getDurationMinutes() <= maxDurationMinutes) {
                    cappedList.add(p);
                    totalD += p.getDurationMinutes();
                }
            }
            selected = cappedList;
        }

        // Final budget check
        int totalCost = selected.stream().mapToInt(Place::getEntryFee).sum();
        while (totalCost > budgetLimit && !selected.isEmpty()) {
            Place removed = selected.remove(selected.size() - 1);
            totalCost -= removed.getEntryFee();
        }

        return selected;
    }
}

