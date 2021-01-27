package io.swagger.model;

public class User {

  private String orgPrefix;
  private String userName;
  private String password;

  public User() {
  }

  public User(String orgPrefix, String userName, String password) {
    this.orgPrefix = orgPrefix;
    this.userName = userName;
    this.password = password;
  }

  public String getOrgPrefix() {
    return orgPrefix;
  }

  public void setOrgPrefix(String orgPrefix) {
    this.orgPrefix = orgPrefix;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
