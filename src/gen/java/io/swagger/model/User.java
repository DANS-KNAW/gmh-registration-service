package io.swagger.model;

public class User {
 //TODO: add organisation name for information (join)
  private String orgPrefix;

  public User() {
  }

  public User(String orgPrefix, String userName, String password) {
    this.orgPrefix = orgPrefix;
  }

  public String getOrgPrefix() {
    return orgPrefix;
  }

  public void setOrgPrefix(String orgPrefix) {
    this.orgPrefix = orgPrefix;
  }

}
