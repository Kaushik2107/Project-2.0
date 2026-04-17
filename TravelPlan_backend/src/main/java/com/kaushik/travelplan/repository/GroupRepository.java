package com.kaushik.travelplan.repository;

import com.kaushik.travelplan.entity.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface GroupRepository extends MongoRepository<Group, String> {
    List<Group> findByAdminEmail(String email);
    List<Group> findByMemberEmails(String email);
}
