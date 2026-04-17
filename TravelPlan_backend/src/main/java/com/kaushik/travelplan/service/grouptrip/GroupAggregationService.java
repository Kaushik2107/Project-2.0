package com.kaushik.travelplan.service.grouptrip;

import com.kaushik.travelplan.dto.TripRequest;
import com.kaushik.travelplan.entity.GroupTrip;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupAggregationService {

    /**
     * Aggregates multiple member responses into a single "Consensus Preference".
     * Priority: High frequency interests, average budget, etc.
     */
    public TripRequest aggregatePreferences(GroupTrip gt) {
        TripRequest consensus = new TripRequest();
        consensus.setCity(gt.getDestination());
        consensus.setDays(gt.getDays());
        
        if (gt.getResponses().isEmpty()) {
            // Default baseline if no one responded
            consensus.setBudget(50000);
            consensus.setFoodType("standard");
            consensus.setPace("moderate");
            consensus.setSpecialInterests(new ArrayList<>());
            consensus.setTravelers(1);
            return consensus;
        }

        Collection<TripRequest> responses = gt.getResponses().stream()
                .map(GroupTrip.MemberResponse::getRequest)
                .collect(Collectors.toList());

        // 1. Budget: Use average
        int avgBudget = (int) responses.stream().mapToInt(TripRequest::getBudget).average().orElse(50000);
        consensus.setBudget(avgBudget);

        // 2. Food Type: Most frequent
        String topFood = responses.stream()
                .collect(Collectors.groupingBy(TripRequest::getFoodType, Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("standard");
        consensus.setFoodType(topFood);

        // 3. Interests: Union of all special interests
        Set<String> allInterests = new HashSet<>();
        responses.forEach(r -> {
            if (r.getSpecialInterests() != null) allInterests.addAll(r.getSpecialInterests());
        });
        consensus.setSpecialInterests(new ArrayList<>(allInterests));

        // 4. Pace: Most frequent
        String topPace = responses.stream()
                .collect(Collectors.groupingBy(TripRequest::getPace, Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("moderate");
        consensus.setPace(topPace);

        consensus.setTravelers(gt.getResponses().size());
        
        return consensus;
    }
}
