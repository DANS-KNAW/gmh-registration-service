package io.swagger.api.impl.authentication;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

@Path("/authentication")
public class AuthenticationEndpoint {

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response authenticateUser(Credentials credentials) {
    String username = credentials.getUsername();
    String password = credentials.getPassword();

    try {

      // Authenticate the user using the credentials provided
      authenticate(username, password);

      // Issue a token for the user
      String token = issueToken(username);

      // Return the token on the response
      return Response.ok(token).build();

    }
    catch (Exception e) {
      return Response.status(Response.Status.FORBIDDEN).build();
    }
  }

  private void authenticate(String username, String password) throws Exception {
    // Authenticate against a database, LDAP, file or whatever
    // Throw an Exception if the credentials are invalid
    System.out.println("username and password ok!");
  }

  private String issueToken(String username) {
    // Issue a token (can be a random String persisted to a database or a JWT token)
    // The issued token must be associated to a user
    // Return the issued token

    Random random = new SecureRandom();
    String token = new BigInteger(130, random).toString(32);
    System.out.println("Token generated:  " + token);
    return token;
  }
}
