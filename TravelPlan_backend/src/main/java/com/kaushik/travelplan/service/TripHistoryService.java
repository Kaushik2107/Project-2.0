package com.kaushik.travelplan.service;

import com.kaushik.travelplan.entity.TripHistory;
import com.kaushik.travelplan.repository.TripHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * FEATURE 10: Trip History Service
 * Save and retrieve past trip plans.
 */
@Service
public class TripHistoryService {

    @Autowired
    private TripHistoryRepository historyRepo;

    /**
     * Save a trip plan to history.
     */
    public TripHistory saveTripHistory(String visitorName, String city, int budget,
                                       int days, int travelers, String foodType,
                                       int totalCost, int perPersonCost,
                                       String hotelName, String planSummary) {
        TripHistory history = new TripHistory();
        history.setVisitorName(visitorName != null ? visitorName : "Anonymous");
        history.setCity(city);
        history.setBudget(budget);
        history.setDays(days);
        history.setTravelers(travelers);
        history.setFoodType(foodType);
        history.setTotalCost(totalCost);
        history.setPerPersonCost(perPersonCost);
        history.setHotelName(hotelName);
        history.setPlanSummary(planSummary);
        history.setCreatedAt(LocalDateTime.now());

        return historyRepo.save(history);
    }

    /**
     * Get all trip history for a visitor.
     */
    public List<TripHistory> getHistoryByVisitor(String visitorName) {
        return historyRepo.findByVisitorNameIgnoreCaseOrderByCreatedAtDesc(visitorName);
    }

    /**
     * Get all trip history (most recent first).
     */
    public List<TripHistory> getAllHistory() {
        return historyRepo.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Get trip history for a city.
     */
    public List<TripHistory> getHistoryByCity(String city) {
        return historyRepo.findByCityIgnoreCaseOrderByCreatedAtDesc(city);
    }

    /**
     * Delete a trip from history.
     */
    public void deleteHistory(String id) {
        historyRepo.deleteById(id);
    }
}
