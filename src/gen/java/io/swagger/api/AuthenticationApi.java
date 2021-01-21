package io.swagger.api;

import io.swagger.model.*;
import io.swagger.api.AuthenticationApiService;
import io.swagger.api.factories.AuthenticationApiServiceFactory;

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


@Path("/authentication")


@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-01-20T15:13:44.667Z[GMT]")public class AuthenticationApi  {
   private final AuthenticationApiService delegate;

   public AuthenticationApi(@Context ServletConfig servletContext) {
      AuthenticationApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("AuthenticationApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (AuthenticationApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = AuthenticationApiServiceFactory.getAuthenticationApi();
      }

      this.delegate = delegate;
   }

    @PUT
    
    @Consumes({ "application/json" })
    
    @Operation(summary = "Authenticates", description = "issues ttoken", security = {
        @SecurityRequirement(name = "UserLogin")    }, tags={ "URN:NBN identifier" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "OK (updated existing)"),
        
        @ApiResponse(responseCode = "401", description = "Authentication information is missing or invalid") })
    public Response authenticate(@Parameter(in = ParameterIn.DEFAULT, description = "A json object with username and password" ,required=true) List<String> body

,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.authenticate(body,securityContext);
    }
}
