package com.kaushik.travelplan.service;

import com.kaushik.travelplan.entity.Hotel;
import com.kaushik.travelplan.entity.Place;
import com.kaushik.travelplan.entity.Restaurant;
import com.kaushik.travelplan.repository.HotelRepository;
import com.kaushik.travelplan.repository.PlaceRepository;
import com.kaushik.travelplan.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * FEATURE 17: Analytics & Trending Service
 * Returns top-rated hotels, places, and trending cities.
 */
@Service
public class AnalyticsService {

    @Autowired
    private HotelRepository hotelRepo;

    @Autowired
    private PlaceRepository placeRepo;

    @Autowired
    private RestaurantRepository restaurantRepo;

    /**
     * Get top-rated hotels across all cities (top 10).
     */
    public List<Hotel> getTopHotels() {
        return hotelRepo.findAll().stream()
                .sorted(Comparator.comparingDouble(Hotel::getRating).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * Get top-rated places across all cities (top 10).
     */
    public List<Place> getTopPlaces() {
        return placeRepo.findAll().stream()
                .sorted(Comparator.comparingDouble(Place::getRating).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * Get top-rated restaurants across all cities (top 10).
     */
    public List<Restaurant> getTopRestaurants() {
        return restaurantRepo.findAll().stream()
                .sorted(Comparator.comparingDouble(Restaurant::getRating).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * Get trending/popular cities based on number of listings.
     */
    public List<Map<String, Object>> getTrendingCities() {
        Map<String, Integer> cityCount = new HashMap<>();

        hotelRepo.findAll().forEach(h -> {
            String city = h.getCity() != null ? h.getCity().toLowerCase() : "unknown";
            cityCount.merge(city, 1, (oldValue, newValue) -> oldValue + newValue);
        });
        placeRepo.findAll().forEach(p -> {
            String city = p.getCity() != null ? p.getCity().toLowerCase() : "unknown";
            cityCount.merge(city, 1, (oldValue, newValue) -> oldValue + newValue);
        });

        return cityCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("city", entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1));
                    map.put("listings", entry.getValue());

                    // Get average rating for the city
                    double avgHotelRating = hotelRepo.findByCityIgnoreCase(entry.getKey()).stream()
                            .mapToDouble(Hotel::getRating).average().orElse(0);
                    double avgPlaceRating = placeRepo.findByCityIgnoreCase(entry.getKey()).stream()
                            .mapToDouble(Place::getRating).average().orElse(0);
                    map.put("avgRating", Math.round((avgHotelRating + avgPlaceRating) / 2 * 10.0) / 10.0);

                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get available cities.
     */
    public List<String> getAvailableCities() {
        Set<String> cities = new TreeSet<>();
        hotelRepo.findAll().forEach(h -> {
            if (h.getCity() != null) cities.add(h.getCity());
        });
        placeRepo.findAll().forEach(p -> {
            if (p.getCity() != null) cities.add(p.getCity());
        });
        return new ArrayList<>(cities);
    }
}
