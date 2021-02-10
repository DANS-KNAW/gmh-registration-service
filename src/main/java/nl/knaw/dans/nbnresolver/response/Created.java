package nl.knaw.dans.nbnresolver.response;

import io.swagger.api.ApiResponseMessage;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.CREATED;

public class Created implements OperationResult {

  private String identifier;

  public Created(String identifier) {
    this.identifier = identifier;
  }

  @Override
  public Response.Status getStatus() {
    return CREATED;
  }

  @Override
  public Object getResponseBody() {
    return new ResponseMessage(CREATED.getStatusCode(),"Successful operation (created new): " + identifier);
  }
}
