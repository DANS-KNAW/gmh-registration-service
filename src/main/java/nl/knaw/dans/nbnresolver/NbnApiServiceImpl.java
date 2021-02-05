package nl.knaw.dans.nbnresolver;

import io.swagger.api.NbnApiService;
import io.swagger.api.NotFoundException;
import nl.knaw.dans.nbnresolver.response.OperationResult;
import io.swagger.model.NbnLocationsObject;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-01-08T12:34:19.815Z[GMT]")
public class NbnApiServiceImpl extends NbnApiService {

  NbnLocationApp app = new NbnLocationApp();

  @Override
  public Response createNbnLocations(NbnLocationsObject body, SecurityContext securityContext) throws NotFoundException {
    OperationResult result = app.doCreateNbnLocations(body, securityContext);
    return Response.status(result.getStatus()).entity(result.getResponseBody()).build();
  }

  @Override
  public Response getNbnRecord(String identifier, SecurityContext securityContext) throws NotFoundException {
    OperationResult result = app.dogetNbnRecord(identifier, securityContext);
    return Response.status(result.getStatus()).entity(result.getResponseBody()).build();
  }

  @Override
  public Response updateNbnRecord(List<String> body, String identifier, SecurityContext securityContext) throws NotFoundException {
    OperationResult result = app.doUpdateNbnRecord(body, identifier, securityContext);
    return Response.status(result.getStatus()).entity(result.getResponseBody()).build();
  }

  @Override
  public Response getLocationsByNbn(String identifier, SecurityContext securityContext) throws NotFoundException {
    OperationResult result = app.doGetLocationsByNbn(identifier, securityContext);
    return Response.status(result.getStatus()).entity(result.getResponseBody()).build();
  }

}
