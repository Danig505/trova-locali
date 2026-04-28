package its.compito.locali.service;

import io.quarkus.runtime.StartupEvent;
import its.compito.locali.persistence.entity.User;
import its.compito.locali.persistence.entity.Venue;
import its.compito.locali.persistence.repository.UserRepository;
import its.compito.locali.persistence.repository.VenueRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class SeedData {

    private final UserRepository userRepository;
    private final VenueRepository venueRepository;

    public SeedData(UserRepository userRepository, VenueRepository venueRepository) {
        this.userRepository = userRepository;
        this.venueRepository = venueRepository;
    }

    @Transactional
    public void onStart(@Observes StartupEvent ev) {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("$2a$10$kya302Daw4yQ1BOeU1bDgOTBXy6iSq5oFt4.k66Bv4yjQ.npF9guG");
            admin.setRole("ADMIN");
            userRepository.persist(admin);

            Venue v = new Venue();
            v.setTitle("Pizzeria da Mario");
            v.setAddress("Via Roma 1, Milano");
            v.setDescription("Locale tipico con forno a legna.");
            v.setLatitude(45.4642);
            v.setLongitude(9.1900);
            v.setCategories(List.of("Pizzeria", "Ristorante"));
            v.setUserId(admin.getId());
            venueRepository.persist(v);
        }
    }
}