package its.compito.locali.web;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import its.compito.locali.service.ReviewService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Path("/moderator")
@RolesAllowed({"MODERATOR", "ADMIN"})
public class ModeratorPage {

    private final Template moderatorTemplate;
    private final ReviewService reviewService;

    public ModeratorPage(
            @Location("moderator.qute.html") Template moderatorTemplate,
            ReviewService reviewService) {
        this.moderatorTemplate = moderatorTemplate;
        this.reviewService = reviewService;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance showPendingReviews() {
        return moderatorTemplate
                .data("newReviews", reviewService.getPendingNewReviews())
                .data("updatedReviews", reviewService.getPendingUpdatedReviews());
    }

    @GET
    @Path("/approve/{id}")
    public Response approve(@PathParam("id") Long id) {
        reviewService.approveReview(id);
        return Response.seeOther(URI.create("/moderator")).build();
    }

    @GET
    @Path("/reject/{id}")
    public Response reject(@PathParam("id") Long id) {
        reviewService.rejectReview(id);
        return Response.seeOther(URI.create("/moderator")).build();
    }
}