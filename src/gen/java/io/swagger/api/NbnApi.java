/**
 * Copyright (C) 2018 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.swagger.api;

import io.swagger.model.*;
import io.swagger.api.NbnApiService;
import io.swagger.api.factories.NbnApiServiceFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import io.swagger.model.LtpLocation;
import io.swagger.model.NbnLocationsObject;
import io.swagger.model.NbnLtpLocationsObject;

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


@Path("/nbn")


@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-10-27T13:45:58.722388+02:00[Europe/Amsterdam]")public class NbnApi  {
   private final NbnApiService delegate;

   public NbnApi(@Context ServletConfig servletContext) {
      NbnApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("NbnApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (NbnApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = NbnApiServiceFactory.getNbnApi();
      }

      this.delegate = delegate;
   }

    @POST
    
    @Consumes({ "application/json" })
    
    @Operation(summary = "Registers a new URN:NBN {identifier} associated with a prioritized list of locations.", description = "Registers a new URN:NBN associated with a prioritized list of locations. Multiple locations are prioritized in respective order. The first location is the preferred. The second is failover, etc. <br />The NBN identifier must have a prefix that matches the authenticated user. If the registered user is a registered LTP archive, the submittede locations will be registered as LTP locations.", security = {
        @SecurityRequirement(name = "BearerAuth")    }, tags={ "URN:NBN identifier" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Successful operation (created new)"),
        
        @ApiResponse(responseCode = "400", description = "Invalid URN:NBN identifier pattern or location uri(s) supplied"),
        
        @ApiResponse(responseCode = "401", description = "Authentication information is missing or invalid"),
        
        @ApiResponse(responseCode = "403", description = "URN:NBN identifier is valid, but does not match the prefix of the authenticated user"),
        
        @ApiResponse(responseCode = "409", description = "Conflict, resource already exists") })
    public Response createNbnLocations(@Parameter(in = ParameterIn.DEFAULT, description = "A json object that contains the URN:NBN and associated locations." ,required=true) NbnLocationsObject body

,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.createNbnLocations(body,securityContext);
    }
    @GET
    @Path("/{identifier}/locations")
    
    @Produces({ "application/json" })
    @Operation(summary = "Returns registered location objects for this URN:NBN {identifier}.", description = "Returns all registered location objects for this {identifier}.<br>The identifier must have a prefix that matches the authenticated user, unless you are a registered LTP user. HTTP 404 otherwise.", security = {
        @SecurityRequirement(name = "BearerAuth")    }, tags={ "URN:NBN identifier" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LtpLocation.class)))),
        
        @ApiResponse(responseCode = "400", description = "Invalid URN:NBN identifier supplied"),
        
        @ApiResponse(responseCode = "401", description = "Authentication information is missing or invalid"),
        
        @ApiResponse(responseCode = "404", description = "Supplied URN:NBN identifier not found"),
        
        @ApiResponse(responseCode = "403", description = "URN:NBN-prefix is not registered to this user") })
    public Response getLocationsByNbn(@Parameter(in = ParameterIn.PATH, description = "URN:NBN identifier",required=true) @PathParam("identifier") String identifier
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.getLocationsByNbn(identifier,securityContext);
    }
    @GET
    @Path("/{identifier}")
    
    @Produces({ "application/json" })
    @Operation(summary = "Returns URN:NBN object (urn:nbn + locations) for this {identifier}.", description = "Returns URN:NBN object (urn:nbn + LTP location objects) for this {identifier}.<br />The identifier must have a prefix that matches the authenticated user if not registered as LTP user. HTTP 404 otherwise.<br />Use this method to see if your URN:NBN has a LTP location associated with it.", security = {
        @SecurityRequirement(name = "BearerAuth")    }, tags={ "URN:NBN identifier" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NbnLtpLocationsObject.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid URN:NBN identifier pattern supplied"),
        
        @ApiResponse(responseCode = "401", description = "Authentication information is missing or invalid"),
        
        @ApiResponse(responseCode = "403", description = "URN:NBN-prefix is not registered to this user"),
        
        @ApiResponse(responseCode = "404", description = "Supplied URN:NBN identifier not found") })
    public Response getNbnRecord(@Parameter(in = ParameterIn.PATH, description = "URN:NBN identifier",required=true) @PathParam("identifier") String identifier
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.getNbnRecord(identifier,securityContext);
    }
    @PUT
    @Path("/{identifier}")
    @Consumes({ "application/json" })
    
    @Operation(summary = "Updates an existing URN:NBN {identifier} or registers a new URN:NBN {identifier} associated with a prioritized list of locations.", description = "Updates or registers a (new) URN:NBN associated with a prioritized list of locations. Multiple locations are prioritized in respective order. The first location is the preferred. The second is failover, etc. <br />Identifiers must have a prefix that matches the authenticated user, unless you are a registered LTP user. In case of an update the existing locations will be replaced by the locations provided in this request.", security = {
        @SecurityRequirement(name = "BearerAuth")    }, tags={ "URN:NBN identifier" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "OK (updated existing)"),
        
        @ApiResponse(responseCode = "201", description = "Successful operation (created new)"),
        
        @ApiResponse(responseCode = "400", description = "Invalid URN:NBN identifier or location(s) supplied"),
        
        @ApiResponse(responseCode = "401", description = "Authentication information is missing or invalid"),
        
        @ApiResponse(responseCode = "403", description = "URN:NBN-prefix is not registered to this user") })
    public Response updateNbnRecord(@Parameter(in = ParameterIn.DEFAULT, description = "A json object that contains the associated locations for the {identifier}" ,required=true) java.util.List<String> body

,@Parameter(in = ParameterIn.PATH, description = "URN:NBN identifier",required=true) @PathParam("identifier") String identifier
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.updateNbnRecord(body,identifier,securityContext);
    }
}
