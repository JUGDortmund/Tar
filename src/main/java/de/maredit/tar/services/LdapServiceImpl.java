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

import de.maredit.tar.properties.LdapProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class LdapServiceImpl implements LdapService {

  public static final int NUM_CONNECTIONS = 10;
  @Autowired
  private LdapProperties ldapProperties;

  private static final Logger LOG = LoggerFactory.getLogger(LdapServiceImpl.class);

  private LDAPConnectionPool connectionPool;

  @PostConstruct
  public void init() throws LDAPException, GeneralSecurityException {
    LDAPConnection ldapConnection = null;
    ldapConnection =
        new LDAPConnection(new SSLUtil(new TrustAllTrustManager()).createSSLSocketFactory(),
                           ldapProperties.getHost(), ldapProperties.getPort(),
                           ldapProperties.getReadUser(), ldapProperties.getReadPassword());

    connectionPool = new LDAPConnectionPool(ldapConnection, NUM_CONNECTIONS);
  }

  @PreDestroy
  public void destroy() throws LDAPException, GeneralSecurityException {
    connectionPool.close();
  }

  /**
   * Method to synchronize the system user objects with the LDAP user
   */
  @Override
  public List<SearchResultEntry> getLdapUserList() throws LDAPException {
    LDAPConnection ldapConnection = null;
    try {
      ldapConnection = connectionPool.getConnection();
      // get value list with userDN
      SearchResultEntry
          searchResultEntry =
          ldapConnection.getEntry(ldapProperties.getApplicationUserDN());
      String[] members = searchResultEntry.getAttributeValues(FIELD_MEMBER);

      // iterate over userDN and create/update users
      List<SearchResultEntry> ldapUser = new ArrayList<SearchResultEntry>();

      for (String member : members) {
        SearchResultEntry userEntry = ldapConnection.getEntry(member);
        if (userEntry != null) {
          ldapUser.add(userEntry);
        }
      }
      connectionPool.releaseConnection(ldapConnection);
      return ldapUser;
    } catch (LDAPException e) {
      LOG.error("Error reading user list from LDAP", e);
      connectionPool.releaseConnectionAfterException(ldapConnection, e);
      throw e;
    }
  }

  @Override
  public Set<String> getLdapManagerList() throws LDAPException {
    LDAPConnection ldapConnection = null;
    try {
      ldapConnection = connectionPool.getConnection();
      // get value list with userDN
      SearchResultEntry
          searchResultEntry =
          ldapConnection.getEntry(ldapProperties.getApplicationTeamleaderDN());

      String[] memberUids = searchResultEntry.getAttributeValues(FIELD_MEMBERUID);

      final HashSet<String> userSet = new HashSet<>();
      Collections.addAll(userSet, memberUids);
      return userSet;
    }
    catch (LDAPException e) {
      LOG.error("Error reading user list from LDAP", e);
      connectionPool.releaseConnectionAfterException(ldapConnection, e);
      throw e;
    }
  }

  @Override
  public boolean authenticateUser(String uid, String password) throws LDAPException {
    BindResult bindResult =
        connectionPool.bind(FIELD_UID + "=" + uid + "," + ldapProperties.getUserLookUpDN(), password);
    return bindResult.getResultCode().equals(ResultCode.SUCCESS);
  }

  @Override
  public List<String> getUserGroups(String uid) throws LDAPException {
    List<String> groups = new ArrayList<>();
    LDAPConnection ldapConnection = connectionPool.getConnection();
    try {
      ldapConnection.bind(ldapProperties.getReadUser(), ldapProperties.getReadPassword());
      SearchRequest searchRequest =
          new SearchRequest(ldapProperties.getUserLookUpDN(), SearchScope.SUBORDINATE_SUBTREE,
                            Filter.createEqualityFilter(FIELD_MEMBER, uid));
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
}