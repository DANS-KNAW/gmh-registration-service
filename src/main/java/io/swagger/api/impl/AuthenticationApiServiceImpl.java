package io.swagger.api.impl;

import io.swagger.api.ApiResponseMessage;
import io.swagger.api.AuthenticationApiService;
import io.swagger.api.NotFoundException;
import io.swagger.api.impl.jdbc.Dao;
import io.swagger.model.Credentials;
import io.swagger.model.User;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.SecureRandom;
import java.util.Base64;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-01-22T09:23:35.355Z[GMT]")
public class AuthenticationApiServiceImpl extends AuthenticationApiService {

  User user;
  private static final SecureRandom secureRandom = new SecureRandom();
  private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

  @Override
  public Response authenticate(Credentials body, SecurityContext securityContext) throws NotFoundException {
    String username = body.getUsername();
    String password = body.getPassword();

    try {
      // Authenticate the user using the credentials provided
      authenticate(username, password);

      // Issue a token for the user
      String token = issueToken();

      //persist token (overwrite existing token for this user)
      Dao.registerToken(token, username, password);

      // Return the token on the response
      return Response.ok(token).build();

    }
    catch (Exception e) {
      return Response.status(FORBIDDEN).entity(new ApiResponseMessage(ApiResponseMessage.ERROR, "Authentication failed: invalid credentials")).build();
    }
  }

  private void authenticate(String username, String password) throws Exception {
    // Authenticate against a database, LDAP, file or whatever
    // Throw an Exception if the credentials are invalid
    user = Dao.getUser(username, password);
  }

  private String issueToken() {
    byte[] randomBytes = new byte[48];
    secureRandom.nextBytes(randomBytes);
    return base64Encoder.encodeToString(randomBytes);
  }

}


