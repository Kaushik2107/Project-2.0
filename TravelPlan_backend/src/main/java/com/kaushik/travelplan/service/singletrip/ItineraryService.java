package com.kaushik.travelplan.service.singletrip;

import com.kaushik.travelplan.dto.DayPlan;
import com.kaushik.travelplan.dto.TimelineStep;
import com.kaushik.travelplan.entity.Hotel;
import com.kaushik.travelplan.entity.Place;
import com.kaushik.travelplan.entity.Restaurant;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * SERVICE 4: Itinerary Builder
 * Generates both structured DayPlan objects and legacy string-based plans.
 */
@Service
public class ItineraryService {

    /**
     * Build structured day-wise plan with places, meals, and transport.
     */
    public List<DayPlan> buildDetailedDayPlan(Hotel hotel, List<Place> places,
                                               List<Restaurant> restaurants, int days,
                                               String transportMode, double dailyDistanceKm,
                                               String arrivalDetails) {
        List<DayPlan> plans = new ArrayList<>();

        // Separate restaurants by meal type
        List<Restaurant> breakfastOptions = filterByMealType(restaurants, "breakfast");
        List<Restaurant> lunchOptions = filterByMealType(restaurants, "lunch");
        List<Restaurant> dinnerOptions = filterByMealType(restaurants, "dinner");

        for (int day = 1; day <= days; day++) {
            DayPlan dp = new DayPlan();
            dp.setDay(day);
            dp.setTransportMode(transportMode != null ? transportMode : "cab");
            dp.setDistanceKm(Math.round(dailyDistanceKm * 100.0) / 100.0);

            // Hotel action
            if (day == 1) {
                dp.setTitle("Day " + day + " — Arrival & Exploration");
                String checkIn = "Check-in at " + (hotel != null ? hotel.getName() : "Hotel");
                if (arrivalDetails != null) {
                    dp.setHotelAction(arrivalDetails + " => " + checkIn);
                } else {
                    dp.setHotelAction(checkIn);
                }
            } else if (day == days) {
                dp.setTitle("Day " + day + " — Final Day & Departure");
                dp.setHotelAction("Check-out");
            } else {
                dp.setTitle("Day " + day + " — Sightseeing");
                dp.setHotelAction("Stay");
            }

            // Distribute places fairly across all days using round-robin
            List<Place> dayPlaces = new ArrayList<>();
            for (int i = 0; i < places.size(); i++) {
                if (i % days == (day - 1)) {
                    dayPlaces.add(places.get(i));
                }
            }
            dp.setPlaces(dayPlaces);

            // Assign meals (rotate through available restaurants)
            if (!breakfastOptions.isEmpty()) {
                dp.setBreakfast(breakfastOptions.get((day - 1) % breakfastOptions.size()));
            }
            if (!lunchOptions.isEmpty()) {
                dp.setLunch(lunchOptions.get((day - 1) % lunchOptions.size()));
            }
            if (!dinnerOptions.isEmpty()) {
                dp.setDinner(dinnerOptions.get((day - 1) % dinnerOptions.size()));
            }

            // Dynamically generate day title based on place categories and calculate duration
            if (day > 1 && day < days && !dayPlaces.isEmpty()) {
                long beachCount = dayPlaces.stream().filter(p -> p.getCategory() != null && p.getCategory().toLowerCase().contains("beach")).count();
                long historyCount = dayPlaces.stream().filter(p -> p.getCategory() != null && (p.getCategory().toLowerCase().contains("fort") || p.getCategory().toLowerCase().contains("museum") || p.getCategory().toLowerCase().contains("monument"))).count();
                long religionCount = dayPlaces.stream().filter(p -> p.getCategory() != null && (p.getCategory().toLowerCase().contains("temple") || p.getCategory().toLowerCase().contains("church") || p.getCategory().toLowerCase().contains("mosque"))).count();
                
                if (beachCount >= 2) dp.setTitle("Day " + day + " — Sun, Sand & Beach Relaxation");
                else if (historyCount >= 2) dp.setTitle("Day " + day + " — Heritage Walk & Cultural Exploration");
                else if (religionCount >= 2) dp.setTitle("Day " + day + " — Spiritual Journey & Architecture");
                else if (dayPlaces.size() >= 3) dp.setTitle("Day " + day + " — Action-Packed City Tour");
                else dp.setTitle("Day " + day + " — Local Sightseeing");
            }

            // Generate specific notes based on duration and indoor/outdoor status
            if (dayPlaces.isEmpty()) {
                dp.setNotes("Leisure day — relax at the hotel or explore nearby streets at your own pace.");
            } else {
                int totalMinutes = dayPlaces.stream().mapToInt(Place::getDurationMinutes).sum();
                boolean mostlyOutdoor = dayPlaces.stream().filter(p -> !p.isIndoor()).count() > dayPlaces.size() / 2;
                
                String pacing = totalMinutes > 240 ? "A busy day ahead with " + (totalMinutes/60) + " hours of planned activities." : "A relaxed schedule with " + (totalMinutes/60) + " hours of activities.";
                String gear = mostlyOutdoor ? " Wear comfortable walking shoes and carry sunscreen or an umbrella." : " Most of today's activities are indoors.";
                dp.setNotes(pacing + gear);
            }

            // --- GENERATE TIMELINE (Expert/Expert format) ---
            dp.setTimeline(generateTimeline(hotel, dayPlaces, dp.getBreakfast(), dp.getLunch(), dp.getDinner(), day, days));

            plans.add(dp);
        }

        return plans;
    }

    private List<TimelineStep> generateTimeline(Hotel hotel, List<Place> places, Restaurant breakfast, Restaurant lunch, Restaurant dinner, int day, int totalDays) {
        List<TimelineStep> timeline = new ArrayList<>();
        int currentHour = 8;
        int currentMin = 0;

        // 1. Breakfast (08:00 AM)
        if (breakfast != null) {
            Map<String, Object> details = new HashMap<>();
            details.put("placeName", breakfast.getName());
            details.put("location", breakfast.getCity());
            details.put("description", "Enjoy a fresh " + breakfast.getCuisine() + " breakfast.");
            details.put("costForTwo", "₹" + (breakfast.getPricePerMeal() * 2));
            details.put("imageQuery", breakfast.getName() + " food");
            timeline.add(createStep("08:00 AM", "09:00 AM", "Breakfast", "Breakfast at " + breakfast.getName(), details));
            currentHour = 9;
        }

        // 2. Travel to first place
        if (!places.isEmpty()) {
            Map<String, Object> details = new HashMap<>();
            details.put("mode", "Auto / Cab");
            details.put("duration", "30 mins");
            details.put("cost", "₹150");
            details.put("tips", "Best for city exploration");
            timeline.add(createStep("09:00 AM", "09:30 AM", "Travel", "Travel to " + places.get(0).getName(), details));
            currentHour = 9; currentMin = 30;

            // 3. First Place
            Place p1 = places.get(0);
            String endTime = formatTime(currentHour, currentMin + p1.getDurationMinutes());
            Map<String, Object> detailsPlace = new HashMap<>();
            detailsPlace.put("placeName", p1.getName());
            detailsPlace.put("location", p1.getCity());
            detailsPlace.put("description", "A must-visit spot.");
            detailsPlace.put("entryFee", "₹" + p1.getEntryFee());
            detailsPlace.put("imageQuery", p1.getName() + " morning view");
            timeline.add(createStep("09:30 AM", endTime, "Visit", "Explore " + p1.getName(), detailsPlace));
            currentHour = 11; currentMin = 30; // reset
        }

        // 4. Travel to Lunch
        Map<String, Object> travelLunch = new HashMap<>();
        travelLunch.put("mode", "Cab");
        travelLunch.put("duration", "60 mins");
        travelLunch.put("cost", "₹200");
        timeline.add(createStep("11:30 AM", "12:30 PM", "Travel", "Travel to Lunch", travelLunch));

        // 5. Lunch
        if (lunch != null) {
            Map<String, Object> lunchDetails = new HashMap<>();
            lunchDetails.put("placeName", lunch.getName());
            lunchDetails.put("location", lunch.getCity());
            lunchDetails.put("famousFor", lunch.getCuisine());
            lunchDetails.put("costForTwo", "₹" + (lunch.getPricePerMeal() * 2));
            lunchDetails.put("imageQuery", lunch.getName() + " meal");
            timeline.add(createStep("12:30 PM", "01:30 PM", "Lunch", "Lunch at " + lunch.getName(), lunchDetails));
        }

        // 6. Second Place (Afternoon)
        if (places.size() > 1) {
            Place p2 = places.get(1);
            Map<String, Object> detailsPlace2 = new HashMap<>();
            detailsPlace2.put("placeName", p2.getName());
            detailsPlace2.put("location", p2.getCity());
            detailsPlace2.put("description", "Absorb the local culture.");
            detailsPlace2.put("entryFee", "₹" + p2.getEntryFee());
            detailsPlace2.put("imageQuery", p2.getName() + " view");
            timeline.add(createStep("02:00 PM", "04:00 PM", "Visit", "Visit " + p2.getName(), detailsPlace2));
        }

        // 7. Evening Activity
        Map<String, Object> evening = new HashMap<>();
        evening.put("placeName", "Local Markets");
        evening.put("location", "City Center");
        evening.put("description", "Experience the vibrant local life.");
        evening.put("entryFee", "Free");
        evening.put("imageQuery", "local market evening vibe");
        timeline.add(createStep("05:00 PM", "06:30 PM", "Visit", "Evening Walk / Market Exploration", evening));

        // 8. Dinner
        if (dinner != null) {
            Map<String, Object> dinnerDetails = new HashMap<>();
            dinnerDetails.put("placeName", dinner.getName());
            dinnerDetails.put("location", dinner.getCity());
            dinnerDetails.put("costForTwo", "₹" + (dinner.getPricePerMeal() * 2));
            dinnerDetails.put("imageQuery", dinner.getName() + " night view");
            timeline.add(createStep("07:30 PM", "09:00 PM", "Dinner", "Dinner at " + dinner.getName(), dinnerDetails));
        }

        // 9. Stay
        if (hotel != null) {
            String title = (day == 1) ? "Check-in Hotel" : "Return to Hotel";
            Map<String, Object> stay = new HashMap<>();
            stay.put("hotelName", hotel.getName());
            stay.put("location", hotel.getCity());
            stay.put("pricePerNight", "₹" + hotel.getPricePerNight());
            stay.put("rating", String.valueOf(hotel.getRating()));
            stay.put("imageQuery", hotel.getName() + " room");
            timeline.add(createStep("09:30 PM", (day == totalDays ? "Departure" : "Next Day"), "Stay", title, stay));
        }

        return timeline;
    }

    private TimelineStep createStep(String start, String end, String type, String title, Map<String, Object> details) {
        return new TimelineStep(start, end, type, title, details);
    }

    private String formatTime(int hour, int min) {
        int h = hour + (min / 60);
        int m = min % 60;
        String ampm = h >= 12 ? "PM" : "AM";
        int dh = h > 12 ? h - 12 : (h == 0 ? 12 : h);
        return String.format("%02d:%02d %s", dh, m, ampm);
    }

    private void advanceTime(int h, int m, int add) {
        // simplified
    }

    /**
     * Build legacy string-based day plan (backward compatible) with precise timeline estimates.
     */
    public List<String> buildDayWisePlan(Hotel hotel, List<Place> places, int days) {
        List<String> plan = new ArrayList<>();

        for (int day = 1; day <= days; day++) {
            StringBuilder sb = new StringBuilder();
            sb.append("Day ").append(day).append(": ");

            int currentTimeHour = 10; // Start the day at 10:00 AM
            int currentTimeMin = 0;

            if (day == 1 && hotel != null) {
                sb.append("[12:00 PM] Hotel Check-in (").append(hotel.getName()).append(") | ");
                currentTimeHour = 14; // Start sightseeing afterwards if day 1
            }

            List<Place> dayPlaces = new ArrayList<>();
            for (int i = 0; i < places.size(); i++) {
                if (i % days == (day - 1)) {
                    dayPlaces.add(places.get(i));
                }
            }

            int added = 0;
            for (Place p : dayPlaces) {
                if (added > 0) sb.append(" | ");
                
                String amPm = currentTimeHour >= 12 ? "PM" : "AM";
                int displayHour = currentTimeHour > 12 ? currentTimeHour - 12 : (currentTimeHour == 0 ? 12 : currentTimeHour);
                String timeStr = String.format("[%02d:%02d %s]", displayHour, currentTimeMin, amPm);
                
                sb.append(timeStr).append(" ").append(p.getName()).append(" (approx. ").append(p.getDurationMinutes()).append(" mins)");
                
                // Advance time
                currentTimeMin += p.getDurationMinutes() + 30; // 30 mins travel/buffer
                currentTimeHour += currentTimeMin / 60;
                currentTimeMin %= 60;

                added++;
            }

            if (day == days && hotel != null) {
                sb.append(" | [11:00 AM] Hotel Check-out");
            }

            if (added == 0 && !(day == 1 && hotel != null) && !(day == days && hotel != null)) {
                sb.append("Leisure time - Local sightseeing, shopping or relaxing.");
            }

            plan.add(sb.toString());
        }

        return plan;
    }

    private List<Restaurant> filterByMealType(List<Restaurant> restaurants, String mealType) {
        if (restaurants == null || restaurants.isEmpty()) return Collections.emptyList();
        List<Restaurant> filtered = new ArrayList<>();
        for (Restaurant r : restaurants) {
            if (r.getMealType() != null &&
                (r.getMealType().equalsIgnoreCase(mealType) || r.getMealType().equalsIgnoreCase("all"))) {
                filtered.add(r);
            }
        }
        // If no specific meal type found, return all restaurants
        return filtered.isEmpty() ? restaurants : filtered;
    }
}

