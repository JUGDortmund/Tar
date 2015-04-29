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

import de.maredit.tar.models.User;
import de.maredit.tar.properties.LdapProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
@Profile({"prod", "dev", "serviceTest"})
public class LdapServiceImpl implements LdapService {

  public static final int NUM_CONNECTIONS = 10;

  @Autowired
  private LdapProperties ldapProperties;

  private static final Logger LOG = LoggerFactory.getLogger(LdapServiceImpl.class);

  private LDAPConnectionPool connectionPool;

  @PostConstruct
  public void init() throws LDAPException, GeneralSecurityException {
    LDAPConnection ldapConnection = null;
    if (ldapProperties.isDisableSSL()) {
      ldapConnection = new LDAPConnection(ldapProperties.getHost(), ldapProperties.getPort());
    } else {
      ldapConnection =
          new LDAPConnection(new SSLUtil(new TrustAllTrustManager()).createSSLSocketFactory(),
                             ldapProperties.getHost(), ldapProperties.getPort());
    }

    connectionPool = new LDAPConnectionPool(ldapConnection, NUM_CONNECTIONS);
  }

  @PreDestroy
  public void destroy() throws LDAPException, GeneralSecurityException {
    connectionPool.close();
  }

  @Override
  public List<User> getLdapUserList() throws LDAPException {
    List<User> users = new ArrayList<>();
    LDAPConnection ldapConnection = connectionPool.getConnection();
    try {
      ldapConnection.bind(ldapProperties.getReadUser(), ldapProperties.getReadPassword());
      // get value list with userDN
      SearchResultEntry
          searchResultEntry =
          ldapConnection.getEntry(ldapProperties.getApplicationUserDN());
      String[] members = searchResultEntry.getAttributeValues(FIELD_MEMBER);

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

  @Override
  public Set<String> getLdapManagerList() throws LDAPException {
    Set<String> manager = new HashSet<>();
    LDAPConnection ldapConnection = connectionPool.getConnection();
    try {
      ldapConnection.bind(ldapProperties.getReadUser(), ldapProperties.getReadPassword());
      // get value list with userDN
      SearchResultEntry
          searchResultEntry =
          ldapConnection.getEntry(ldapProperties.getApplicationTeamleaderDN());

      String[] memberUids = searchResultEntry.getAttributeValues(FIELD_MEMBERUID);

      Collections.addAll(manager, memberUids);
    } catch (LDAPException e) {
      LOG.error("Error reading user list from LDAP", e);
      connectionPool.releaseConnectionAfterException(ldapConnection, e);
      throw e;
    }
    connectionPool.releaseConnection(ldapConnection);;
    return manager;
  }

  @Override
  public boolean authenticateUser(String uid, String password) throws LDAPException {
    try {
      BindResult bindResult =
          connectionPool.bind(ldapProperties.getUserBindDN().replace("$username", uid), password);

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
      ldapConnection.bind(ldapProperties.getReadUser(), ldapProperties.getReadPassword());
      SearchRequest searchRequest =
          new SearchRequest(ldapProperties.getGroupLookUpDN(), SearchScope.SUBORDINATE_SUBTREE,
                            Filter.createEqualityFilter(ldapProperties.getGroupLookUpAttribute(),
                                                        ldapProperties.getUserBindDN()
                                                            .replace("$username", uid)));
      SearchResult searchResults = ldapConnection.search(searchRequest);

      if (searchResults.getEntryCount() > 0) {
        for (SearchResultEntry entry : searchResults.getSearchEntries()) {
          groups.add(entry.getAttribute(FIELD_CN).getValue());
        }
      }
    } catch (LDAPException e) {
      connectionPool.releaseConnectionAfterException(ldapConnection, e);
      throw e;
    }
    connectionPool.releaseConnection(ldapConnection);
    return groups;
  }

  private User createUser(SearchResultEntry resultEntry) {
    User user;
    user = new User();
    user.setMail(resultEntry.getAttributeValue(LdapService.FIELD_MAIL));
    user.setUidNumber(resultEntry.getAttributeValue(LdapService.FIELD_UIDNUMBER));
    user.setUsername(resultEntry.getAttributeValue(LdapService.FIELD_UID));
    user.setFirstName(resultEntry.getAttributeValue(LdapService.FIELD_CN));
    user.setLastName(resultEntry.getAttributeValue(LdapService.FIELD_SN));
    user.setActive(Boolean.TRUE);

    LOG.debug("User created. username: %s/uidNumber: %s", user.getUsername(), user.getUidNumber());
    return user;
  }
}
