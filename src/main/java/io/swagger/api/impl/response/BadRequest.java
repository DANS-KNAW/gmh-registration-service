package io.swagger.api.impl.response;

import io.swagger.api.ApiResponseMessage;

import javax.ws.rs.core.Response;

public class BadRequest implements OperationResult {

  String identifier;

  public BadRequest(String identifier) {
    this.identifier = identifier;
  }

  @Override
  public Response.Status getStatus() {
    return Response.Status.BAD_REQUEST;
  }

  @Override
  public Object getResponseBody() {
    return new ApiResponseMessage(ApiResponseMessage.INFO, "Invalid URN:NBN identifier supplied: " + identifier);
  }
}
