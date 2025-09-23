package com.spkd.tinycrm.tinyos.service;

import com.spkd.tinycrm.tinyos.entity.SupportTicket;
import com.spkd.tinycrm.tinyos.repository.SupportTicketRepository;
import com.spkd.tinycrm.tinyos.dto.TicketUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
        supportTicket.setCreatedDate(new Date());
        supportTicket.setUpdatedDate(new Date());
        if (supportTicket.getStatus() == null || supportTicket.getStatus().isEmpty()) {
            supportTicket.setStatus("OPEN");
        }
        return supportTicketRepository.save(supportTicket);
    }

    public SupportTicket updateTicket(Long id, SupportTicket supportTicket) {
        if (supportTicketRepository.existsById(id)) {
            supportTicket.setId(id);
            supportTicket.setUpdatedDate(new Date());
            return supportTicketRepository.save(supportTicket);
        } else {
            return null;
        }
    }

    public void deleteTicket(Long id) {
        supportTicketRepository.deleteById(id);
    }

    public SupportTicket updateTicketStatus(Long id, String status) {
        Optional<SupportTicket> optionalTicket = supportTicketRepository.findById(id);
        if (optionalTicket.isPresent()) {
            SupportTicket ticket = optionalTicket.get();
            ticket.setStatus(status);
            ticket.setUpdatedDate(new Date());
            return supportTicketRepository.save(ticket);
        }
        return null;
    }

    public SupportTicket patchTicket(Long id, TicketUpdateRequest updateRequest) {
        Optional<SupportTicket> optionalTicket = supportTicketRepository.findById(id);
        if (optionalTicket.isPresent()) {
            SupportTicket ticket = optionalTicket.get();
            
            // Update only non-null fields from the request
            if (updateRequest.getTitle() != null && !updateRequest.getTitle().trim().isEmpty()) {
                ticket.setSubject(updateRequest.getTitle());
            }
            if (updateRequest.getDescription() != null) {
                ticket.setDescription(updateRequest.getDescription());
            }
            if (updateRequest.getStatus() != null && !updateRequest.getStatus().trim().isEmpty()) {
                ticket.setStatus(updateRequest.getStatus());
            }
            if (updateRequest.getPriority() != null && !updateRequest.getPriority().trim().isEmpty()) {
                ticket.setPriority(updateRequest.getPriority());
            }
            if (updateRequest.getCategory() != null && !updateRequest.getCategory().trim().isEmpty()) {
                ticket.setCategory(updateRequest.getCategory());
            }
            
            ticket.setUpdatedDate(new Date());
            return supportTicketRepository.save(ticket);
        }
        return null;
    }
}
