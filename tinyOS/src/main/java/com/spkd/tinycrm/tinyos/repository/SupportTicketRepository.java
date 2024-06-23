package com.spkd.tinycrm.tinyos.repository;


import com.spkd.tinycrm.tinyos.entity.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
}
