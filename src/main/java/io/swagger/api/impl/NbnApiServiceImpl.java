package io.swagger.api.impl;

import io.swagger.api.ApiResponseMessage;
import io.swagger.api.NbnApiService;
import io.swagger.api.NotFoundException;
import io.swagger.api.impl.jdbc.Dao;
import io.swagger.api.impl.jdbc.Dao.SqlResponse;
import io.swagger.model.NbnLocationsObject;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-01-08T12:34:19.815Z[GMT]")
public class NbnApiServiceImpl extends NbnApiService {

  Dao dao = new Dao();
  NbnValidator nbnValidator = new NbnValidator();
  LocationValidator locationValidator = new LocationValidator();

  @Override
  public Response createNbnLocations(NbnLocationsObject body, SecurityContext securityContext) throws NotFoundException {

    Response response = null;
    boolean nbnIsValid = nbnValidator.validate(body.getIdentifier());
    boolean locationsValid = locationValidator.validateAllLocations(body.getLocations());

    if (nbnIsValid && locationsValid) {
      SqlResponse result = dao.createOrUpdateNbn(body);
      switch (result) {
        case OK:
          response = Response.status(201).entity(new ApiResponseMessage(ApiResponseMessage.INFO, "Successful operation (created new)")).build();
          break;
        //TODO refactor
        case UPDATE:
          response = Response.status(201).entity(new ApiResponseMessage(ApiResponseMessage.INFO, "Successful operation (created new)")).build();
          break;
        case DUPLICATE:
          response = Response.status(409).entity(new ApiResponseMessage(ApiResponseMessage.INFO, "Conflict, resource already exists")).build();
          break;
        //Todo: what is response for general SQL insert failure?
        case FAILURE:
          response = Response.status(400).entity(new ApiResponseMessage(ApiResponseMessage.INFO, "Nbn could not be registered")).build();
          break;
      }
    }
    else {
      response = Response.status(400).entity(new ApiResponseMessage(ApiResponseMessage.INFO, "Invalid URN:NBN identifier pattern or location uri(s) supplied")).build();
    }
    return response;
  }

  @Override
  public Response getLocationsByNbn(String identifier, SecurityContext securityContext) throws NotFoundException {
    if (nbnValidator.validate(identifier)) {
      List<String> locations = dao.getLocations(identifier);
      if (locations.isEmpty()) {
        return Response.status(404).entity(new ApiResponseMessage(ApiResponseMessage.INFO, "Supplied URN:NBN identifier not found")).build();
      }
      else {
        return Response.status(200, "OK").entity(locations).build();
      }
    }
    else {
      return Response.status(400).entity(new ApiResponseMessage(ApiResponseMessage.INFO, "Invalid URN:NBN identifier supplied")).build();
    }
  }

  @Override
  public Response getNbnRecord(String identifier, SecurityContext securityContext) throws NotFoundException {
    Map<String, Object> nbnRecord = new HashMap<>();
    if (nbnValidator.validate(identifier)) {
      List<String> locations = dao.getLocations(identifier);
      if (locations.isEmpty()) {
        return Response.status(404).entity(new ApiResponseMessage(ApiResponseMessage.INFO, "Supplied URN:NBN identifier not found")).build();
      }
      else {
        nbnRecord.put("identifier", identifier);
        nbnRecord.put("locations", locations);
        return Response.status(200, "OK").entity(nbnRecord).build();
      }
    }
    else {
      return Response.status(400).entity(new ApiResponseMessage(ApiResponseMessage.INFO, "Invalid URN:NBN identifier supplied")).build();
    }
  }

  @Override
  public Response updateNbnRecord(List<String> body, String identifier, SecurityContext securityContext) throws NotFoundException {
    Response response = null;
    NbnLocationsObject nbnLocationsObject = new NbnLocationsObject();
    nbnLocationsObject.setIdentifier(identifier);
    nbnLocationsObject.setLocations(body);
    SqlResponse result = dao.createOrUpdateNbn(nbnLocationsObject);
    switch (result) {
      case UPDATE:
        response = Response.status(200).entity(new ApiResponseMessage(ApiResponseMessage.INFO, "OK (updated existing)")).build();
        break;
      case OK:
        response = Response.status(201).entity(new ApiResponseMessage(ApiResponseMessage.INFO, "Successful operation (created new)")).build();
        break;
      case DUPLICATE:
        response = Response.status(409).entity(new ApiResponseMessage(ApiResponseMessage.INFO, "Conflict, resource already exists")).build();
        break;
      //Todo: what is response for general SQL insert failure?
      case FAILURE:
        response = Response.status(400).entity(new ApiResponseMessage(ApiResponseMessage.INFO, "Nbn could not be registered")).build();
        break;
    }
    return response;
  }

}
