package io.swagger.api;

import io.swagger.model.*;
import io.swagger.api.TokenApiService;
import io.swagger.api.factories.TokenApiServiceFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import io.swagger.model.Credentials;

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


@Path("/token")


@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-02-03T12:44:06.016Z[GMT]")public class TokenApi  {
   private final TokenApiService delegate;

   public TokenApi(@Context ServletConfig servletContext) {
      TokenApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("TokenApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (TokenApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = TokenApiServiceFactory.getTokenApi();
      }

      this.delegate = delegate;
   }

    @POST
    
    @Consumes({ "application/json" })
    
    @Operation(summary = "Returns an api token", description = "Authenticates a user and generates an api token to be used for requests to the other endpoints.", security = {
        @SecurityRequirement(name = "UserLogin")    }, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "OK (Token generated)"),
        
        @ApiResponse(responseCode = "401", description = "Authentication information is missing or invalid") })
    public Response token(@Parameter(in = ParameterIn.DEFAULT, description = "A json object with username and password" ,required=true) Credentials body

,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.token(body,securityContext);
    }
}
