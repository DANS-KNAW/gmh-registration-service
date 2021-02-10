package nl.knaw.dans.nbnresolver.response;

import io.swagger.api.ApiResponseMessage;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.*;
import static javax.ws.rs.core.Response.Status.*;

public class BadRequest implements OperationResult {

  String identifier;

  public BadRequest(String identifier) {
    this.identifier = identifier;
  }

  @Override
  public Status getStatus() {
    return BAD_REQUEST;
  }

  @Override
  public Object getResponseBody() {
    return new ResponseMessage(BAD_REQUEST.getStatusCode(), "Invalid URN:NBN identifier pattern or location uri(s) supplied: " + identifier);
  }
}
