package io.swagger.api.factories;

import io.swagger.api.NbnApiService;
import io.swagger.api.impl.NbnApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-01-08T12:34:19.815Z[GMT]")
public class NbnApiServiceFactory {

  private final static NbnApiService service = new NbnApiServiceImpl();

  public static NbnApiService getNbnApi() {
    return service;
  }
}
