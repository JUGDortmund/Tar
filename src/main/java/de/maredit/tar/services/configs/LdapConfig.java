package de.maredit.tar.services.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.ldap")
public class LdapConfig {

  private String host;

  private int port;

  private String userLookUpDN;

  private String apllicationUserDN;

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

  public String getApllicationUserDN() {
    return apllicationUserDN;
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

  public void setApllicationUserDN(String apllicationUserDN) {
    this.apllicationUserDN = apllicationUserDN;
  }

  public void setReadUser(String readUser) {
    this.readUser = readUser;
  }

  public void setReadPassword(String readPassword) {
    this.readPassword = readPassword;
  }
}