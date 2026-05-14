package its.compito.locali.service;

import its.compito.locali.persistence.entity.Venue;
import its.compito.locali.persistence.repository.VenueRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class VenueService {

    private final VenueRepository venueRepository;

    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    // IL @TRANSACTIONAL ORA STA QUI! Nel posto giusto.
    @Transactional
    public void saveOrUpdateVenue(Integer id, String title, String description, String address,
                                  Double latitude, Double longitude, List<String> categories,
                                  List<String> photos, int userId) {

        Venue venue;

        // Se l'ID è nullo, è un nuovo locale
        if (id == null) {
            venue = new Venue();
            venue.setUserId(userId);
        } else {
            // Se esiste, lo peschiamo dal DB
            venue = venueRepository.findById(id);
            if (venue == null) {
                throw new IllegalArgumentException("Locale non trovato");
            }
        }

        // Aggiorniamo i campi
        venue.setTitle(title);
        venue.setDescription(description);
        venue.setAddress(address);
        if (latitude != null) venue.setLatitude(latitude);
        if (longitude != null) venue.setLongitude(longitude);
        if (categories != null) venue.setCategories(categories);
        venue.setPhotos(photos);

        // Se era nuovo, lo salviamo per la prima volta
        // (Se esisteva già, Hibernate salverà le modifiche automaticamente a fine transazione)
        if (id == null) {
            venueRepository.persist(venue);
        }
    }

    @Transactional
    public void deleteVenueById(int id) {
        venueRepository.deleteById(id);
    }
}