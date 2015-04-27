package de.maredit.tar.services.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.ldap")
public class LdapConfig {

  private String host;

  private int port;

  private String userLookUpDN;

  private String applicationUserDN;

  private String readUser;

  private String readPassword;

  private String groupLookUpDN;
  
  private String groupLookUpAttribute;
  
  private String userBindDN;
  
  private boolean disableSSL;

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getUserLookUpDN() {
    return userLookUpDN;
  }

  public void setUserLookUpDN(String userLookUpDN) {
    this.userLookUpDN = userLookUpDN;
  }

  public String getReadUser() {
    return readUser;
  }

  public void setReadUser(String readUser) {
    this.readUser = readUser;
  }

  public String getReadPassword() {
    return readPassword;
  }

  public void setReadPassword(String readPassword) {
    this.readPassword = readPassword;
  }

  public String getApplicationUserDN() {
    return applicationUserDN;
  }
 
  public void setApplicationUserDN(String applicationUserDN) {
    this.applicationUserDN = applicationUserDN;
  }

  public String getGroupLookUpDN() {
    return groupLookUpDN;
  }

  public void setGroupLookUpDN(String groupLookUpDN) {
    this.groupLookUpDN = groupLookUpDN;
  }

  public String getGroupLookUpAttribute() {
    return groupLookUpAttribute;
  }

  public void setGroupLookUpAttribute(String groupLookUpAttribute) {
    this.groupLookUpAttribute = groupLookUpAttribute;
  }

  public String getUserBindDN() {
    return userBindDN;
  }

  public void setUserBindDN(String userBindDN) {
    this.userBindDN = userBindDN;
  }

  public boolean isDisableSSL() {
    return disableSSL;
  }

  public void setDisableSSL(boolean disableSSL) {
    this.disableSSL = disableSSL;
  }

}