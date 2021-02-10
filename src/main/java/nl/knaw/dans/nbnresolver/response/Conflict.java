package nl.knaw.dans.nbnresolver.response;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.CONFLICT;

public class Conflict implements OperationResult {

  private String identifier;

  public Conflict(String identifier) {
    this.identifier = identifier;
  }

  @Override
  public Response.Status getStatus() {
    return CONFLICT;
  }

  @Override
  public Object getResponseBody() {
    return new ResponseMessage(CONFLICT.getStatusCode(), "Conflict, resource already exists: " + identifier);
  }
}
