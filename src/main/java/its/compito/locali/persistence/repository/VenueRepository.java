package its.compito.locali.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import its.compito.locali.persistence.entity.Venue;
import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.panache.common.Sort;
import java.util.List;
import java.util.ArrayList;

@ApplicationScoped
public class VenueRepository implements PanacheRepositoryBase<Venue, Integer> {

    public List<Venue> getLatestVenues(int limit) {
        return findAll(Sort.descending("id")).page(0, limit).list();
    }

    public List<Venue> getFilteredVenues(String search, String category) {
        String query = "1=1";
        java.util.Map<String, Object> params = new java.util.HashMap<>();

        if (search != null && !search.trim().isEmpty()) {
            query += " and lower(title) like :search";
            params.put("search", "%" + search.toLowerCase() + "%");
        }
        if (category != null && !category.trim().isEmpty()) {
            query += " and :category MEMBER OF categories";
            params.put("category", category);
        }

        return find(query, params).list();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public List<Venue> findNearbyVenues(double userLat, double userLon, double maxDistanceKm) {
        List<Venue> allVenues = listAll();
        List<Venue> nearbyVenues = new ArrayList<>();

        for (Venue venue : allVenues) {
            double distance = calculateDistance(userLat, userLon, venue.getLatitude(), venue.getLongitude());
            if (distance <= maxDistanceKm) {
                nearbyVenues.add(venue);
            }
        }
        return nearbyVenues;
    }
}