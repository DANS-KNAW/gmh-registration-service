package nl.knaw.dans.nbnresolver.response;

import static javax.ws.rs.core.Response.Status;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

public class NotFound implements OperationResult {

  private String identifier;

  public NotFound(String identifier) {
    this.identifier = identifier;
  }

  @Override
  public Status getStatus() {
    return NOT_FOUND;
  }

  @Override
  public Object getResponseBody() {
    return new ResponseMessage(NOT_FOUND.getStatusCode(), "Supplied URN:NBN identifier not found: " + identifier);
  }
}

