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
     * Select places to visit that maximize total rating within budget.
     * Classic 0/1 Knapsack using Dynamic Programming.
     */
    public List<Place> optimizePlaces(List<Place> places, int budgetLimit) {
        if (places.isEmpty() || budgetLimit <= 0) return Collections.emptyList();

        int n = places.size();

        // Scale down budget to avoid massive DP table (use ₹10 granularity)
        int scale = 10;
        int capacity = budgetLimit / scale;
        if (capacity <= 0) {
            capacity = budgetLimit;
            scale = 1;
        }

        // DP table: dp[i][w] = max rating using first i items with capacity w
        double[][] dp = new double[n + 1][capacity + 1];
        int[] scaledCosts = new int[n];

        for (int i = 0; i < n; i++) {
            scaledCosts[i] = Math.max(1, places.get(i).getEntryFee() / scale);
        }

        // Fill DP table
        for (int i = 1; i <= n; i++) {
            int cost = scaledCosts[i - 1];
            double rating = places.get(i - 1).getRating();

            for (int w = 0; w <= capacity; w++) {
                dp[i][w] = dp[i - 1][w]; // don't take item i
                if (cost <= w) {
                    dp[i][w] = Math.max(dp[i][w], dp[i - 1][w - cost] + rating);
                }
            }
        }

        // Backtrack to find selected items
        List<Place> selected = new ArrayList<>();
        int w = capacity;
        for (int i = n; i >= 1; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                selected.add(places.get(i - 1));
                w -= scaledCosts[i - 1];
            }
        }

        // Verify total actual cost doesn't exceed budget
        int totalCost = selected.stream().mapToInt(Place::getEntryFee).sum();
        while (totalCost > budgetLimit && !selected.isEmpty()) {
            Place removed = selected.remove(selected.size() - 1);
            totalCost -= removed.getEntryFee();
        }

        return selected;
    }
}

