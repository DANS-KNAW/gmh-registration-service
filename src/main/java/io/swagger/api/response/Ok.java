package io.swagger.api.response;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.*;

public class Ok implements OperationResult {

  private Object responseBody;

  public Ok(Object responseBody) {
    this.responseBody = responseBody;
  }

  @Override
  public Status getStatus() {
    return Status.OK;
  }

  @Override
  public Object getResponseBody() {
    return responseBody;
  }

}
