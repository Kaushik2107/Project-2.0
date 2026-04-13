package com.kaushik.travelplan.service;

import com.kaushik.travelplan.entity.Place;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * SERVICE 3: Place Scoring & Selection
 * Greedy selection with category diversity.
 */
@Service
public class PlaceScoringService {

    /**
     * Select best places using greedy algorithm with category diversity.
     * First pass: one from each category. Second pass: fill remaining budget.
     */
    public List<Place> selectBestPlaces(List<Place> places, int placesBudget) {
        if (placesBudget <= 0 || places.isEmpty()) return Collections.emptyList();

        double maxRating = places.stream().mapToDouble(Place::getRating).max().orElse(5.0);
        int maxFee = places.stream().mapToInt(Place::getEntryFee).max().orElse(1);

        // Sort by combined score descending
        List<Place> scored = new ArrayList<>(places);
        scored.sort((a, b) -> {
            double sa = scorePlace(a, maxRating, maxFee);
            double sb = scorePlace(b, maxRating, maxFee);
            return Double.compare(sb, sa);
        });

        // Greedy selection within budget, with category diversity
        List<Place> selected = new ArrayList<>();
        Set<String> usedCategories = new HashSet<>();
        int spent = 0;

        // First pass: pick one from each category
        for (Place p : scored) {
            String cat = (p.getCategory() != null) ? p.getCategory().toLowerCase() : "general";
            if (!usedCategories.contains(cat) && spent + p.getEntryFee() <= placesBudget) {
                selected.add(p);
                usedCategories.add(cat);
                spent += p.getEntryFee();
            }
        }

        // Second pass: fill remaining budget with best remaining places
        for (Place p : scored) {
            if (!selected.contains(p) && spent + p.getEntryFee() <= placesBudget) {
                selected.add(p);
                spent += p.getEntryFee();
            }
        }

        return selected;
    }

    /**
     * Score a place: rating (60%) + value-for-money (40%).
     */
    public double scorePlace(Place p, double maxRating, int maxFee) {
        double ratingScore = (maxRating > 0) ? p.getRating() / maxRating : 0;
        double valueScore = (maxFee > 0) ? 1.0 - ((double) p.getEntryFee() / maxFee) : 0.5;
        return (ratingScore * 0.60) + (valueScore * 0.40);
    }
}
