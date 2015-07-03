package de.maredit.tar.services;


import com.unboundid.ldap.sdk.LDAPException;
import de.maredit.tar.data.User;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Profile({"dummyLdapService"})
@ConfigurationProperties(locations = "classpath:dummy-user.yaml")
public class LdapServiceDummyImpl implements LdapService {

  private List<User> users;

  private List<String> supervisors;

  private Map<String, String> authenticate;

  private Map<String, List<String>> groups;

  public Map<String, String> getAuthenticate() {
    return authenticate;
  }

  public void setAuthenticate(Map<String, String> authenticate) {
    this.authenticate = authenticate;
  }

  public Map<String, List<String>> getGroups() {
    return groups;
  }

  public void setGroups(Map<String, List<String>> groups) {
    this.groups = groups;
  }

  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }

  @Override
  public boolean authenticateUser(String uid, String password) throws LDAPException {
    return password.equals(authenticate.get(uid));
  }

  @Override
  public List<String> getUserGroups(String uid) throws LDAPException {
    return groups.get(uid);
  }

  @Override
  public List<User> getLdapUserList() throws LDAPException {
    final List<User> users = getUsers();
    return users;
  }

  @Override
  public Set<String> getLdapSupervisorList() throws LDAPException {
    return new HashSet<String>(getSupervisors());
  }

  public List<String> getSupervisors() {
    return supervisors;
  }

  public void setSupervisors(List<String> supervisors) {
    this.supervisors = supervisors;
  }
}
