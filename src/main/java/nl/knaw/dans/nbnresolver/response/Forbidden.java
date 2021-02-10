package nl.knaw.dans.nbnresolver.response;

import static javax.ws.rs.core.Response.Status;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;

public class Forbidden implements OperationResult {

  public Forbidden() {
  }

  @Override
  public Status getStatus() {
    return FORBIDDEN;
  }

  @Override
  public Object getResponseBody() {
    return new ResponseMessage(FORBIDDEN.getStatusCode(), "URN:NBN identifier is valid, but does not match the prefix of the authenticated user");
  }
}
