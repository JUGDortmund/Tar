package de.maredit.tar.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.ldap")
public class LdapProperties {

  private String host;

  private int port;

  private String userLookUpDN;

  private String applicationUserDN;

  private String applicationTeamleaderDN;

  private String readUser;

  private String readPassword;

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getUserLookUpDN() {
    return userLookUpDN;
  }

  public String getReadUser() {
    return readUser;
  }

  public String getReadPassword() {
    return readPassword;
  }

  public String getApplicationUserDN() {
    return applicationUserDN;
  }

  public String getApplicationTeamleaderDN() {
    return applicationTeamleaderDN;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void setUserLookUpDN(String userLookUpDN) {
    this.userLookUpDN = userLookUpDN;
  }

  public void setApplicationUserDN(String applicationUserDN) {
    this.applicationUserDN = applicationUserDN;
  }

  public void setApplicationTeamleaderDN(String applicationTeamleaderDN) {
    this.applicationTeamleaderDN = applicationTeamleaderDN;
  }

  public void setReadUser(String readUser) {
    this.readUser = readUser;
  }

  public void setReadPassword(String readPassword) {
    this.readPassword = readPassword;
  }
}