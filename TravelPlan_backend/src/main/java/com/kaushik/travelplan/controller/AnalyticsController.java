package com.kaushik.travelplan.controller;

import com.kaushik.travelplan.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173"})
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/trending")
    public Map<String, Object> getTrending() {
        Map<String, Object> result = new HashMap<>();
        result.put("trendingCities", analyticsService.getTrendingCities());
        result.put("topHotels", analyticsService.getTopHotels());
        result.put("topPlaces", analyticsService.getTopPlaces());
        result.put("topRestaurants", analyticsService.getTopRestaurants());
        return result;
    }

    @GetMapping("/cities")
    public List<String> getAvailableCities() {
        return analyticsService.getAvailableCities();
    }
}
