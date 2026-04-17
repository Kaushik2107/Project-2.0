package com.kaushik.travelplan.service.grouptrip;

import com.kaushik.travelplan.dto.TripRequest;
import com.kaushik.travelplan.entity.Group;
import com.kaushik.travelplan.entity.GroupTrip;
import com.kaushik.travelplan.exception.ResourceNotFoundException;
import com.kaushik.travelplan.repository.GroupRepository;
import com.kaushik.travelplan.repository.GroupTripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class GroupCollaborationService {

    @Autowired private GroupRepository groupRepo;
    @Autowired private GroupTripRepository groupTripRepo;
    @Autowired private com.kaushik.travelplan.repository.UserRepository userRepo;

    public Group createGroup(String name, String adminEmail) {
        Group group = new Group();
        group.setName(name);
        group.setAdminEmail(adminEmail);
        group.getMemberEmails().add(adminEmail);
        return groupRepo.save(group);
    }

    public Group addMember(String groupId, String email) {
        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", groupId));
        
        // Validation 1: Check if user exists in the system
        userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));

        // Validation 2: Check if user is already a member
        if (group.getMemberEmails().contains(email)) {
            throw new com.kaushik.travelplan.exception.InvalidRequestException("member already exists in this group");
        }

        group.getMemberEmails().add(email);
        return groupRepo.save(group);
    }

    public Group removeMember(String groupId, String email) {
        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", groupId));
        group.getMemberEmails().remove(email);
        return groupRepo.save(group);
    }

    public List<Group> getMyGroups(String email) {
        return groupRepo.findByMemberEmails(email);
    }

    public GroupTrip initiateTrip(String groupId, String destination, int days) {
        GroupTrip gt = new GroupTrip();
        gt.setGroupId(groupId);
        gt.setDestination(destination);
        gt.setDays(days);
        gt.setStatus("COLLECTING");
        return groupTripRepo.save(gt);
    }

    public GroupTrip submitResponse(String tripId, String userEmail, TripRequest req) {
        GroupTrip gt = groupTripRepo.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("GroupTrip", tripId));
        
        // Remove existing response if any
        gt.getResponses().removeIf(r -> r.getEmail().equals(userEmail));
        
        // Add new response
        GroupTrip.MemberResponse mr = new GroupTrip.MemberResponse();
        mr.setEmail(userEmail);
        mr.setRequest(req);
        gt.getResponses().add(mr);
        
        gt.getSubmittedEmails().add(userEmail);
        return groupTripRepo.save(gt);
    }

    public Optional<GroupTrip> getActiveTrip(String groupId) {
        return groupTripRepo.findByGroupIdAndStatus(groupId, "COLLECTING");
    }
    
    public GroupTrip getTrip(String tripId) {
        return groupTripRepo.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("GroupTrip", tripId));
    }
}
