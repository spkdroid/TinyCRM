package com.spkd.tinycrm.tinyos.repository;

import com.spkd.tinycrm.tinyos.entity.SupportTicket;
import com.spkd.tinycrm.tinyos.entity.TicketComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {
    List<TicketComment> findBySupportTicketOrderByCreatedDateAsc(SupportTicket supportTicket);
    List<TicketComment> findBySupportTicketIdOrderByCreatedDateAsc(Long ticketId);
}