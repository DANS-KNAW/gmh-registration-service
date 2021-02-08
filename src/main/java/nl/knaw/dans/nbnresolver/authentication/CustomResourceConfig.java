package nl.knaw.dans.nbnresolver.authentication;

import org.glassfish.jersey.server.ResourceConfig;

public class CustomResourceConfig extends ResourceConfig {

  public CustomResourceConfig() {
    packages("nl.knaw.dans.nbnresolver.authentication");
    register(AuthenticationFilter.class);
  }
}
