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

  private String applicationSupervisorDN;

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

  public String getReadUser() {
    return readUser;
  }

  public String getReadPassword() {
    return readPassword;
  }

  public String getApplicationSupervisorDN() {
    return applicationSupervisorDN;
  }

  public String getApplicationUserDN() {
    return applicationUserDN;
  }

  public String getUserBindDN() {
    return userBindDN;
  }

  public String getGroupLookUpDN() {
    return groupLookUpDN;
  }

  public boolean isDisableSSL() {
    return disableSSL;
  }

  public String getGroupLookUpAttribute() {
    return groupLookUpAttribute;
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

  public void setReadUser(String readUser) {
    this.readUser = readUser;
  }

  public void setReadPassword(String readPassword) {
    this.readPassword = readPassword;
  }

  public void setApplicationSupervisorDN(String applicationSupervisorDN) {
    this.applicationSupervisorDN = applicationSupervisorDN;
  }

  public void setGroupLookUpAttribute(String groupLookUpAttribute) {
    this.groupLookUpAttribute = groupLookUpAttribute;
  }

  public void setUserBindDN(String userBindDN) {
    this.userBindDN = userBindDN;
  }

  public void setApplicationUserDN(String applicationUserDN) {
    this.applicationUserDN = applicationUserDN;
  }

  public void setGroupLookUpDN(String groupLookUpDN) {
    this.groupLookUpDN = groupLookUpDN;
  }

  public void setDisableSSL(boolean disableSSL) {
    this.disableSSL = disableSSL;
  }

}