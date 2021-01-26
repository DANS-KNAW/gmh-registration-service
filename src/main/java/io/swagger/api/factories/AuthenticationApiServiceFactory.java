package io.swagger.api.factories;

import io.swagger.api.AuthenticationApiService;
import io.swagger.api.impl.AuthenticationApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-01-22T09:23:35.355Z[GMT]")public class AuthenticationApiServiceFactory {
    private final static AuthenticationApiService service = new AuthenticationApiServiceImpl();

    public static AuthenticationApiService getAuthenticationApi() {
        return service;
    }
}
