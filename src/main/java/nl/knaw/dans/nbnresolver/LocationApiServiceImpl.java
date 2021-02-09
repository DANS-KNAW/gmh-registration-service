package nl.knaw.dans.nbnresolver;

import io.swagger.api.LocationApiService;
import nl.knaw.dans.nbnresolver.response.OperationResult;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-01-08T12:34:19.815Z[GMT]")
public class LocationApiServiceImpl extends LocationApiService {

  NbnLocationApp app = new NbnLocationApp();

  @Override
  public Response getNbnByLocation(String location, SecurityContext securityContext) {
    OperationResult result = app.doGetNbnByLocation(location);
    return Response.status(result.getStatus()).entity(result.getResponseBody()).build();
  }
}
