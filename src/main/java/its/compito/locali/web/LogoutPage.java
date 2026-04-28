package its.compito.locali.web;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Path("/logout")
public class LogoutPage {

    @GET
    public Response doLogout() {
        return Response.seeOther(URI.create("/login")).build();
    }
}