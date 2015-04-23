package de.maredit.tar.services;


import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

import de.maredit.tar.configs.LdapConfig;
import de.maredit.tar.models.User;
import de.maredit.tar.repositories.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class LdapServiceImpl implements LdapService {

  @Autowired
  private LdapConfig ldapConfig;

  @Autowired
  UserRepository userRepository;

  private static final Logger LOG = LoggerFactory.getLogger(LdapServiceImpl.class);

  private LDAPConnectionPool connectionPool;

  @PostConstruct
  public void init() throws LDAPException, GeneralSecurityException {
    LDAPConnection ldapConnection = null;
    ldapConnection =
        new LDAPConnection(new SSLUtil(new TrustAllTrustManager()).createSSLSocketFactory(),
                           ldapConfig.getHost(), ldapConfig.getPort(),
                           ldapConfig.getReadUser(), ldapConfig.getReadPassword());

    connectionPool = new LDAPConnectionPool(ldapConnection, 10);
  }

  @PreDestroy
  public void destroy() throws LDAPException, GeneralSecurityException {
    connectionPool.close();
  }

  /**
   * Method to synchronize the system user objects with the LDAP user
   */
  @Override
  public void synchronizeLdapUser() throws LDAPException {
    LDAPConnection ldapConnection = null;
    try {
      ldapConnection = connectionPool.getConnection();
      // get value list with userDN
      SearchResultEntry
          searchResultEntry =
          ldapConnection.getEntry(ldapConfig.getApllicationUserDN());
      String[] members = searchResultEntry.getAttributeValues("member");

      // iterate over userDN and create/update users
      List<String> editedUser = new ArrayList();

      for (String member : members) {
        SearchResultEntry userEntry = ldapConnection.getEntry(member);
        if (userEntry != null) {
          User user = userRepository.findByUidNumber(userEntry.getAttributeValue("uidNumber"));
          if (user == null) {
            user = createUser(userEntry);
          } else {
            updateUser(userEntry, user);
          }
          userRepository.save(user);
          editedUser.add(user.getUidNumber());
        }

      }

      // iterate over all users from repository and delete if they are not part of the LDAP
      deactivateDeletedLdapUser(editedUser);
      connectionPool.releaseConnection(ldapConnection);
    } catch (LDAPException e) {
      LOG.error("Error reading user list from LDAP", e);
      connectionPool.releaseConnectionAfterException(ldapConnection, e);
      throw e;
    }
  }

  private void deactivateDeletedLdapUser(List<String> editedUser) {
    List<User> applicationUsers = userRepository.findAll();

    //iterate over all users currently in the application
    for (User applicationUser : applicationUsers) {
      if (!editedUser.contains(applicationUser.getUidNumber())) {
        applicationUser.setActive(false);
        userRepository.save(applicationUser);
      }
    }
  }

  private void updateUser(SearchResultEntry resultEntry, User user) {
    // set name changes here
    user.setMail(resultEntry.getAttributeValue("mail"));
    user.setUsername(resultEntry.getAttributeValue("uid"));
    user.setFirstName(resultEntry.getAttributeValue("cn"));
    user.setLastName(resultEntry.getAttributeValue("sn"));
    LOG.debug("User updated. username: %s/uidNumber: %s", user.getUsername(), user.getUidNumber());
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

    LOG.debug("User created. username: %s/uidNumber: %s", user.getUsername(), user.getUidNumber());
    return user;
  }

  @Override
  public boolean authenticateUser(String uid, String password) throws LDAPException {
    BindResult bindResult =
        connectionPool.bind("uid=" + uid + ",ou=users,o=maredit,dc=de", password);
    return bindResult.getResultCode().equals(ResultCode.SUCCESS);
  }

  @Override
  public List<String> getUserGroups(String uid) throws LDAPException {
    List<String> groups = new ArrayList<>();
    LDAPConnection ldapConnection = connectionPool.getConnection();
    try {
      ldapConnection.bind("cn=read,cn=ldap,ou=services,o=maredit,dc=de", "7Ey8vHzPPZrdWA");
      SearchRequest searchRequest =
          new SearchRequest("ou=groups,o=maredit,dc=de", SearchScope.SUBORDINATE_SUBTREE,
                            Filter.createEqualityFilter("memberUid", uid));
      SearchResult searchResults = ldapConnection.search(searchRequest);

      if (searchResults.getEntryCount() > 0) {
        for (SearchResultEntry entry : searchResults.getSearchEntries()) {
          groups.add(entry.getAttribute("cn").getValue());
        }
      }
    } catch (LDAPException e) {
      connectionPool.releaseConnectionAfterException(ldapConnection, e);
      throw e;
    }
    connectionPool.releaseConnection(ldapConnection);
    return groups;
  }

}
