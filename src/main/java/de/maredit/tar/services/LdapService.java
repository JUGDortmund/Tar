package de.maredit.tar.services;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

import de.maredit.tar.configs.LdapConfig;
import de.maredit.tar.models.User;
import de.maredit.tar.repositories.UserRepository;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LdapService {

  @Autowired
  private LdapConfig ldapConfig;

  @Autowired
  UserRepository userRepository;

  private static final Logger log = LoggerFactory.getLogger(LdapService.class);

  public void synchronizeLdapUser() {
    try {
      LDAPConnection
          ldapConnection =
          new LDAPConnection(new SSLUtil(new TrustAllTrustManager()).createSSLSocketFactory(),
                             ldapConfig.getHost(), ldapConfig.getPort(),
                             ldapConfig.getReadUser(), ldapConfig.getReadPassword());
      // get value list with userDN
      SearchResultEntry searchResultEntry = ldapConnection.getEntry(ldapConfig.getApllicationUserDN());
      String[] members = searchResultEntry.getAttributeValues("member");

      // iterate over userDN and create/update users
      List<SearchResultEntry> userEntries = new ArrayList();
      for( String member : members) {
        SearchResultEntry userEntry = ldapConnection.getEntry(member);
        if (userEntry != null) {
          User user = userRepository.findByUidNumber(userEntry.getAttributeValue("uidNumber"));
          if(user == null) {
            user = createUser(userEntry);
          } else {
            updateUser(userEntry, user);
          }
          userRepository.save(user);
        }
      }

      List<User> applicationUsers = userRepository.findAll();

      for(User applicationUser : applicationUsers ) {
      }

      ldapConnection.close();
    } catch (LDAPException | GeneralSecurityException e) {
      log.error("Error reading user list from LDAP", e);
    }

  }

  private void updateUser(SearchResultEntry resultEntry, User user) {
    // set name changes here
    user.setMail(resultEntry.getAttributeValue("mail"));
    user.setUsername(resultEntry.getAttributeValue("uid"));
    user.setFirstName(resultEntry.getAttributeValue("cn"));
    user.setLastName(resultEntry.getAttributeValue("sn"));
    if(log.isDebugEnabled()) {
      log.debug("User modified. username:" + user.getUsername()+"/uidNumber:" + user.getUidNumber());
    }
  }

  private User createUser(SearchResultEntry resultEntry) {
    User user;
    user = new User();
    user.setMail(resultEntry.getAttributeValue("mail"));
    user.setUidNumber(resultEntry.getAttributeValue("uidNumber"));
    user.setUsername(resultEntry.getAttributeValue("uid"));
    user.setFirstName(resultEntry.getAttributeValue("cn"));
    user.setLastName(resultEntry.getAttributeValue("sn"));
    user.setActive(Boolean.TRUE);

    if(log.isDebugEnabled()) {
      log.debug("User created. username:" + user.getUsername()+ "/uidNumber:" + user.getUidNumber());
    }
    return user;
  }

  public boolean authenticateUser(String uid, String password) {
    try {
      LDAPConnection
          ldapConnection =
          new LDAPConnection(new SSLUtil(new TrustAllTrustManager()).createSSLSocketFactory(),
                             ldapConfig.getHost(), ldapConfig.getPort(),
                             "uid=" + uid + "," + ldapConfig.getUserLookUpDN(), password);
      ldapConnection.close();
      return true;
    } catch (LDAPException | GeneralSecurityException e) {
      log.error("Error during LDAP authentication", e);
      return false;
    }
  }

}
