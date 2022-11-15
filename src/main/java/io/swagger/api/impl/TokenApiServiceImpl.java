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
package io.swagger.api.impl;

import io.swagger.api.NotFoundException;
import io.swagger.api.TokenApiService;
import io.swagger.model.Credentials;

import nl.knaw.dans.nbnresolver.jdbc.Dao;
import nl.knaw.dans.nbnresolver.jdbc.InvalidCredentialsException;
import nl.knaw.dans.nbnresolver.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-10-27T13:45:58.722388+02:00[Europe/Amsterdam]")
public class TokenApiServiceImpl extends TokenApiService {

  User user;
  private static final Logger logger = LoggerFactory.getLogger(TokenApiServiceImpl.class);
  private static final SecureRandom secureRandom = new SecureRandom();
  private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

  @Override
  public Response token(Credentials body, SecurityContext securityContext) throws NotFoundException {
    String username = body.getUsername();
    String password = body.getPassword();
    System.out.println("USER: " + username);

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
    catch (InvalidCredentialsException e) {
      return Response.status(FORBIDDEN).entity("Authentication failed: invalid credentials").build();
    }
    catch (SQLException ex) {
      logger.error("Database error: " + ex.getMessage());
      logger.debug(ex.getMessage());
      return Response.status(INTERNAL_SERVER_ERROR).entity("Internal server error").build();
    }
  }

  private void authenticate(String username, String password) throws InvalidCredentialsException, SQLException {
    // Throw an Exception if the credentials are invalid
    user = Dao.getUserByCredentials(username, password);
  }

  private String issueToken() {
    byte[] randomBytes = new byte[48];
    secureRandom.nextBytes(randomBytes);
    return base64Encoder.encodeToString(randomBytes);
  }

}
