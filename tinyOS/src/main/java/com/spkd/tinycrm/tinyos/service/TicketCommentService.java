package com.spkd.tinycrm.tinyos.service;

import com.spkd.tinycrm.tinyos.entity.TicketComment;
import com.spkd.tinycrm.tinyos.repository.TicketCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketCommentService {

    @Autowired
    private TicketCommentRepository ticketCommentRepository;

    public List<TicketComment> getAllComments() {
        return ticketCommentRepository.findAll();
    }

    public Optional<TicketComment> getCommentById(Long id) {
        return ticketCommentRepository.findById(id);
    }

    public TicketComment createComment(TicketComment ticketComment) {
        return ticketCommentRepository.save(ticketComment);
    }

    public TicketComment updateComment(Long id, TicketComment ticketComment) {
        if (ticketCommentRepository.existsById(id)) {
            ticketComment.setId(id);
            return ticketCommentRepository.save(ticketComment);
        } else {
            return null;
        }
    }

    public void deleteComment(Long id) {
        ticketCommentRepository.deleteById(id);
    }

    public List<TicketComment> getCommentsByTicketId(Long ticketId) {
        return ticketCommentRepository.findBySupportTicketIdOrderByCreatedDateAsc(ticketId);
    }
}
