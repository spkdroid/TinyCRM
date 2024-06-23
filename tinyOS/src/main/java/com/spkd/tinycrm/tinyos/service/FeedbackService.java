package com.spkd.tinycrm.tinyos.service;

import com.spkd.tinycrm.tinyos.entity.Feedback;
import com.spkd.tinycrm.tinyos.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    public Optional<Feedback> getFeedbackById(Long id) {
        return feedbackRepository.findById(id);
    }

    public Feedback createFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    public Feedback updateFeedback(Long id, Feedback feedback) {
        if (feedbackRepository.existsById(id)) {
            feedback.setId(id);
            return feedbackRepository.save(feedback);
        } else {
            return null;
        }
    }

    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }
}
