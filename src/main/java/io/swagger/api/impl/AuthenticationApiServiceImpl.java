package io.swagger.api.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.api.AuthenticationApiService;
import io.swagger.api.NotFoundException;
import io.swagger.api.impl.jdbc.Dao;
import io.swagger.model.Credentials;
import io.swagger.model.User;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static io.swagger.api.impl.authentication.KeyUtil.getSecretKey;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-01-22T09:23:35.355Z[GMT]")
public class AuthenticationApiServiceImpl extends AuthenticationApiService {
  private static String TOKEN_ISSUER = "BRI-GMH";
  Dao dao = new Dao();
  User user;

  @Override
  public Response authenticate(Credentials body, SecurityContext securityContext) throws NotFoundException {
    String username = body.getUsername();
    String password = body.getPassword();

    try {

      // Authenticate the user using the credentials provided
      authenticate(username, password);

      // Issue a token for the user
      String token = issueToken();

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
    user = dao.getUser(username, password);
  }

  private String issueToken() {
    // Issue a token (can be a random String persisted to a database or a JWT token)
    // The issued token must be associated to a user
    // Return the issued token
    String jwtToken = Jwts.builder()
        .setSubject(user.getUserName())
        .setId(user.getOrgPrefix())
        .setIssuer(TOKEN_ISSUER)
        .setIssuedAt(new Date()).setExpiration(Date.from(LocalDateTime.now().plusMinutes(60L).toInstant(ZoneOffset.UTC)))
        .signWith(SignatureAlgorithm.HS512, getSecretKey())
        .compact();
    return jwtToken;
  }

}


