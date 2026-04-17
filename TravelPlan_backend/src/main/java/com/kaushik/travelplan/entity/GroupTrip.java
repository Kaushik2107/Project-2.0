package com.kaushik.travelplan.entity;

import com.kaushik.travelplan.dto.TripRequest;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Document(collection = "group_trips")
public class GroupTrip {
    @Id
    private String id;
    private String groupId;
    private String destination;
    private int days;
    private String status; // COLLECTING, FINALIZED
    
    @Data
    public static class MemberResponse {
        private String email;
        private TripRequest request;
    }

    // List of responses from members
    private List<MemberResponse> responses = new ArrayList<>();
    
    // Set of emails who have submitted (for quick check)
    private Set<String> submittedEmails = new HashSet<>();
}
