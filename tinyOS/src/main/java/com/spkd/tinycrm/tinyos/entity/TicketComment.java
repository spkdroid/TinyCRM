package com.spkd.tinycrm.tinyos.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class TicketComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private SupportTicket supportTicket;

    private String comment;
    private String author;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SupportTicket getSupportTicket() {
        return supportTicket;
    }

    public void setSupportTicket(SupportTicket supportTicket) {
        this.supportTicket = supportTicket;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
