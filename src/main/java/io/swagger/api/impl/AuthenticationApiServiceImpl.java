package io.swagger.api.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.api.AuthenticationApiService;
import io.swagger.api.NotFoundException;
import io.swagger.model.Credentials;

import javax.crypto.SecretKey;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.ResourceBundle;

import static io.swagger.api.impl.authentication.AuthUtil.getSecretKey;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-01-22T09:23:35.355Z[GMT]")
public class AuthenticationApiServiceImpl extends AuthenticationApiService {

  private static String keyStorePwd;
  //private SecretKey key = generateKey();

  static {
    ResourceBundle properties = ResourceBundle.getBundle("application");
    keyStorePwd = properties.getString("KEYSTORE_PASSWORD");
  }

  @Override
  public Response authenticate(Credentials body, SecurityContext securityContext) throws NotFoundException {
    String username = body.getUsername();
    String password = body.getPassword();

    try {

      // Authenticate the user using the credentials provided
      authenticate(username, password);

      // Issue a token for the user
      String token = issueToken(username);

      //save secret key to keystore
      //saveKey(key, username);

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
    String jwtToken = Jwts.builder()
        .setSubject(username)
        .setId("UI:13")
        .setIssuer("BRI-GMH")
        .setIssuedAt(new Date())
        .setExpiration(Date.from(LocalDateTime.now().plusMinutes(60L).toInstant(ZoneOffset.UTC)))
        .signWith(SignatureAlgorithm.HS512, getSecretKey())
        .compact();
    return jwtToken;
  }

//  private void saveKey(SecretKey key, String entryName) {
//    KeyStore ks = null;
//    try {
//      ks = KeyStore.getInstance("JCEKS");
//      ks.load(new FileInputStream("keystore.jceks"), keyStorePwd.toCharArray());
//      KeyStore.SecretKeyEntry secret = new KeyStore.SecretKeyEntry(key);
//      //pwd voor single entries omitted
//      KeyStore.ProtectionParameter password = new KeyStore.PasswordProtection("".toCharArray());
//      ks.setEntry(entryName, secret, password);
//      FileOutputStream fos = new FileOutputStream("keystore.jceks");
//      ks.store(fos, keyStorePwd.toCharArray());
//    }
//    catch (KeyStoreException e) {
//      e.printStackTrace();
//    }
//    catch (CertificateException e) {
//      e.printStackTrace();
//    }
//    catch (NoSuchAlgorithmException e) {
//      e.printStackTrace();
//    }
//    catch (FileNotFoundException e) {
//      e.printStackTrace();
//    }
//    catch (IOException e) {
//      e.printStackTrace();
//    }
//  }

}


