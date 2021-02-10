package nl.knaw.dans.nbnresolver.response;

import static javax.ws.rs.core.Response.Status;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

public class Unauthorized implements OperationResult {

  public Unauthorized() {
  }

  @Override
  public Status getStatus() {
    return UNAUTHORIZED;
  }

  @Override
  public Object getResponseBody() {
    return new ResponseMessage(UNAUTHORIZED.getStatusCode(), "Authentication information is missing or invalid");
  }
}
