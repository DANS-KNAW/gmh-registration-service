package io.swagger.api.impl.authentication;

import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.ResourceBundle;

public class KeystoreUtil {

  private String keyStorePwd;

  public KeystoreUtil() {

    ResourceBundle properties = ResourceBundle.getBundle("application");
    keyStorePwd = properties.getString("KEYSTORE_PASSWORD");
  }

  public Key getSigningKey(String entryName) {
    Key signingKey = null;
    KeyStore ks = null;
    try {
      ks = KeyStore.getInstance("JCEKS");
      ks.load(new FileInputStream("keystore.jceks"), keyStorePwd.toCharArray());
      signingKey = ks.getKey("username", "".toCharArray());
    }
    catch (KeyStoreException e) {
      e.printStackTrace();
    }
    catch (CertificateException e) {
      e.printStackTrace();
    }
    catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    catch (UnrecoverableKeyException e) {
      e.printStackTrace();
    }
    System.out.println("return: " + signingKey);
    return signingKey;
  }

}
