package its.compito.locali.web;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/login")
public class LoginPage {

    private final Template loginTemplate;

    public LoginPage(@Location("login.qute.html") Template loginTemplate) {
        this.loginTemplate = loginTemplate;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @PermitAll
    public TemplateInstance showLogin(@QueryParam("error") String error) {
        if (error != null) {
            return loginTemplate.data("errore", "Credenziali errate. Riprova.");
        }
        return loginTemplate.data("errore", null);
    }
}