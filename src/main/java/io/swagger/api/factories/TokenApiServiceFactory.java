package io.swagger.api.factories;

import io.swagger.api.TokenApiService;
import nl.knaw.dans.nbnresolver.TokenApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-02-03T12:44:06.016Z[GMT]")public class TokenApiServiceFactory {
    private final static TokenApiService service = new TokenApiServiceImpl();

    public static TokenApiService getTokenApi() {
        return service;
    }
}
