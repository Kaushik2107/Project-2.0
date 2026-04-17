package com.kaushik.travelplan.service.singletrip;

import com.kaushik.travelplan.entity.Place;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VibeService {

    public String calculateVibe(List<Place> places) {
        if (places == null || places.isEmpty()) return "Unknown";

        Map<String, Long> categoryCounts = places.stream()
                .filter(p -> p.getCategory() != null)
                .collect(Collectors.groupingBy(Place::getCategory, Collectors.counting()));

        String topCategory = categoryCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Cultural");

        switch (topCategory.toLowerCase()) {
            case "beach":
            case "nature":
                return "Relaxing & Serene";
            case "heritage":
            case "temple":
            case "spirituality":
                return "Cultural & Spiritual";
            case "adventure":
                return "Action-Packed";
            case "palace":
                return "Royal & Majestic";
            default:
                return "Eclectic & Diverse";
        }
    }
}

