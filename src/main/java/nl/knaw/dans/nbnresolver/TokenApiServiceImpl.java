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
package nl.knaw.dans.nbnresolver;

import io.swagger.api.ApiResponseMessage;
import io.swagger.api.NotFoundException;
import io.swagger.api.TokenApiService;
import nl.knaw.dans.nbnresolver.jdbc.Dao;
import io.swagger.model.Credentials;
import io.swagger.model.User;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.SecureRandom;
import java.util.Base64;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-02-03T12:44:06.016Z[GMT]")
public class TokenApiServiceImpl extends TokenApiService {


  User user;
  private static final SecureRandom secureRandom = new SecureRandom();
  private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

  @Override
  public Response token (Credentials body, SecurityContext securityContext) throws NotFoundException {
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
    // Throw an Exception if the credentials are invalid
    user = Dao.getUserByCredentials(username, password);
  }

  private String issueToken() {
    byte[] randomBytes = new byte[48];
    secureRandom.nextBytes(randomBytes);
    return base64Encoder.encodeToString(randomBytes);
  }

}


