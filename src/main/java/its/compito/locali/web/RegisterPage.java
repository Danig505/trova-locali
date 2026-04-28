package its.compito.locali.web; // O il package in cui si trova

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import its.compito.locali.service.UserService;

@Path("/register")
public class RegisterPage {

    private final Template registerTemplate;
    private final UserService userService;

    public RegisterPage(
            @Location("register.qute.html") Template registerTemplate,
            UserService userService) {
        this.registerTemplate = registerTemplate;
        this.userService = userService;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance showRegisterPage(@QueryParam("error") String error) {
        return registerTemplate.data("error", error);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response processRegistration(
            @FormParam("username") String username,
            @FormParam("password") String password,
            @FormParam("confirmPassword") String confirmPassword) {

        UserService.RegistrationResult result = userService.register(username, password, confirmPassword);

        if (result.ok()) {
            return Response.seeOther(URI.create("/login")).build();
        } else {
            String encodedError = java.net.URLEncoder.encode(result.errorMessage(), java.nio.charset.StandardCharsets.UTF_8);

            return Response.seeOther(URI.create("/register?error=" + encodedError)).build();
        }
    }
}