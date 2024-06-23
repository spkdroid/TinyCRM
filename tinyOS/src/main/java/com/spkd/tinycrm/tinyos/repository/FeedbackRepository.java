package com.spkd.tinycrm.tinyos.repository;

import com.spkd.tinycrm.tinyos.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
