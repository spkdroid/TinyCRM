package com.spkd.tinycrm.tinyos.repository;

import com.spkd.tinycrm.tinyos.entity.TicketComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {
}