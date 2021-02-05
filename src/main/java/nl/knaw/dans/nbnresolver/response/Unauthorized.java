package nl.knaw.dans.nbnresolver.response;

import io.swagger.api.ApiResponseMessage;

import static javax.ws.rs.core.Response.Status;

public class Unauthorized implements OperationResult {

  public Unauthorized() {}

  @Override
  public Status getStatus() {
    return Status.UNAUTHORIZED;
  }

  @Override
  public Object getResponseBody() {
    return new ApiResponseMessage(ApiResponseMessage.INFO, "Authentication information is missing or invalid");
  }
}
