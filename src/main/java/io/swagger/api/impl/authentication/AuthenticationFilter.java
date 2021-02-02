package io.swagger.api.impl.authentication;

import io.jsonwebtoken.Jwts;
import io.swagger.api.response.Unauthorized;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

import static io.swagger.api.impl.authentication.KeyUtil.getSecretKey;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

  private static final String REALM = "example";
  private static final String AUTHENTICATION_SCHEME = "Bearer";

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {

    // Get the Authorization header from the request
    String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

    // Validate the Authorization header
    if (!isTokenBasedAuthentication(authorizationHeader)) {
      abortWithUnauthorized(requestContext);
      return;
    }

    // Extract the token from the Authorization header
    String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();

    try {

      // Validate the token
      validateToken(token, requestContext);

    }
    catch (Exception e) {
      abortWithUnauthorized(requestContext);
    }

    requestContext.setSecurityContext(new SecurityContext() {

      @Override
      public Principal getUserPrincipal() {
        return () -> getOrgPrefix(token);
      }

      @Override
      public boolean isUserInRole(String s) {
        return false;
      }

      //TODO" implement
      @Override
      public boolean isSecure() {
        return true;
      }

      @Override
      public String getAuthenticationScheme() {
        return "Token-Based-Auth-Scheme";
      }
    });
  }

  private boolean isTokenBasedAuthentication(String authorizationHeader) {

    // Check if the Authorization header is valid
    // It must not be null and must be prefixed with "Bearer" plus a whitespace
    // The authentication scheme comparison must be case-insensitive
    return authorizationHeader != null && authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
  }

  private void abortWithUnauthorized(ContainerRequestContext requestContext) {
    Unauthorized unauthorized = new Unauthorized();
    requestContext.abortWith(Response.status(unauthorized.getStatus()).entity(unauthorized.getResponseBody()).build());
  }

  private void validateToken(String token, ContainerRequestContext requestContext) throws Exception {

    // Check if the token was issued by the server and if it's not expired
    // Throw an Exception if the token is invalid
    // Validate the token

    try {
      // Validate the token
      Jwts.parser().setSigningKey(getSecretKey()).parseClaimsJws(token);

    }
    catch (Exception e) {
      abortWithUnauthorized(requestContext);
    }
  }

  private String getOrgPrefix(String token) {
    return Jwts.parser().setSigningKey(getSecretKey()).parseClaimsJws(token).getBody().getId();
  }
}
