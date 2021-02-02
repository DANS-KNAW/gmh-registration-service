package io.swagger.api.impl.response;

import io.swagger.api.ApiResponseMessage;

import static javax.ws.rs.core.Response.Status;

public class Forbidden implements OperationResult {

  public Forbidden() {}

  @Override
  public Status getStatus() {
    return Status.FORBIDDEN;
  }

  @Override
  public Object getResponseBody() {
    return new ApiResponseMessage(ApiResponseMessage.INFO, "URN:NBN identifier is valid, but does not match the prefix of the authenticated user");
  }
}
