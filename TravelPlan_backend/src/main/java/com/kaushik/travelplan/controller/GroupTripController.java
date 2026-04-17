package com.kaushik.travelplan.controller;

import com.kaushik.travelplan.dto.TripRequest;
import com.kaushik.travelplan.dto.TripResponse;
import com.kaushik.travelplan.entity.Group;
import com.kaushik.travelplan.entity.GroupTrip;
import com.kaushik.travelplan.service.grouptrip.GroupAggregationService;
import com.kaushik.travelplan.service.grouptrip.GroupCollaborationService;
import com.kaushik.travelplan.service.singletrip.TripPlannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173"})
public class GroupTripController {

    @Autowired private GroupCollaborationService groupService;
    @Autowired private GroupAggregationService aggregationService;
    @Autowired private TripPlannerService tripPlannerService;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping
    public Group createGroup(@RequestBody Map<String, String> body) {
        return groupService.createGroup(body.get("name"), getCurrentUserEmail());
    }

    @GetMapping("/my")
    public List<Group> getMyGroups() {
        return groupService.getMyGroups(getCurrentUserEmail());
    }

    @PostMapping("/{groupId}/members")
    public Group addMember(@PathVariable String groupId, @RequestBody Map<String, String> body) {
        return groupService.addMember(groupId, body.get("email"));
    }

    @PostMapping("/{groupId}/trips")
    public GroupTrip initiateTrip(@PathVariable String groupId, @RequestBody Map<String, Object> body) {
        return groupService.initiateTrip(groupId, (String) body.get("destination"), (Integer) body.get("days"));
    }

    @GetMapping("/{groupId}/active-trip")
    public GroupTrip getActiveTrip(@PathVariable String groupId) {
        return groupService.getActiveTrip(groupId).orElse(null);
    }

    @PostMapping("/trips/{tripId}/responses")
    public GroupTrip submitResponse(@PathVariable String tripId, @RequestBody TripRequest req) {
        return groupService.submitResponse(tripId, getCurrentUserEmail(), req);
    }

    @GetMapping("/trips/{tripId}")
    public GroupTrip getTrip(@PathVariable String tripId) {
        return groupService.getTrip(tripId);
    }

    @PostMapping("/trips/{tripId}/finalize")
    public TripResponse finalizeTrip(@PathVariable String tripId) {
        GroupTrip gt = groupService.getTrip(tripId);
        TripRequest consensus = aggregationService.aggregatePreferences(gt);
        
        // Generate the final trip based on aggregated consensus
        TripResponse res = tripPlannerService.generatePlan(consensus);
        
        // Update status
        gt.setStatus("FINALIZED");
        // Save would happen here if we tracked results in GroupTrip entity, 
        // but for now we just return the generated response.
        
        return res;
    }
}
