package com.kaushik.travelplan.service.singletrip;

import com.kaushik.travelplan.dto.DayPlan;
import com.kaushik.travelplan.dto.TimelineStep;
import com.kaushik.travelplan.entity.Hotel;
import com.kaushik.travelplan.entity.Place;
import com.kaushik.travelplan.entity.Restaurant;
import com.kaushik.travelplan.util.HaversineUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * SERVICE 4: Itinerary Builder (Optimized)
 * Generates structured, proximity-aware day plans with dynamic timelines.
 */
@Service
public class ItineraryService {

    private static final int START_HOUR = 9;
    private static final int AVG_SPEED_KMH = 30; // Average city travel speed
    private static final int TRAVEL_BUFFER_MINS = 15; // Extra time for parking/waiting

    /**
     * Build structured day-wise plan using contiguous path splitting.
     */
    public List<DayPlan> buildDetailedDayPlan(Hotel hotel, List<Place> places,
                                               List<Restaurant> restaurants, int days,
                                               String transportMode, double totalDistanceKm,
                                               String arrivalDetails) {
        List<DayPlan> plans = new ArrayList<>();
        if (days <= 0) return plans;

        // 1. IMPROVED DISTRIBUTION: Contiguous Path Splitting
        // Since 'places' is already optimized by ProximityService, we split it into N segments.
        List<List<Place>> dayAssignments = distributePlacesContiguously(places, days);

        // Separate restaurants by meal type
        List<Restaurant> breakfastOptions = filterByMealType(restaurants, "breakfast");
        List<Restaurant> lunchOptions = filterByMealType(restaurants, "lunch");
        List<Restaurant> dinnerOptions = filterByMealType(restaurants, "dinner");

        for (int day = 1; day <= days; day++) {
            DayPlan dp = new DayPlan();
            dp.setDay(day);
            dp.setTransportMode(transportMode != null ? transportMode : "cab");

            List<Place> dayPlaces = dayAssignments.get(day - 1);
            dp.setPlaces(dayPlaces);

            // Assign meals
            dp.setBreakfast(!breakfastOptions.isEmpty() ? breakfastOptions.get((day - 1) % breakfastOptions.size()) : null);
            dp.setLunch(!lunchOptions.isEmpty() ? lunchOptions.get((day - 1) % lunchOptions.size()) : null);
            dp.setDinner(!dinnerOptions.isEmpty() ? dinnerOptions.get((day - 1) % dinnerOptions.size()) : null);

            // Hotel & Title Logic
            updateDayTitleAndHotelAction(dp, hotel, day, days, arrivalDetails);

            // 2. DYNAMIC TIMELINE GENERATION
            dp.setTimeline(generateDynamicTimeline(hotel, dayPlaces, dp.getBreakfast(), dp.getLunch(), dp.getDinner(), day, days));

            // Calculate daily distance (rough estimate based on total)
            dp.setDistanceKm(Math.round((totalDistanceKm / days) * 100.0) / 100.0);

            // Notes based on workload
            generateDayNotes(dp);

            plans.add(dp);
        }

        return plans;
    }

    private List<List<Place>> distributePlacesContiguously(List<Place> places, int days) {
        List<List<Place>> assignments = new ArrayList<>();
        for (int i = 0; i < days; i++) assignments.add(new ArrayList<>());
        
        if (places.isEmpty()) return assignments;

        // We try to balance based on Visit Duration
        int totalVisitTime = places.stream().mapToInt(Place::getDurationMinutes).sum();
        int targetTimePerDay = totalVisitTime / days;

        int currentDay = 0;
        int currentDayTime = 0;

        for (Place p : places) {
            // If current day is getting too full, and it's not the last day, move to next
            if (currentDay < days - 1 && currentDayTime + (p.getDurationMinutes() / 2) > targetTimePerDay) {
                currentDay++;
                currentDayTime = 0;
            }
            assignments.get(currentDay).add(p);
            currentDayTime += p.getDurationMinutes();
        }
        
        return assignments;
    }

    private static final int MAX_TRAVEL_TIME_MINS = 90; // Cap travel time to prevent coordinate bugs
    private static final int DAY_END_HOUR = 22; // 10:00 PM

    private List<TimelineStep> generateDynamicTimeline(Hotel hotel, List<Place> places, 
                                                       Restaurant b, Restaurant l, Restaurant d, 
                                                       int day, int totalDays) {
        List<TimelineStep> timeline = new ArrayList<>();
        int h = START_HOUR, m = 0;

        TimeResult tr = new TimeResult(h, m);

        // 1. Breakfast
        if (b != null) {
            timeline.add(createMealStep(tr, 60, "Breakfast", b));
        }

        double prevLat = hotel != null ? hotel.getLatitude() : (places.isEmpty() ? 0 : places.get(0).getLatitude());
        double prevLng = hotel != null ? hotel.getLongitude() : (places.isEmpty() ? 0 : places.get(0).getLongitude());

        int midPoint = (places.size() / 2);
        int placeIdx = 0;

        for (Place p : places) {
            // STOP ADDING if we have reached the end of a reasonable day (10 PM)
            if (tr.h >= DAY_END_HOUR) {
                break;
            }

            // Insert Lunch after some activities
            if (placeIdx == midPoint && l != null && tr.h < 15) {
                timeline.add(createMealStep(tr, 60, "Lunch", l));
            }

            // Travel to place
            double dist = HaversineUtil.distanceKm(prevLat, prevLng, p.getLatitude(), p.getLongitude());
            int travelTime = (int)Math.ceil((dist / AVG_SPEED_KMH) * 60) + TRAVEL_BUFFER_MINS;
            
            // SANITY CHECK: Cap travel time to prevent coordinate-related bugs (348 hours etc)
            if (travelTime > MAX_TRAVEL_TIME_MINS) {
                travelTime = MAX_TRAVEL_TIME_MINS;
            }
            
            String travelStart = formatTime(tr.h, tr.m);
            advanceTime(tr, travelTime);
            timeline.add(createTravelStep(travelStart, formatTime(tr.h, tr.m), "Travel to " + p.getName(), dist, travelTime));

            // Visit place
            String visitStart = formatTime(tr.h, tr.m);
            int duration = p.getDurationMinutes();
            advanceTime(tr, duration);
            timeline.add(createVisitStep(visitStart, formatTime(tr.h, tr.m), p));

            prevLat = p.getLatitude();
            prevLng = p.getLongitude();
            placeIdx++;
        }

        // 3. Dinner (if not already happened and not too late)
        if (d != null && tr.h < DAY_END_HOUR) {
            if (tr.h < 19) { tr.h = 19; tr.m = 0; }
            timeline.add(createMealStep(tr, 60, "Dinner", d));
        }

        // 4. Return to Hotel
        if (hotel != null) {
            String returnStart = formatTime(tr.h, tr.m);
            timeline.add(createStep(returnStart, (day == totalDays ? "End" : "Night"), "Stay", 
                (day == 1 ? "Check-in Hotel" : "Return to Hotel"), new HashMap<>()));
        }

        return timeline;
    }

    private static class TimeResult {
        int h, m;
        TimeResult(int h, int m) { this.h = h; this.m = m; }
    }

    private void advanceTime(TimeResult tr, int addMins) {
        tr.m += addMins;
        tr.h += tr.m / 60;
        tr.m %= 60;
        // Cap hour at 30 to prevent insane overflows leaking if logic fails
        if (tr.h > 30) tr.h = 30; 
    }

    private TimelineStep createMealStep(TimeResult tr, int duration, String type, Restaurant r) {
        Map<String, Object> details = new HashMap<>();
        details.put("placeName", r.getName());
        details.put("cuisine", r.getCuisine());
        details.put("costForTwo", "₹" + (r.getPricePerMeal() * 2));
        String start = formatTime(tr.h, tr.m);
        advanceTime(tr, duration);
        return createStep(start, formatTime(tr.h, tr.m), type, type + " at " + r.getName(), details);
    }

    private TimelineStep createTravelStep(String start, String end, String title, double dist, int mins) {
        Map<String, Object> details = new HashMap<>();
        details.put("distance", Math.round(dist * 10.0) / 10.0 + " km");
        details.put("duration", mins + " mins");
        details.put("mode", "Cab/Auto");
        return createStep(start, end, "Travel", title, details);
    }

    private TimelineStep createVisitStep(String start, String end, Place p) {
        Map<String, Object> details = new HashMap<>();
        details.put("placeName", p.getName());
        details.put("category", p.getCategory());
        details.put("entryFee", "₹" + p.getEntryFee());
        return createStep(start, end, "Visit", "Explore " + p.getName(), details);
    }

    private void updateDayTitleAndHotelAction(DayPlan dp, Hotel hotel, int day, int days, String arrival) {
        if (day == 1) {
            dp.setTitle("Day 1: Arrival & Welcome");
            dp.setHotelAction(arrival != null ? arrival + " & Check-in" : "Check-in at " + (hotel != null ? hotel.getName() : "Hotel"));
        } else if (day == days) {
            dp.setTitle("Day " + day + ": Memories & Departure");
            dp.setHotelAction("Morning check-out");
        } else {
            dp.setTitle("Day " + day + ": Deep Exploration");
            dp.setHotelAction("Stay");
        }
    }

    private void generateDayNotes(DayPlan dp) {
        if (dp.getPlaces().isEmpty()) {
            dp.setNotes("A relaxed day for leisure shopping or hotel amenities.");
            return;
        }
        int count = dp.getPlaces().size();
        String vibe = count > 5 ? "Packed day!" : (count > 2 ? "Balanced pace." : "Very relaxed.");
        dp.setNotes(vibe + " You'll be visiting " + count + " major attractions today. Wear walking shoes!");
    }

    private String formatTime(int h, int m) {
        // Handle overflow cases to stay within standard clock bounds
        int displayH = h % 24; 
        String amp = displayH >= 12 ? "PM" : "AM";
        int dh = displayH > 12 ? displayH - 12 : (displayH == 0 ? 12 : displayH);
        return String.format("%02d:%02d %s", dh, m, amp);
    }

    private TimelineStep createStep(String s, String e, String type, String title, Map<String, Object> d) {
        return new TimelineStep(s, e, type, title, d);
    }

    public List<String> buildDayWisePlan(Hotel hotel, List<Place> places, int days) {
        List<String> plan = new ArrayList<>();
        List<List<Place>> dayAssignments = distributePlacesContiguously(places, days);

        for (int i = 0; i < days; i++) {
            StringBuilder sb = new StringBuilder("Day " + (i + 1) + ": ");
            List<Place> dp = dayAssignments.get(i);
            if (dp.isEmpty()) sb.append("Leisure time.");
            else {
                for (int j = 0; j < dp.size(); j++) {
                    sb.append(dp.get(j).getName()).append(j < dp.size() - 1 ? " | " : "");
                }
            }
            plan.add(sb.toString());
        }
        return plan;
    }

    private List<Restaurant> filterByMealType(List<Restaurant> restaurants, String mealType) {
        if (restaurants == null || restaurants.isEmpty()) return Collections.emptyList();
        List<Restaurant> filtered = new ArrayList<>();
        for (Restaurant r : restaurants) {
            if (r.getMealType() != null && (r.getMealType().equalsIgnoreCase(mealType) || r.getMealType().equalsIgnoreCase("all"))) {
                filtered.add(r);
            }
        }
        return filtered.isEmpty() ? restaurants : filtered;
    }
}
