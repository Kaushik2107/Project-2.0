package com.kaushik.travelplan.repository;

import com.kaushik.travelplan.entity.GroupTrip;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface GroupTripRepository extends MongoRepository<GroupTrip, String> {
    Optional<GroupTrip> findByGroupIdAndStatus(String groupId, String status);
}
