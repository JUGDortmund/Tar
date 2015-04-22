package de.maredit.tar.services;

import com.unboundid.ldap.sdk.LDAPConnection;

import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchResultEntry;
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
public class LdapService {

  @Autowired
  private LdapConfig ldapConfig;

  private static final Logger LOG = LoggerFactory.getLogger(LdapService.class);
  private LDAPConnectionPool connectionPool;

  @PostConstruct
  public void init() throws LDAPException, GeneralSecurityException {
    LDAPConnection ldapConnection = null;
    ldapConnection =
        new LDAPConnection(new SSLUtil(new TrustAllTrustManager()).createSSLSocketFactory(),
            "ldap-master.maredit.net", 636);
    connectionPool = new LDAPConnectionPool(ldapConnection, 10);
  }

  @PreDestroy
  public void destroy() throws LDAPException, GeneralSecurityException {
    connectionPool.close();
  }

  @Autowired
  UserRepository userRepository;

  private static final Logger log = LoggerFactory.getLogger(LdapService.class);

  public void synchronizeLdapUser() throws LDAPException {
    LDAPConnection ldapConnection = connectionPool.getConnection();
    try {
      ldapConnection.bind(ldapConfig.getReadUser(), ldapConfig.getReadPassword()); // get value list
                                                                                   // with userDN
      SearchResultEntry searchResultEntry =
          ldapConnection.getEntry(ldapConfig.getApllicationUserDN());
      String[] members = searchResultEntry.getAttributeValues("member");

      // iterate over userDN and create/update users
      List<SearchResultEntry> userEntries = new ArrayList();
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
        }
      }

      List<User> applicationUsers = userRepository.findAll();

      for (User applicationUser : applicationUsers) {
      }

      ldapConnection.close();
    } catch (LDAPException e) {
      log.error("Error reading user list from LDAP", e);
    }
  }

  private void updateUser(SearchResultEntry resultEntry, User user) {
    // set name changes here
    user.setMail(resultEntry.getAttributeValue("mail"));
    user.setUsername(resultEntry.getAttributeValue("uid"));
    user.setFirstName(resultEntry.getAttributeValue("cn"));
    user.setLastName(resultEntry.getAttributeValue("sn"));
    if (log.isDebugEnabled()) {
      log.debug("User modified. username:" + user.getUsername() + "/uidNumber:"
          + user.getUidNumber());
    }
  }

  public boolean authenticateUser(String uid, String password) throws LDAPException {
    BindResult bindResult =
        connectionPool.bind("uid=" + uid + ",ou=users,o=maredit,dc=de", password);
    return bindResult.getResultCode().equals(ResultCode.SUCCESS);
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

    if (log.isDebugEnabled()) {
      log.debug("User created. username:" + user.getUsername() + "/uidNumber:"
          + user.getUidNumber());
    }
    return user;
  }

  public List<String> getUserGroups(String uid) throws LDAPException {
    List<String> groups = new ArrayList<>();
    LDAPConnection ldapConnection = connectionPool.getConnection();
    try {
      ldapConnection.bind(ldapConfig.getReadUser(), ldapConfig.getReadPassword());
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
