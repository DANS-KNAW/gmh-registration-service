package nl.knaw.dans.nbnresolver.authentication;

import nl.knaw.dans.nbnresolver.jdbc.Dao;
import nl.knaw.dans.nbnresolver.response.Unauthorized;
import io.swagger.model.User;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.security.Principal;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

  private static final String AUTHENTICATION_SCHEME = "Bearer";

  @Override
  public void filter(ContainerRequestContext requestContext) {

    // Get the Authorization header from the request
    String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

    // Validate the Authorization header
    if (!isTokenBasedAuthentication(authorizationHeader)) {
      abortWithUnauthorized(requestContext);
      return;
    }

    // Extract the token from the Authorization header
    String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();

    // Validate the token
    User currentUser = validateToken(token, requestContext);

    requestContext.setSecurityContext(new SecurityContext() {

      @Override
      public Principal getUserPrincipal() {
        //TODO: refactor
        return currentUser::getOrgPrefix;
      }

      @Override
      public boolean isUserInRole(String s) {
        return false;
      }

      @Override
      public boolean isSecure() {
        return false;
      }

      @Override
      public String getAuthenticationScheme() {
        return null;
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

  // Validates by getting the current User by the token from the request. If the db returns no result it means the token is not in the db and therefore not valid.
  private User validateToken(String token, ContainerRequestContext requestContext) {

    User currentUser = null;

    try {
      currentUser = Dao.getUserByToken(token);
    }
    catch (Exception e) {
      abortWithUnauthorized(requestContext);
    }
    return currentUser;
  }

}
