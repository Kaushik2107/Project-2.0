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
    @Autowired private SuggestionService suggestionService;
    @Autowired private VibeService vibeService;
    @Autowired private WeatherSimService weatherSimService;
    @Autowired private RestaurantService restaurantService;
    @Autowired private TransportService transportService;
    @Autowired private TripScoringService tripScoringService;
    @Autowired private GroupTripService groupTripService;
    @Autowired private ImageService imageService;
    @Autowired private LLMService llmService;

    public TripResponse generatePlan(TripRequest req) {
        validateRequest(req);
        String city = req.getCity().trim();
        if (isCustomMode(req)) {
            return handleCustomMode(req, city);
        }
        return handleSmartPlanning(req, city);
    }

    private void validateRequest(TripRequest req) {
        if (req.getCity() == null || req.getCity().trim().isEmpty()) throw new InvalidRequestException("City is required");
        if (req.getDays() <= 0) throw new InvalidRequestException("Days must be greater than 0");
        if (req.getBudget() <= 0) throw new InvalidRequestException("Budget must be greater than 0");
        if (req.getTravelers() <= 0) throw new InvalidRequestException("Travelers must be at least 1");
    }

    private boolean isCustomMode(TripRequest req) {
        return (req.getSelectedHotelId() != null && !req.getSelectedHotelId().isEmpty())
            || (req.getSelectedPlaceIds() != null && !req.getSelectedPlaceIds().isEmpty());
    }

    private TripResponse handleCustomMode(TripRequest req, String city) {
        TripResponse res = new TripResponse();
        int days = req.getDays();
        int travelers = req.getTravelers();
        int foodCostPerDay = budgetService.getFoodCostPerDay(req.getFoodType(), req.getManualFoodBudget());

        Hotel hotel = null;
        if (req.getSelectedHotelId() != null && !req.getSelectedHotelId().isEmpty()) {
            hotel = hotelRepo.findById(req.getSelectedHotelId()).orElseThrow(() -> new ResourceNotFoundException("Hotel", req.getSelectedHotelId()));
        }

        List<Place> selectedPlaces = new ArrayList<>();
        if (req.getSelectedPlaceIds() != null) {
            for (String pid : req.getSelectedPlaceIds()) {
                placeRepo.findById(pid).ifPresent(selectedPlaces::add);
            }
        }

        int hotelCost = hotel != null ? (int) Math.ceil((double) travelers / hotel.getMaxOccupancy()) * hotel.getPricePerNight() * days : 0;
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
        res.setTripVibe(vibeService.calculateVibe(selectedPlaces));
        res.setWeatherForecast(weatherSimService.getSimulatedWeather(city));
        res.setTripScore(tripScoringService.scoreTrip(hotel, selectedPlaces, Collections.emptyList(), req.getBudget(), totalCost, days));
        return res;
    }

    private TripResponse handleSmartPlanning(TripRequest req, String city) {
        // 1. Generate Main Plan
        TripResponse mainPlan = calculatePlanInternal(req, city);

        // 2. Generate Shadow Plans for AI Verdict
        if (mainPlan.getHotel() != null) {
            try {
                TripResponse eco = generateTierPlan(req, city, (int)(req.getBudget() * 0.7));
                TripResponse elite = generateTierPlan(req, city, (int)(req.getBudget() * 1.5));
                List<TripResponse> plansForAi = List.of(eco, mainPlan, elite);
                
                // Testing Log: Verify shadow data before AI analysis
                System.out.println("AI Shadow Analysis: Eco Score=" + eco.getTripScore() + " | Main Score=" + mainPlan.getTripScore() + " | Elite Score=" + elite.getTripScore());
                
                mainPlan.setAiRecommendation(llmService.getRecommendation(city, req.getDays(), req.getBudget(), plansForAi));
            } catch (Exception e) {
                System.err.println("Shadow Plan Error: " + e.getMessage());
            }
        }
        return mainPlan;
    }

    private TripResponse generateTierPlan(TripRequest req, String city, int budget) {
        TripRequest tierReq = new TripRequest();
        tierReq.setCity(city);
        tierReq.setBudget(budget);
        tierReq.setDays(req.getDays());
        tierReq.setTravelers(req.getTravelers());
        tierReq.setFoodType(req.getFoodType());
        tierReq.setTravelDate(req.getTravelDate());
        tierReq.setManualFoodBudget(req.getManualFoodBudget());
        tierReq.setDietPreference(req.getDietPreference());
        tierReq.setCuisinePreference(req.getCuisinePreference());
        return calculatePlanInternal(tierReq, city);
    }

    private TripResponse calculatePlanInternal(TripRequest req, String city) {
        int budget = req.getBudget();
        int days = req.getDays();
        int travelers = req.getTravelers();
        double seasonMultiplier = budgetService.getSeasonMultiplier(req.getTravelDate());

        List<Hotel> hotels = hotelRepo.findByCityIgnoreCase(city);
        List<Place> places = placeRepo.findByCityIgnoreCase(city);

        if (hotels.isEmpty()) {
            throw new CityNotFoundException(city);
        }

        BudgetBreakdown bb = budgetService.distribute(budget, days, req.getFoodType(), req.getTravelDate());
        int foodCostPerDay = budgetService.getFoodCostPerDay(req.getFoodType(), req.getManualFoodBudget());
        int foodCost = foodCostPerDay * travelers * days;

        Hotel bestHotel = hotelScoringService.selectBestHotel(hotels, bb.getHotelBudget(), days);
        int roomsNeeded = (int) Math.ceil((double) travelers / bestHotel.getMaxOccupancy());
        int hotelCost = (int)(roomsNeeded * bestHotel.getPricePerNight() * days * seasonMultiplier);

        List<Place> selectedPlaces = new ArrayList<>();
        Map<String, Object> transportInfo = new HashMap<>();
        int transportCost = 300 * days;
        int placesCost = 0;
        List<Restaurant> restaurants = Collections.emptyList();
        int restaurantCost = 0;

        if (!places.isEmpty()) {
            int remainingForPlaces = (budget - (hotelCost + foodCost)) / travelers;
            selectedPlaces = knapsackService.optimizePlaces(places, Math.max(0, remainingForPlaces), days);
            selectedPlaces = proximityService.optimizeRouteOrder(bestHotel, selectedPlaces);
            
            double dist = transportService.calculateTotalDistance(bestHotel, selectedPlaces);
            transportInfo = transportService.calculateTransportCost(city, dist, days, travelers);
            transportCost = (int) transportInfo.getOrDefault("totalCost", transportCost);
            
            restaurants = restaurantService.recommendRestaurants(city, bb.getFoodBudget(), days, travelers, req.getDietPreference(), req.getCuisinePreference());
            restaurantCost = restaurantService.calculateRestaurantCost(restaurants, days, travelers);

            // Restaurant cost flex logic
            if (!restaurants.isEmpty() && restaurantCost <= (bb.getFoodBudget() * 2 + 2000)) {
                foodCost = restaurantCost;
            } else if (!restaurants.isEmpty() && (hotelCost + restaurantCost + transportCost) < budget) {
                foodCost = restaurantCost;
            } else {
                restaurants = Collections.emptyList();
                restaurantCost = foodCost;
            }

            placesCost = selectedPlaces.stream().mapToInt(Place::getEntryFee).sum() * travelers;
        }

        // Image Hydration
        hydrateImages(bestHotel, selectedPlaces, restaurants, city);

        int totalCost = hotelCost + foodCost + transportCost + placesCost;
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
        res.setRestaurants(restaurants);
        res.setRestaurantCost(restaurantCost);
        res.setTransportMode((String) transportInfo.getOrDefault("mode", "cab"));
        res.setTotalDistanceKm(Math.round((Double) transportInfo.getOrDefault("distance", 0.0) * 100.0) / 100.0);
        res.setCityImageUrl(imageService.fetchImageForLocation(city + " travel"));
        
        bb.setHotelActual(hotelCost);
        bb.setFoodActual(foodCost);
        bb.setTravelActual(transportCost);
        bb.setPlacesActual(placesCost);
        bb.setSavings(budget - totalCost);
        res.setBudgetBreakdown(bb);

        res.setDayWisePlanDetailed(itineraryService.buildDetailedDayPlan(bestHotel, selectedPlaces, restaurants, days, res.getTransportMode(), res.getTotalDistanceKm() / Math.max(1, days), null));
        res.setSuggestionMessage(suggestionService.buildSuggestion(hotels, places, budget, days, req.getFoodType(), req.getManualFoodBudget(), totalCost));
        res.setTripScore(tripScoringService.scoreTrip(bestHotel, selectedPlaces, restaurants, budget, totalCost, days));
        res.setTripVibe(vibeService.calculateVibe(selectedPlaces));
        res.setWeatherForecast(weatherSimService.getSimulatedWeather(city));
        res.setSeasonInfo(budgetService.getSeasonLabel(req.getTravelDate()));
        res.setSeasonMultiplier(seasonMultiplier);
        
        return res;
    }

    private void hydrateImages(Hotel hotel, List<Place> places, List<Restaurant> rests, String city) {
        if (hotel != null && isPlaceholder(hotel.getImageUrl())) {
            hotel.setImageUrl(imageService.fetchImageForLocation(hotel.getName() + " " + city + " hotel"));
        }
        if (places != null) {
            places.parallelStream().forEach(p -> {
                if (isPlaceholder(p.getImageUrl())) {
                    p.setImageUrl(imageService.fetchImageForLocation(p.getName() + " " + city));
                }
            });
        }
        if (rests != null) {
            rests.parallelStream().forEach(r -> {
                if (isPlaceholder(r.getImageUrl())) {
                    r.setImageUrl(imageService.fetchImageForLocation(r.getName() + " " + city + " restaurant"));
                }
            });
        }
    }

    private boolean isPlaceholder(String url) {
        return url == null || url.isEmpty() || url.contains("unsplash") || url.startsWith("/images/") || url.contains("null");
    }
}
