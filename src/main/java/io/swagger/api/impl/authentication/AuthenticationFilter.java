package io.swagger.api.impl.authentication;

import io.jsonwebtoken.Jwts;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import static io.jsonwebtoken.impl.crypto.MacProvider.generateKey;
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
  }

  private boolean isTokenBasedAuthentication(String authorizationHeader) {

    // Check if the Authorization header is valid
    // It must not be null and must be prefixed with "Bearer" plus a whitespace
    // The authentication scheme comparison must be case-insensitive
    return authorizationHeader != null && authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
  }

  private void abortWithUnauthorized(ContainerRequestContext requestContext) {

    // Abort the filter chain with a 401 status code response
    // The WWW-Authenticate header is sent along with the response
    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"").build());
  }

  private void validateToken(String token, ContainerRequestContext requestContext) throws Exception {

    // Check if the token was issued by the server and if it's not expired
    // Throw an Exception if the token is invalid
    // Validate the token

    try {
      // Validate the token
      Jwts.parser()
          .setSigningKey(getSecretKey())
          .parseClaimsJws(token);
    }
    catch (Exception e) {
      abortWithUnauthorized(requestContext);
    }
  }
}
