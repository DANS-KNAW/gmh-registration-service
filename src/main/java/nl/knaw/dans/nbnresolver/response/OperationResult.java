package nl.knaw.dans.nbnresolver.response;

import static javax.ws.rs.core.Response.*;

public interface OperationResult {

  public Status getStatus();

  public Object getResponseBody();

}
