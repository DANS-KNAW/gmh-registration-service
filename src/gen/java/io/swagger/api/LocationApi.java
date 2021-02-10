package io.swagger.api;

import io.swagger.api.factories.LocationApiServiceFactory;
import nl.knaw.dans.nbnresolver.authentication.Secured;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import javax.servlet.ServletConfig;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.net.URL;

@Path("/location")

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-01-22T09:23:35.355Z[GMT]")
public class LocationApi {

  private final LocationApiService delegate;

  public LocationApi(@Context ServletConfig servletContext) {
    LocationApiService delegate = null;

    if (servletContext != null) {
      String implClass = servletContext.getInitParameter("LocationApi.implementation");
      if (implClass != null && !"".equals(implClass.trim())) {
        try {
          delegate = (LocationApiService) Class.forName(implClass).newInstance();
        }
        catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }

    if (delegate == null) {
      delegate = LocationApiServiceFactory.getLocationApi();
    }

    this.delegate = delegate;
  }



  @GET
  @Secured
  @Path("/{location}")

  @Produces({ "application/json" })
  @Operation(summary = "Returns URN:NBN identifier(s) registered for this location.", description = "Returns URN:NBN identifier(s) associated with this location, if at least one of them is registered to the authenticated user.", security = { @SecurityRequirement(name = "BearerAuth") }, tags = { "Location" })
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)))),

      @ApiResponse(responseCode = "401", description = "Authentication information is missing or invalid"),

      @ApiResponse(responseCode = "404", description = "Object (location) not found") })
  public Response getNbnByLocation(@Parameter(in = ParameterIn.PATH, description = "Location URI", required = true) @PathParam("location") String location, @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.getNbnByLocation(location, securityContext);
  }
}
