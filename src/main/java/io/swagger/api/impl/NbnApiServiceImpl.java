package io.swagger.api.impl;

import io.swagger.api.ApiResponseMessage;
import io.swagger.api.NbnApiService;
import io.swagger.api.NotFoundException;
import io.swagger.api.impl.jdbc.Dao;
import io.swagger.model.NbnLocationsObject;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-01-08T12:34:19.815Z[GMT]")
public class NbnApiServiceImpl extends NbnApiService {

  Dao dao = new Dao();
  NbnValidator nbnValidator = new NbnValidator();

  @Override
  public Response createNbnLocations(NbnLocationsObject body, SecurityContext securityContext) throws NotFoundException {
    // do some magic!
    return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
  }

  @Override
  public Response getLocationsByNbn(String identifier, SecurityContext securityContext) throws NotFoundException {
    if (nbnValidator.validate(identifier)) {
      List<String> locations = dao.getLocations(identifier);
      return Response.ok().entity(locations).build();
    }
    return Response.status(400).entity(new ApiResponseMessage(400, "Invalid URN:NBN identifier pattern or location uri(s) supplied")).build();
  }

  @Override
  public Response getNbnRecord(String identifier, SecurityContext securityContext) throws NotFoundException {
    return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
  }

  @Override
  public Response updateNbnRecord(List<String> body, String identifier, SecurityContext securityContext) throws NotFoundException {
    // do some magic!
    return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
  }
}
