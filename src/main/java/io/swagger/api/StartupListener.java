package io.swagger.api;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ResourceBundle;

@WebListener
public class StartupListener implements ServletContextListener {

  private static String keyStorePwd;

  static {
    ResourceBundle properties = ResourceBundle.getBundle("application");
    keyStorePwd = properties.getString("KEYSTORE_PASSWORD");
  }

  @Override
  public void contextInitialized(ServletContextEvent event) {
    // Perform action during application's startup
    
    try {
      KeyStore ks = KeyStore.getInstance("JCEKS");
      char[] pwdArray = keyStorePwd.toCharArray();
      ks.load(null, pwdArray);

      FileOutputStream fos = new FileOutputStream("keystore.jceks");
      ks.store(fos, pwdArray);
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
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {
    // Perform action during application's shutdown
  }
}