package de.maredit.tar.services;


import org.springframework.context.annotation.Profile;

import de.maredit.tar.models.User;
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
import de.maredit.tar.services.configs.LdapConfig;
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
@Profile({"prod", "ldapTest"})
public class LdapServiceImpl implements LdapService {

  @Autowired
  private LdapConfig ldapConfig;

  private static final Logger LOG = LoggerFactory.getLogger(LdapServiceImpl.class);

  private LDAPConnectionPool connectionPool;

  @PostConstruct
  public void init() throws LDAPException, GeneralSecurityException {
    LDAPConnection ldapConnection = null;
    if (ldapConfig.isDisableSSL()) {
      ldapConnection = new LDAPConnection(ldapConfig.getHost(), ldapConfig.getPort());
    } else {
      ldapConnection =
          new LDAPConnection(new SSLUtil(new TrustAllTrustManager()).createSSLSocketFactory(),
              ldapConfig.getHost(), ldapConfig.getPort());
    }

    connectionPool = new LDAPConnectionPool(ldapConnection, 10);
  }

  @PreDestroy
  public void destroy() throws LDAPException, GeneralSecurityException {
    connectionPool.close();
  }

  @Override
  public List<User> getUsers() throws LDAPException {
    List<User> users = new ArrayList<>();
    LDAPConnection ldapConnection = connectionPool.getConnection();
    try {
      ldapConnection.bind(ldapConfig.getReadUser(), ldapConfig.getReadPassword());
      // get value list with userDN
      SearchResultEntry searchResultEntry =
          ldapConnection.getEntry(ldapConfig.getApplicationUserDN());
      String[] members = searchResultEntry.getAttributeValues("member");

      for (String member : members) {
        SearchResultEntry userEntry = ldapConnection.getEntry(member);
        if (userEntry != null) {
          users.add(createUser(userEntry));
        }
      }
    } catch (LDAPException e) {
      connectionPool.releaseConnectionAfterException(ldapConnection, e);
      throw e;
    }
    connectionPool.releaseConnection(ldapConnection);
    return users;

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
    try {
    BindResult bindResult =
        connectionPool.bind(ldapConfig.getUserBindDN().replace("$username", uid), password);
    return bindResult.getResultCode().equals(ResultCode.SUCCESS);
    } catch (LDAPException e) {
      if (e.getResultCode().equals(ResultCode.INVALID_CREDENTIALS)) {
        return false;
      }
      throw e;
    }
    }

  @Override
  public List<String> getUserGroups(String uid) throws LDAPException {
    List<String> groups = new ArrayList<>();
    LDAPConnection ldapConnection = connectionPool.getConnection();
    try {
      ldapConnection.bind(ldapConfig.getReadUser(), ldapConfig.getReadPassword());
      SearchRequest searchRequest =
          new SearchRequest(ldapConfig.getGroupLookUpDN(), SearchScope.SUBORDINATE_SUBTREE,
              Filter.createEqualityFilter(ldapConfig.getGroupLookUpAttribute(),
                  ldapConfig.getUserBindDN().replace("$username", uid)));
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

  public LdapConfig getLdapConfig() {
    return ldapConfig;
  }
}
