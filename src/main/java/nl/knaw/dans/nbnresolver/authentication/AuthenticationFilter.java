/**
 * Copyright (C) 2018 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.nbnresolver.authentication;

import io.swagger.model.User;
import nl.knaw.dans.nbnresolver.jdbc.Dao;
import nl.knaw.dans.nbnresolver.jdbc.InvalidTokenException;
import nl.knaw.dans.nbnresolver.response.InternalServerError;
import nl.knaw.dans.nbnresolver.response.Unauthorized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.security.Principal;
import java.sql.SQLException;

//See: https://cassiomolin.com/2014/11/06/token-based-authentication-with-jaxrs-20/

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

  private static final String AUTHENTICATION_SCHEME = "Bearer";
  private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

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

    final SecurityContext currentSecurityContext = requestContext.getSecurityContext();

    requestContext.setSecurityContext(new SecurityContext() {

      @Override
      public Principal getUserPrincipal() {
        return currentUser::getOrgPrefix;
      }

      @Override
      public boolean isUserInRole(String role) {
        return role.equalsIgnoreCase("ltp") && currentUser.isLTP();
      }

      @Override
      public boolean isSecure() {
        return currentSecurityContext.isSecure();
      }

      @Override
      public String getAuthenticationScheme() {
        return "Bearer";
      }
    });
  }

  private boolean isTokenBasedAuthentication(String authorizationHeader) {
    return authorizationHeader != null && authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
  }

  private void abortWithUnauthorized(ContainerRequestContext requestContext) {
    Unauthorized unauthorized = new Unauthorized();
    requestContext.abortWith(Response.status(unauthorized.getStatus()).entity(unauthorized.getResponseBody()).build());
  }

  private void abortWithInternalServerError(ContainerRequestContext requestContext, Exception e) {
    logger.error("Database error: " + e.getMessage());
    InternalServerError internalServerError = new InternalServerError();
    requestContext.abortWith(Response.status(internalServerError.getStatus()).entity(internalServerError.getResponseBody()).build());
  }

  // Validates by getting the current User by the token from the request.
  // If the db returns no result it means the token is not in the db and therefore not valid.
  private User validateToken(String token, ContainerRequestContext requestContext) {

    User currentUser = null;

    try {
      currentUser = Dao.getUserByToken(token);
    }
    catch (InvalidTokenException e) {
      abortWithUnauthorized(requestContext);
    }
    catch (SQLException ex) {
      abortWithInternalServerError(requestContext, ex);
    }
    return currentUser;
  }

}
