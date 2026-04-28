package its.compito.locali.service;

import its.compito.locali.persistence.entity.Review;
import its.compito.locali.persistence.entity.User;
import its.compito.locali.persistence.entity.Venue;
import its.compito.locali.persistence.repository.ReviewRepository;
import its.compito.locali.persistence.repository.UserRepository;
import its.compito.locali.persistence.repository.VenueRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final VenueRepository venueRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, VenueRepository venueRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.venueRepository = venueRepository;
    }

    @Transactional
    public void addReview(int venueId, String username, int rating, String comment) {
        User user = userRepository.findByUsername(username);
        Venue venue = venueRepository.findById(venueId);

        if (user != null && venue != null) {
            Review existingReview = reviewRepository.findByUserAndVenue(username, venueId);

            if (existingReview != null) {
                if ("APPROVED".equals(existingReview.getStatus())) {
                    existingReview.setOldRating(existingReview.getRating());
                    existingReview.setOldComment(existingReview.getComment());
                }

                existingReview.setRating(rating);
                existingReview.setComment(comment);
                existingReview.setStatus("PENDING_UPDATE");
            } else {
                Review review = new Review();
                review.setUser(user);
                review.setVenue(venue);
                review.setRating(rating);
                review.setComment(comment);
                review.setStatus("PENDING");
                reviewRepository.persist(review);
            }
        }
    }

    @Transactional
    public void deleteReview(int venueId, String username) {
        Review review = reviewRepository.findByUserAndVenue(username, venueId);
        if (review != null) {
            reviewRepository.delete(review);
        }
    }

    // --- NUOVI METODI DIVISI PER IL MODERATORE ---

    public List<Review> getPendingNewReviews() {
        return reviewRepository.list("status", "PENDING");
    }

    public List<Review> getPendingUpdatedReviews() {
        return reviewRepository.list("status", "PENDING_UPDATE");
    }

    @Transactional
    public void approveReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId);
        if (review != null) {
            review.setStatus("APPROVED");
            review.setOldRating(null);
            review.setOldComment(null);
        }
    }

    @Transactional
    public void rejectReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId);
        if (review != null) {
            if ("PENDING_UPDATE".equals(review.getStatus())) {
                review.setRating(review.getOldRating());
                review.setComment(review.getOldComment());
                review.setStatus("APPROVED");
                review.setOldRating(null);
                review.setOldComment(null);
            } else {
                reviewRepository.delete(review);
            }
        }
    }
}