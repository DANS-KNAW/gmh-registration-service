package io.swagger.api.impl.authentication;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class KeyUtil {

  private static SecretKey secretKey;

  static {
    KeyGenerator keyGenerator;

    {
      try {
        keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom secRandom = new SecureRandom();
        keyGenerator.init(secRandom);
        secretKey = keyGenerator.generateKey();

      }
      catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      }
    }
  }

  public static SecretKey getSecretKey() {
    return secretKey;
  }
}





