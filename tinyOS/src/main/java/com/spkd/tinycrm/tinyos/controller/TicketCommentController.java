package com.spkd.tinycrm.tinyos.controller;

import com.spkd.tinycrm.tinyos.entity.TicketComment;
import com.spkd.tinycrm.tinyos.service.TicketCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class TicketCommentController {

    @Autowired
    private TicketCommentService ticketCommentService;

    @GetMapping
    public List<TicketComment> getAllComments() {
        return ticketCommentService.getAllComments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketComment> getCommentById(@PathVariable Long id) {
        return ticketCommentService.getCommentById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public TicketComment createComment(@RequestBody TicketComment ticketComment) {
        return ticketCommentService.createComment(ticketComment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketComment> updateComment(@PathVariable Long id, @RequestBody TicketComment ticketComment) {
        TicketComment updatedComment = ticketCommentService.updateComment(id, ticketComment);
        if (updatedComment != null) {
            return ResponseEntity.ok(updatedComment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        ticketCommentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}