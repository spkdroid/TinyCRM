package com.spkd.tinycrm.tinyos.controller;

import com.spkd.tinycrm.tinyos.entity.SupportTicket;
import com.spkd.tinycrm.tinyos.service.SupportTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class SupportTicketController {

    @Autowired
    private SupportTicketService supportTicketService;

    @GetMapping
    public List<SupportTicket> getAllTickets() {
        return supportTicketService.getAllTickets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupportTicket> getTicketById(@PathVariable Long id) {
        return supportTicketService.getTicketById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public SupportTicket createTicket(@RequestBody SupportTicket supportTicket) {
        return supportTicketService.createTicket(supportTicket);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupportTicket> updateTicket(@PathVariable Long id, @RequestBody SupportTicket supportTicket) {
        SupportTicket updatedTicket = supportTicketService.updateTicket(id, supportTicket);
        if (updatedTicket != null) {
            return ResponseEntity.ok(updatedTicket);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        supportTicketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
