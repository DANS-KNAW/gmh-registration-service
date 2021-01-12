package io.swagger.api;

import io.swagger.model.NbnLocationsObject;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-01-08T12:34:19.815Z[GMT]")
public abstract class NbnApiService {

  public abstract Response createNbnLocations(NbnLocationsObject body, SecurityContext securityContext) throws NotFoundException;

  public abstract Response getLocationsByNbn(String identifier, SecurityContext securityContext) throws NotFoundException;

  public abstract Response getNbnRecord(String identifier, SecurityContext securityContext) throws NotFoundException;

  public abstract Response updateNbnRecord(List<String> body, String identifier, SecurityContext securityContext) throws NotFoundException;
}
