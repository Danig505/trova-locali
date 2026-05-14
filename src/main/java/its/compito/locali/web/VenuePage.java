package its.compito.locali.web;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import its.compito.locali.persistence.entity.Review;
import its.compito.locali.persistence.entity.User;
import its.compito.locali.persistence.entity.Venue;
import its.compito.locali.persistence.repository.UserRepository;
import its.compito.locali.persistence.repository.VenueRepository;
import its.compito.locali.service.ReviewService;
import its.compito.locali.service.VenueService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Path("/venues")
public class VenuePage {

    private final Template venuesTemplate;
    private final Template venueDetailTemplate;
    private final Template venueFormTemplate;

    private final VenueRepository venueRepository;
    private final UserRepository userRepository;

    private final ReviewService reviewService;
    private final VenueService venueService; // <-- Aggiunto il VenueService!

    public VenuePage(
            @Location("venues.qute.html") Template venuesTemplate,
            @Location("venue_detail.qute.html") Template venueDetailTemplate,
            @Location("venue_form.qute.html") Template venueFormTemplate,
            VenueRepository venueRepository,
            UserRepository userRepository,
            ReviewService reviewService,
            VenueService venueService) {
        this.venuesTemplate = venuesTemplate;
        this.venueDetailTemplate = venueDetailTemplate;
        this.venueFormTemplate = venueFormTemplate;
        this.venueRepository = venueRepository;
        this.userRepository = userRepository;
        this.reviewService = reviewService;
        this.venueService = venueService;
    }

    @GET
    @PermitAll
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance showVenues(
            @Context SecurityContext securityContext,
            @QueryParam("search") String search,
            @QueryParam("category") String category,
            @QueryParam("userLat") Double userLat,
            @QueryParam("userLon") Double userLon,
            @QueryParam("radius") Double radius) {

        var ultimeAggiunte = venueRepository.getLatestVenues(10);
        List<Venue> listaLocali;

        double actualRadius = (radius != null) ? radius : 2.0;

        if (userLat != null && userLon != null) {
            listaLocali = venueRepository.findNearbyVenues(userLat, userLon, actualRadius);
        } else {
            listaLocali = venueRepository.getFilteredVenues(search, category);
        }

        String username = null;
        String role = "GUEST";
        int currentUserId = -1;

        if (securityContext.getUserPrincipal() != null) {
            username = securityContext.getUserPrincipal().getName();
            User u = userRepository.findByUsername(username);
            if (u != null) {
                currentUserId = u.getId();
            }
            if (securityContext.isUserInRole("MODERATOR") || securityContext.isUserInRole("ADMIN")) {
                role = "MODERATOR";
            } else {
                role = "USER";
            }
        }

        return venuesTemplate.data("recipes", listaLocali)
                .data("latestRecipes", ultimeAggiunte)
                .data("role", role)
                .data("username", username)
                .data("currentUserId", currentUserId)
                .data("search", search)
                .data("selectedCategory", category)
                .data("userLat", userLat)
                .data("userLon", userLon)
                .data("radius", actualRadius);
    }

    @GET
    @Path("/{id}")
    @PermitAll
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance showVenueDetail(@PathParam("id") int id, @Context SecurityContext securityContext) {
        Venue venue = venueRepository.findById(id);
        if (venue == null) throw new NotFoundException();

        String username = null;
        String role = "GUEST";
        int currentUserId = -1;

        if (securityContext.getUserPrincipal() != null) {
            username = securityContext.getUserPrincipal().getName();
            User u = userRepository.findByUsername(username);
            if (u != null) currentUserId = u.getId();
            if (securityContext.isUserInRole("MODERATOR") || securityContext.isUserInRole("ADMIN")) role = "MODERATOR";
        }

        Review userReview = null;
        List<Review> approvedReviews = new ArrayList<>();
        double sum = 0;

        if (venue.getReviews() != null) {
            for (Review r : venue.getReviews()) {
                if ("APPROVED".equals(r.getStatus())) {
                    approvedReviews.add(r);
                    sum += r.getRating();
                }
                if (username != null && r.getUser().getUsername().equals(username)) {
                    userReview = r;
                }
            }
        }

        double mediaVoti = approvedReviews.isEmpty() ? 0.0 : sum / approvedReviews.size();
        approvedReviews.sort((r1, r2) -> Long.compare(r2.getId(), r1.getId()));

        User creator = userRepository.findById(venue.getUserId());
        String creatorName = (creator != null) ? creator.getUsername() : "Utente Sconosciuto";

        return venueDetailTemplate
                .data("recipe", venue)
                .data("username", username)
                .data("role", role)
                .data("currentUserId", currentUserId)
                .data("userReview", userReview)
                .data("approvedReviews", approvedReviews)
                .data("mediaVoti", String.format("%.1f", mediaVoti))
                .data("creatorName", creatorName);
    }

    // NESSUN TRANSACTIONAL QUI!
    @GET
    @Path("/delete/{id}")
    @Authenticated
    public Response deleteVenue(@PathParam("id") int id, @Context SecurityContext securityContext) {
        Venue venue = venueRepository.findById(id);
        if (venue == null) return Response.status(404).build();

        String username = securityContext.getUserPrincipal().getName();
        User currentUser = userRepository.findByUsername(username);
        boolean isMod = securityContext.isUserInRole("MODERATOR") || securityContext.isUserInRole("ADMIN");

        if (!isMod && venue.getUserId() != currentUser.getId()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        // Il controller si limita a chiamare il Service
        venueService.deleteVenueById(id);

        return Response.seeOther(java.net.URI.create("/venues")).build();
    }

    @GET
    @Path("/new")
    @Authenticated
    @Produces(MediaType.TEXT_HTML)
    public Response showAddForm() {
        return Response.ok(venueFormTemplate.data("recipe", null)).build();
    }

    @GET
    @Path("/edit/{id}")
    @Authenticated
    @Produces(MediaType.TEXT_HTML)
    public Response showEditForm(@PathParam("id") int id, @Context SecurityContext securityContext) {
        Venue venue = venueRepository.findById(id);
        if (venue == null) return Response.status(404).build();

        String username = securityContext.getUserPrincipal().getName();
        User currentUser = userRepository.findByUsername(username);
        boolean isMod = securityContext.isUserInRole("MODERATOR") || securityContext.isUserInRole("ADMIN");

        if (!isMod && venue.getUserId() != currentUser.getId()) {
            return Response.status(Response.Status.FORBIDDEN).entity("Accesso Negato: Non sei il creatore del locale.").build();
        }

        return Response.ok(venueFormTemplate.data("recipe", venue)).build();
    }

    // NESSUN TRANSACTIONAL QUI!
    @POST
    @Path("/save")
    @Authenticated
    public Response saveVenue(
            @FormParam("id") String idStr,
            @FormParam("title") String title,
            @FormParam("description") String description,
            @FormParam("address") String address,
            @FormParam("latitude") Double latitude,
            @FormParam("longitude") Double longitude,
            @FormParam("category") List<String> categories,
            @FormParam("photos") String photosRaw,
            @Context SecurityContext securityContext) {

        String username = securityContext.getUserPrincipal().getName();
        User currentUser = userRepository.findByUsername(username);
        boolean isMod = securityContext.isUserInRole("MODERATOR") || securityContext.isUserInRole("ADMIN");

        Integer parsedId = (idStr == null || idStr.trim().isEmpty()) ? null : Integer.parseInt(idStr);

        // Controllo di sicurezza: se sto modificando, devo essere l'autore o un mod
        if (parsedId != null) {
            Venue venue = venueRepository.findById(parsedId);
            if (venue == null) return Response.status(404).build();
            if (!isMod && venue.getUserId() != currentUser.getId()) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
        }

        // Formatto le foto prima di passarle al database
        List<String> photoList = new ArrayList<>();
        if (photosRaw != null && !photosRaw.trim().isEmpty()) {
            photoList = new ArrayList<>(java.util.Arrays.asList(photosRaw.split("\\r?\\n")));
            photoList.replaceAll(String::trim);
            photoList.removeIf(String::isEmpty);
        }

        // Chiamo il Service! Qui si apre la transazione veloce.
        venueService.saveOrUpdateVenue(parsedId, title, description, address, latitude, longitude, categories, photoList, currentUser.getId());

        return Response.seeOther(java.net.URI.create("/venues")).build();
    }

    // NESSUN TRANSACTIONAL QUI (lo gestisce il ReviewService internamente)
    @POST
    @Path("/{id}/review")
    @Authenticated
    public Response postReview(
            @PathParam("id") int venueId,
            @FormParam("rating") int rating,
            @FormParam("comment") String comment,
            @Context SecurityContext securityContext) {

        String username = securityContext.getUserPrincipal().getName();
        reviewService.addReview(venueId, username, rating, comment);
        return Response.seeOther(URI.create("/venues/" + venueId)).build();
    }

    // NESSUN TRANSACTIONAL QUI
    @GET
    @Path("/{id}/review/delete")
    @Authenticated
    public Response deleteReview(
            @PathParam("id") int venueId,
            @Context SecurityContext securityContext) {

        String username = securityContext.getUserPrincipal().getName();
        reviewService.deleteReview(venueId, username);
        return Response.seeOther(URI.create("/venues/" + venueId)).build();
    }

    // NESSUN TRANSACTIONAL QUI
    @GET
    @Path("/review/{reviewId}/delete-by-mod")
    @Authenticated
    public Response deleteReviewByMod(
            @PathParam("reviewId") int reviewId,
            @QueryParam("venueId") int venueId,
            @Context SecurityContext securityContext) {

        boolean isMod = securityContext.isUserInRole("MODERATOR") || securityContext.isUserInRole("ADMIN");

        if (!isMod) {
            return Response.status(Response.Status.FORBIDDEN).entity("Accesso negato. Solo i moderatori possono usare questa funzione.").build();
        }

        reviewService.deleteReviewById(reviewId);

        return Response.seeOther(URI.create("/venues/" + venueId)).build();
    }
}