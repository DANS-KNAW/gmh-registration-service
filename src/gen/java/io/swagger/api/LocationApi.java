package io.swagger.api;

import io.swagger.model.*;
import io.swagger.api.LocationApiService;
import io.swagger.api.factories.LocationApiServiceFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


import java.util.Map;
import java.util.List;
import io.swagger.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.*;
import javax.validation.constraints.*;


@Path("/location")


@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-01-08T12:34:19.815Z[GMT]")public class LocationApi  {
   private final LocationApiService delegate;

   public LocationApi(@Context ServletConfig servletContext) {
      LocationApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("LocationApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (LocationApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
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
    @Path("/{location}")
    
    @Produces({ "application/json" })
    @Operation(summary = "Returns URN:NBN identifier(s) registered for this location.", description = "Returns URN:NBN identifier(s) associated with this location, if at least one of them is registered to the authenticated user.", security = {
        @SecurityRequirement(name = "UserLogin")    }, tags={ "Location" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)))),
        
        @ApiResponse(responseCode = "401", description = "Authentication information is missing or invalid"),
        
        @ApiResponse(responseCode = "404", description = "Object (location) not found") })
    public Response getNbnByLocation(@Parameter(in = ParameterIn.PATH, description = "Location URI",required=true) @PathParam("location") String location
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.getNbnByLocation(location,securityContext);
    }
}
