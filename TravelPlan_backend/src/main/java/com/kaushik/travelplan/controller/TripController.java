package com.kaushik.travelplan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaushik.travelplan.dto.TripRequest;
import com.kaushik.travelplan.dto.TripResponse;
import com.kaushik.travelplan.service.TripHistoryService;
import com.kaushik.travelplan.service.TripPlannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plan")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173"})
public class TripController {

    @Autowired
    private TripPlannerService tripPlannerService;

    @Autowired
    private TripHistoryService tripHistoryService;

    @PostMapping
    public TripResponse generate(@RequestBody TripRequest req) {
        TripResponse res = tripPlannerService.generatePlan(req);

        // Save history mapping to authenticated user
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                String username = auth.getName();
                ObjectMapper mapper = new ObjectMapper();
                mapper.findAndRegisterModules(); // just in case for datetimes
                String summary = mapper.writeValueAsString(res);

                String hotelName = res.getHotel() != null ? res.getHotel().getName() : "None";
                tripHistoryService.saveTripHistory(
                        username, req.getCity(), req.getBudget(), req.getDays(), req.getTravelers(),
                        req.getFoodType(), res.getTotalCost(), res.getPerPersonCost(),
                        hotelName, summary
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }
}