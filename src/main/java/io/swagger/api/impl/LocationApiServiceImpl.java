package io.swagger.api.impl;

import io.swagger.api.ApiResponseMessage;
import io.swagger.api.LocationApiService;
import io.swagger.api.NotFoundException;
import io.swagger.api.impl.jdbc.Dao;
import io.swagger.api.impl.validation.LocationValidator;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

import static javax.ws.rs.core.Response.Status.*;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-01-08T12:34:19.815Z[GMT]")
public class LocationApiServiceImpl extends LocationApiService {

  LocationValidator locationValidator = new LocationValidator();
  Dao dao = new Dao();
  Response response;

  @Override
  public Response getNbnByLocation(String location, SecurityContext securityContext) throws NotFoundException {
    Response response = null;
    List<String> result = null;
    result = dao.getNbnByLocation(location);
    if (result.size() > 0) {
      return Response.status(OK).entity(result).build();
    }
    else {
      return Response.status(NOT_FOUND).entity(new ApiResponseMessage(ApiResponseMessage.INFO, "Object (location) not found: " + location )).build();

    }

  }
}
