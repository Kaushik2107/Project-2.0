package com.kaushik.travelplan.service.singletrip;

import com.kaushik.travelplan.dto.*;
import com.kaushik.travelplan.entity.*;
import com.kaushik.travelplan.exception.*;
import com.kaushik.travelplan.repository.HotelRepository;
import com.kaushik.travelplan.repository.PlaceRepository;
import com.kaushik.travelplan.service.grouptrip.GroupTripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * MAIN ORCHESTRATOR SERVICE
 * Replaces the old monolithic TripService.
 * Coordinates all sub-services to generate a complete trip plan.
 */
@Service
public class TripPlannerService {

    @Autowired private HotelRepository hotelRepo;
    @Autowired private PlaceRepository placeRepo;

    @Autowired private BudgetService budgetService;
    @Autowired private HotelScoringService hotelScoringService;
    @Autowired private PlaceScoringService placeScoringService;
    @Autowired private KnapsackOptimizerService knapsackService;
    @Autowired private ProximityService proximityService;
    @Autowired private ItineraryService itineraryService;
    @Autowired
    private SuggestionService suggestionService;

    @Autowired
    private VibeService vibeService;

    @Autowired
    private WeatherSimService weatherSimService;

    @Autowired private RestaurantService restaurantService;
    @Autowired private TransportService transportService;
    @Autowired private TripScoringService tripScoringService;
    @Autowired private GroupTripService groupTripService;
    @Autowired private ImageService imageService;

    // ═══════════════════════════════════════════════
    //  MAIN ENTRY POINT
    // ═══════════════════════════════════════════════
    public TripResponse generatePlan(TripRequest req) {

        // ── Input validation ──
        validateRequest(req);

        String city = req.getCity().trim();
        int travelers = req.getTravelers();

        // ── CUSTOM MODE ──
        if (isCustomMode(req)) {
            return handleCustomMode(req, city);
        }

        // ── SMART PLANNING MODE ──
        return handleSmartPlanning(req, city);
    }

    // ═══════════════════════════════════════════════
    //  VALIDATION
    // ═══════════════════════════════════════════════
    private void validateRequest(TripRequest req) {
        if (req.getCity() == null || req.getCity().trim().isEmpty()) {
            throw new InvalidRequestException("City is required");
        }
        if (req.getDays() <= 0) {
            throw new InvalidRequestException("Days must be greater than 0");
        }
        if (req.getBudget() <= 0) {
            throw new InvalidRequestException("Budget must be greater than 0");
        }
        if (req.getTravelers() <= 0) {
            throw new InvalidRequestException("Travelers must be at least 1");
        }
    }

    // ═══════════════════════════════════════════════
    //  CUSTOM MODE
    // ═══════════════════════════════════════════════
    private boolean isCustomMode(TripRequest req) {
        return (req.getSelectedHotelId() != null && !req.getSelectedHotelId().isEmpty())
            || (req.getSelectedPlaceIds() != null && !req.getSelectedPlaceIds().isEmpty());
    }

    private TripResponse handleCustomMode(TripRequest req, String city) {
        TripResponse res = new TripResponse();
        int days = req.getDays();
        int travelers = req.getTravelers();
        int foodCostPerDay = budgetService.getFoodCostPerDay(req.getFoodType(), req.getManualFoodBudget());


        // Resolve hotel
        Hotel hotel = null;
        if (req.getSelectedHotelId() != null && !req.getSelectedHotelId().isEmpty()) {
            hotel = hotelRepo.findById(req.getSelectedHotelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hotel", req.getSelectedHotelId()));
        }

        // Resolve places
        List<Place> selectedPlaces = new ArrayList<>();
        if (req.getSelectedPlaceIds() != null) {
            for (String pid : req.getSelectedPlaceIds()) {
                placeRepo.findById(pid).ifPresent(selectedPlaces::add);
            }
        }

        // Calculate costs with group awareness
        int hotelCost = hotel != null ?
                (int) Math.ceil((double) travelers / hotel.getMaxOccupancy()) * hotel.getPricePerNight() * days : 0;
        int foodCost = foodCostPerDay * travelers * days;
        int placesCost = selectedPlaces.stream().mapToInt(Place::getEntryFee).sum() * travelers;
        int travelCost = 300 * days * (int) Math.ceil((double) travelers / 4);
        int totalCost = hotelCost + foodCost + travelCost + placesCost;

        res.setHotel(hotel);
        res.setPlaces(selectedPlaces);
        res.setHotelCost(hotelCost);
        res.setFoodCost(foodCost);
        res.setTravelCost(travelCost);
        res.setPlacesCost(placesCost);
        res.setTotalCost(totalCost);
        res.setTravelers(travelers);
        res.setPerPersonCost(totalCost / travelers);
        res.setDayWisePlan(itineraryService.buildDayWisePlan(hotel, selectedPlaces, days));
        res.setMessage("Custom mode: total cost calculated for your selections.");

        // Season info
        res.setSeasonInfo(budgetService.getSeasonLabel(req.getTravelDate()));
        res.setSeasonMultiplier(budgetService.getSeasonMultiplier(req.getTravelDate()));
        
        res.setTripVibe(vibeService.calculateVibe(selectedPlaces));
        res.setWeatherForecast(weatherSimService.getSimulatedWeather(city));
        
        // Ensure consistent UI experience
        res.setTripScore(tripScoringService.scoreTrip(hotel, selectedPlaces, Collections.emptyList(),
                req.getBudget(), totalCost, days));

        return res;
    }

    // ═══════════════════════════════════════════════
    //  SMART PLANNING MODE (Main Logic)
    // ═══════════════════════════════════════════════
    private TripResponse handleSmartPlanning(TripRequest req, String city) {

        int budget = req.getBudget();
        int days = req.getDays();
        int travelers = req.getTravelers();

        // ── Seasonal pricing adjustment ──
        double seasonMultiplier = budgetService.getSeasonMultiplier(req.getTravelDate());

        // ── Fetch data from MongoDB ──
        List<Hotel> hotels = hotelRepo.findByCityIgnoreCase(city);
        List<Place> places = placeRepo.findByCityIgnoreCase(city);

        // ── Safe handling: no data ──
        if (hotels.isEmpty() && places.isEmpty()) {
            throw new CityNotFoundException(city);
        }
        if (hotels.isEmpty()) {
            TripResponse res = new TripResponse();
            res.setMessage("No hotels found for city: " + city + ". Try another city!");
            res.setPlaces(Collections.emptyList());
            res.setDayWisePlan(Collections.emptyList());
            res.setTotalCost(0);
            res.setPerPersonCost(0);
            res.setTripVibe("Unknown");
            res.setWeatherForecast(weatherSimService.getSimulatedWeather(city));
            return res;
        }


        // ── FEATURE 1: Budget distribution ──
        BudgetBreakdown bb = budgetService.distribute(budget, days, req.getFoodType(), req.getTravelDate());

        // ── FEATURE 2: Food customization ──
        int foodCostPerDay = budgetService.getFoodCostPerDay(req.getFoodType(), req.getManualFoodBudget());

        int foodCost = foodCostPerDay * travelers * days;

        // ── Hotel selection ──
        // We look for hotels whose NORMAL price fits within the target budget.
        // We don't divide by seasonMultiplier here because we want to see if the BASE price fits.
        int hotelBudget = bb.getHotelBudget(); 
        Hotel bestHotel = hotelScoringService.selectBestHotel(hotels, hotelBudget, days);

        // Group: rooms needed
        int roomsNeeded = (int) Math.ceil((double) travelers / bestHotel.getMaxOccupancy());
        int hotelCost = roomsNeeded * bestHotel.getPricePerNight() * days;
        hotelCost = (int)(hotelCost * seasonMultiplier); // apply seasonal pricing

        // ── FEATURE 8: Smart transport ──
        double totalDistanceKm = 0;
        Map<String, Object> transportInfo = new HashMap<>();

        if (!places.isEmpty()) {
            // ── FEATURE 13: Knapsack optimizer for places ──
            int remainingForPlaces = budget - (hotelCost + foodCost);
            if (remainingForPlaces < 0) remainingForPlaces = 0;
            // Use as much of the remaining budget as possible for places to satisfy high-budget requests
            int placesBudgetPerPerson = remainingForPlaces / travelers;


            List<Place> selectedPlaces = knapsackService.optimizePlaces(places, placesBudgetPerPerson);

            // ── FEATURE 12: Proximity optimization ──
            selectedPlaces = proximityService.optimizeRouteOrder(bestHotel, selectedPlaces);

            // Calculate transport
            totalDistanceKm = transportService.calculateTotalDistance(bestHotel, selectedPlaces);
            transportInfo = transportService.calculateTransportCost(city, totalDistanceKm, days, travelers);
            int transportCost = (int) transportInfo.getOrDefault("totalCost", 300 * days);

            // ── FEATURE 7: Restaurant recommendations ──
            List<Restaurant> restaurants = restaurantService.recommendRestaurants(
                    city, bb.getFoodBudget(), days, travelers,
                    req.getDietPreference(), req.getCuisinePreference());
            int restaurantCost = restaurantService.calculateRestaurantCost(restaurants, days, travelers);
            
            // Critical fix: If the cheapest restaurants are still way too expensive, drop them.
            // We allow up to 50% flex on the food budget, or if the total budget isn't blown yet.
            if (!restaurants.isEmpty() && restaurantCost <= (bb.getFoodBudget() * 2 + 2000)) {
                foodCost = restaurantCost;
            } else if (!restaurants.isEmpty() && (hotelCost + restaurantCost + (300 * days)) < budget) {
                // If the total is incredibly still under budget despite expensive food, allow it
                foodCost = restaurantCost;
            } else {
                // The premium restaurants are too expensive for this budget!
                restaurants = Collections.emptyList();
                restaurantCost = foodCost; // Keep standard local food cost
            }

            // ── Dynamic Image Hydration ──
            if (bestHotel != null && (bestHotel.getImageUrl() == null || bestHotel.getImageUrl().contains("unsplash") || bestHotel.getImageUrl().startsWith("/images/") || bestHotel.getImageUrl().isEmpty() || bestHotel.getImageUrl().contains("null"))) {
                bestHotel.setImageUrl(imageService.fetchImageForLocation(bestHotel.getName() + " " + city + " hotel"));
            }
            if (selectedPlaces != null) {
                selectedPlaces.parallelStream().forEach(p -> {
                    if (p.getImageUrl() == null || p.getImageUrl().contains("unsplash") || p.getImageUrl().startsWith("/images/") || p.getImageUrl().isEmpty() || p.getImageUrl().contains("null")) {
                        p.setImageUrl(imageService.fetchImageForLocation(p.getName() + " " + city));
                    }
                });
            }
            if (restaurants != null) {
                restaurants.parallelStream().forEach(r -> {
                    if (r.getImageUrl() == null || r.getImageUrl().contains("unsplash") || r.getImageUrl().startsWith("/images/") || r.getImageUrl().isEmpty() || r.getImageUrl().contains("null")) {
                        r.setImageUrl(imageService.fetchImageForLocation(r.getName() + " " + city + " restaurant"));
                    }
                });
            }

            // ── Calculate totals ──
            int placesCost = selectedPlaces.stream().mapToInt(Place::getEntryFee).sum() * travelers;
            int totalCost = hotelCost + foodCost + transportCost + placesCost;

            // ── Build response ──
            TripResponse res = new TripResponse();
            res.setHotel(bestHotel);
            res.setPlaces(selectedPlaces);
            res.setTravelers(travelers);
            res.setHotelCost(hotelCost);
            res.setFoodCost(foodCost);
            res.setTravelCost(transportCost);
            res.setPlacesCost(placesCost);
            res.setTotalCost(totalCost);
            res.setPerPersonCost(totalCost / travelers);
            res.setCityImageUrl(imageService.fetchImageForLocation(city + " travel"));

            // Restaurants
            res.setRestaurants(restaurants);
            res.setRestaurantCost(restaurantCost);

            // Transport details
            res.setTransportMode((String) transportInfo.getOrDefault("mode", "cab"));
            res.setTransportCost(transportCost);
            res.setTotalDistanceKm(Math.round(totalDistanceKm * 100.0) / 100.0);

            // Budget breakdown
            bb.setHotelActual(hotelCost);
            bb.setFoodActual(foodCost);
            bb.setTravelActual(transportCost);
            bb.setPlacesActual(placesCost);
            bb.setSavings(budget - totalCost);
            res.setBudgetBreakdown(bb);

            // ── CRAZY TRANSPORT (Ahmedabad -> Goa example) ──
            if (req.getSourceCity() != null && !req.getSourceCity().isEmpty()) {
                String source = req.getSourceCity().trim();
                String dest = city;
                String arrivalTxt = String.format("A Bus will pick you up at 6 PM from %s Main Station. You will reach %s at 3 AM. Stay in the selected hotel and enjoy breakfast there.", source, dest);
                res.setArrivalDetails(arrivalTxt);
            }

            // ── FEATURE 3: Day-wise plan ──
            res.setDayWisePlan(itineraryService.buildDayWisePlan(bestHotel, selectedPlaces, days));
            res.setDayWisePlanDetailed(itineraryService.buildDetailedDayPlan(
                    bestHotel, selectedPlaces, restaurants, days,
                    (String) transportInfo.getOrDefault("mode", "cab"),
                    totalDistanceKm / Math.max(1, days),
                    res.getArrivalDetails()));

            // ── FEATURE 4: Suggestion engine ──
            res.setSuggestionMessage(suggestionService.buildSuggestion(
                    hotels, places, budget, days, req.getFoodType(), req.getManualFoodBudget(), totalCost));


            // ── FEATURE 16: Trip scoring ──
            res.setTripScore(tripScoringService.scoreTrip(bestHotel, selectedPlaces, restaurants,
                    budget, totalCost, days));

            // ── FEATURE 18: Seasonal info ──
            res.setSeasonInfo(budgetService.getSeasonLabel(req.getTravelDate()));
            res.setSeasonMultiplier(seasonMultiplier);

            // ── FEATURE 15: Group trip breakdown ──
            if (travelers > 1) {
                GroupCostBreakdown gcb = groupTripService.calculateGroupCost(
                        bestHotel, selectedPlaces, days, travelers, foodCostPerDay, transportInfo);
                res.setGroupCostBreakdown(gcb);
            }
            
            res.setTripVibe(vibeService.calculateVibe(selectedPlaces));
            res.setWeatherForecast(weatherSimService.getSimulatedWeather(city));

            return res;
        }

        // Places empty - hotel only
        TripResponse res = new TripResponse();
        res.setHotel(bestHotel);
        res.setHotelCost(hotelCost);
        if (bestHotel != null && (bestHotel.getImageUrl() == null || bestHotel.getImageUrl().contains("unsplash") || bestHotel.getImageUrl().startsWith("/images/") || bestHotel.getImageUrl().isEmpty() || bestHotel.getImageUrl().contains("null"))) {
            bestHotel.setImageUrl(imageService.fetchImageForLocation(bestHotel.getName() + " " + city + " hotel"));
        }

        res.setFoodCost(foodCost);
        res.setTotalCost(hotelCost + foodCost);
        res.setPerPersonCost((hotelCost + foodCost) / travelers);
        res.setPlaces(Collections.emptyList());
        res.setDayWisePlan(itineraryService.buildDayWisePlan(bestHotel, Collections.emptyList(), days));
        res.setTripVibe("Eclectic & Diverse");
        res.setWeatherForecast(weatherSimService.getSimulatedWeather(city));
        res.setMessage("Minimal options: We found a hotel but no activities within budget.");
        
        // Ensure even minimal plans have a score & detailed structure for UI
        res.setTripScore(tripScoringService.scoreTrip(bestHotel, Collections.emptyList(), Collections.emptyList(),
                budget, hotelCost + foodCost, days));
        res.setDayWisePlanDetailed(itineraryService.buildDetailedDayPlan(
                bestHotel, Collections.emptyList(), Collections.emptyList(), days, "cab", 0, null));

        return res;

    }
}

