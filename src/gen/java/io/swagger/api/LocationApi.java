package io.swagger.api;

import io.swagger.api.factories.LocationApiServiceFactory;
import io.swagger.api.impl.authentication.Secured;
import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
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
import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
  @Path("/swagger")
  @Produces(MediaType.TEXT_HTML)
  public URL getSwaggerUI(){

    ClassLoader c = Thread.currentThread().getContextClassLoader();
    return c.getResource("/src/main/webapp/dist/index.html");


//    try {
//      String base = ("/src/main/webapp/dist/index.html");
//      File f = new File(base);
//      System.out.println("file" + f);
//      return new FileInputStream(f);
//    } catch (FileNotFoundException e) {
//      // log the error?
//      return null;
//    }
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
