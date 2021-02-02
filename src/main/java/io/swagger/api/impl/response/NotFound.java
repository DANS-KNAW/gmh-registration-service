package io.swagger.api.impl.response;

import io.swagger.api.ApiResponseMessage;

import static javax.ws.rs.core.Response.*;
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
    return new ApiResponseMessage(ApiResponseMessage.WARNING, "Supplied URN:NBN identifier not found: " + identifier);
  }
}

