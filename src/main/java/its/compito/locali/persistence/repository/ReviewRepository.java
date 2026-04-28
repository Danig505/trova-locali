package its.compito.locali.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import its.compito.locali.persistence.entity.Review;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ReviewRepository implements PanacheRepositoryBase<Review, Long> {

    public Review findByUserAndVenue(String username, int venueId) {
        return find("user.username = ?1 and venue.id = ?2", username, venueId).firstResult();
    }
}