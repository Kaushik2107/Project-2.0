package com.kaushik.travelplan.service;

import com.kaushik.travelplan.dto.GroupCostBreakdown;
import com.kaushik.travelplan.entity.Hotel;
import com.kaushik.travelplan.entity.Place;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * FEATURE 15: Group Trip Service
 * Smart cost splitting for N travelers.
 *
 * KEY INSIGHT: When friends travel together, costs are NOT simply multiplied by N.
 * - Hotel: Shared rooms (2 per room), so rooms = ceil(N/2). Much cheaper per person!
 * - Food: Scales linearly (each person eats their own food)
 * - Transport: Shared vehicle. A cab fits 4, auto fits 3. Cost = vehicles_needed * base_cost
 * - Places: Entry fee is per person, scales linearly
 *
 * Example: Solo trip = ₹15,000. Two friends = NOT ₹30,000!
 * Hotel stays same (₹6000 → ₹3000 each), transport shared (₹1500 → ₹750 each)
 * Real cost: ~₹22,000 total = ₹11,000 per person (27% savings!)
 */
@Service
public class GroupTripService {

    /**
     * Calculate group cost breakdown with smart sharing logic.
     */
    public GroupCostBreakdown calculateGroupCost(Hotel hotel, List<Place> places,
                                                  int days, int travelers,
                                                  int foodCostPerDay,
                                                  Map<String, Object> transportInfo) {

        GroupCostBreakdown gcb = new GroupCostBreakdown();
        gcb.setTravelers(travelers);

        // ── HOTEL: Shared rooms ──
        // Max occupancy per room (typically 2, some hotels allow 3)
        int maxOccupancy = hotel != null ? hotel.getMaxOccupancy() : 2;
        int roomsNeeded = (int) Math.ceil((double) travelers / maxOccupancy);
        int pricePerNight = hotel != null ? hotel.getPricePerNight() : 0;

        int hotelTotal = roomsNeeded * pricePerNight * days;
        int hotelPerPerson = hotelTotal / travelers;

        gcb.setRoomsNeeded(roomsNeeded);
        gcb.setHotelCostTotal(hotelTotal);
        gcb.setHotelCostPerPerson(hotelPerPerson);

        // ── FOOD: Per person (everyone eats!) ──
        int foodTotal = foodCostPerDay * travelers * days;
        int foodPerPerson = foodCostPerDay * days;

        gcb.setFoodCostTotal(foodTotal);
        gcb.setFoodCostPerPerson(foodPerPerson);

        // ── TRANSPORT: Shared vehicles ──
        int transportTotal = 0;
        if (transportInfo != null && transportInfo.containsKey("totalCost")) {
            transportTotal = (int) transportInfo.get("totalCost");
        }
        int transportPerPerson = travelers > 0 ? transportTotal / travelers : 0;

        gcb.setTransportCostTotal(transportTotal);
        gcb.setTransportCostPerPerson(transportPerPerson);

        // ── PLACES: Entry fee per person ──
        int singlePlacesCost = places.stream().mapToInt(Place::getEntryFee).sum();
        int placesTotal = singlePlacesCost * travelers;
        int placesPerPerson = singlePlacesCost;

        gcb.setPlacesCostTotal(placesTotal);
        gcb.setPlacesCostPerPerson(placesPerPerson);

        // ── TOTALS ──
        int totalCost = hotelTotal + foodTotal + transportTotal + placesTotal;
        int perPersonCost = totalCost / travelers;

        gcb.setTotalCost(totalCost);
        gcb.setPerPersonCost(perPersonCost);

        // ── SAVINGS vs solo ──
        // Calculate what it would cost if each person went solo
        int soloHotel = pricePerNight * days;
        int soloFood = foodCostPerDay * days;
        int soloTransport = transportTotal / Math.max(1, (int) Math.ceil((double) travelers / 4)); // 1 vehicle for solo
        int soloPlaces = singlePlacesCost;
        int soloCost = soloHotel + soloFood + soloTransport + soloPlaces;

        int savingsPerPerson = soloCost - perPersonCost;
        gcb.setSavingsVsSolo(Math.max(0, savingsPerPerson));

        if (savingsPerPerson > 0) {
            int savingsPercent = (int)((double) savingsPerPerson / soloCost * 100);
            gcb.setSavingsMessage("🎉 Each person saves ₹" + savingsPerPerson
                    + " (" + savingsPercent + "% cheaper) by traveling together!");
        } else {
            gcb.setSavingsMessage("💡 Group size doesn't significantly reduce per-person cost for this trip.");
        }

        return gcb;
    }
}
