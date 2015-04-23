package de.maredit.tar.services;


import com.unboundid.ldap.sdk.LDAPException;
import de.maredit.tar.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Profile("dev")
public class LdapDummyServiceImpl implements LdapService {

  @Autowired

  private static final Logger LOG = LoggerFactory.getLogger(LdapDummyServiceImpl.class);

  @Override
  public List<User> getUsers() throws LDAPException {
    List<User> users = new ArrayList<>();

    return users;

  }

   @Override
  public boolean authenticateUser(String uid, String password) throws LDAPException {
    return "test".equals(uid) && "test".equals(password);
  }

  @Override
  public List<String> getUserGroups(String uid) throws LDAPException {
    List<String> groups = new ArrayList<>();

    return groups;
  }
  
}
