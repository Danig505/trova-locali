package its.compito.locali.web;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import its.compito.locali.persistence.entity.User;
import its.compito.locali.persistence.repository.UserRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;

@Path("/admin")
@RolesAllowed({"MODERATOR", "ADMIN"})
public class UserAdminPage {

    private final Template usersTemplate;
    private final UserRepository userRepository;

    public UserAdminPage(
            @Location("users.qute.html") Template usersTemplate,
            UserRepository userRepository) {
        this.usersTemplate = usersTemplate;
        this.userRepository = userRepository;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance showUsers(
            @Context SecurityContext securityContext,
            @QueryParam("search") String search,
            @QueryParam("roleFilter") String roleFilter) {

        List<User> listaUtenti = userRepository.getFilteredUsers(search, roleFilter);
        String currentUsername = securityContext.getUserPrincipal().getName();

        return usersTemplate.data("users", listaUtenti)
                .data("username", currentUsername)
                .data("search", search)
                .data("roleFilter", roleFilter);
    }

    @POST
    @Path("/update-role")
    @Transactional
    public Response updateRole(
            @FormParam("id") int id,
            @FormParam("role") String newRole) {

        userRepository.updateUserRole(id, newRole);
        return Response.seeOther(java.net.URI.create("/admin")).build();
    }
}