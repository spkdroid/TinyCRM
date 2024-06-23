package com.spkd.tinycrm.tinyos.service;

import com.spkd.tinycrm.tinyos.entity.SupportTicket;
import com.spkd.tinycrm.tinyos.repository.SupportTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupportTicketService {

    @Autowired
    private SupportTicketRepository supportTicketRepository;

    public List<SupportTicket> getAllTickets() {
        return supportTicketRepository.findAll();
    }

    public Optional<SupportTicket> getTicketById(Long id) {
        return supportTicketRepository.findById(id);
    }

    public SupportTicket createTicket(SupportTicket supportTicket) {
        return supportTicketRepository.save(supportTicket);
    }

    public SupportTicket updateTicket(Long id, SupportTicket supportTicket) {
        if (supportTicketRepository.existsById(id)) {
            supportTicket.setId(id);
            return supportTicketRepository.save(supportTicket);
        } else {
            return null;
        }
    }

    public void deleteTicket(Long id) {
        supportTicketRepository.deleteById(id);
    }
}
