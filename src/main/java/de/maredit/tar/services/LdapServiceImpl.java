package de.maredit.tar.services;


import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

import de.maredit.tar.models.User;
import de.maredit.tar.properties.LdapProperties;
import de.maredit.tar.providers.VersionProvider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
@Profile({"prod", "serviceTest"})
public class LdapServiceImpl implements LdapService {

  public static final int NUM_CONNECTIONS = 10;
  
  private static final Pattern UID_PATTERN = Pattern.compile("uid=(\\w+)");

  @Autowired
  private LdapProperties ldapProperties;

  private static final Logger LOG = LogManager.getLogger(LdapServiceImpl.class);

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
      SearchResultEntry searchResultEntry =
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
  public Set<String> getLdapSupervisorList() throws LDAPException {
    Set<String> manager = new HashSet<>();
    LDAPConnection ldapConnection = connectionPool.getConnection();
    try {
      ldapConnection.bind(ldapProperties.getReadUser(), ldapProperties.getReadPassword());
      // get value list with userDN
      SearchResultEntry searchResultEntry =
          ldapConnection.getEntry(ldapProperties.getApplicationSupervisorDN());

      String[] memberUids = searchResultEntry.getAttributeValues(FIELD_MEMBER);
      
      for (String member : memberUids) {
        Matcher m = UID_PATTERN.matcher(member);
        if (m.find()) {
          manager.add(m.group(1));
        }
      }
    } catch (LDAPException e) {
      LOG.error("Error reading user list from LDAP", e);
      connectionPool.releaseConnectionAfterException(ldapConnection, e);
      throw e;
    }
    connectionPool.releaseConnection(ldapConnection);
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
      SearchResult searchResults = searchForLdapGroups(uid, ldapConnection);

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

  private SearchResult searchForLdapGroups(String uid, LDAPConnection ldapConnection)
      throws LDAPException, LDAPSearchException {
    ldapConnection.bind(ldapProperties.getReadUser(), ldapProperties.getReadPassword());
    SearchRequest searchRequest =
        new SearchRequest(ldapProperties.getGroupLookUpDN(), SearchScope.SUBORDINATE_SUBTREE,
                          Filter.createEqualityFilter(ldapProperties.getGroupLookUpAttribute(),
                                                      ldapProperties
                                                          .getUserBindDN()
                                                          .replace("$username", uid)));
    SearchResult searchResults = ldapConnection.search(searchRequest);
    return searchResults;
  }

  private User createUser(SearchResultEntry resultEntry) {
    User user = new User();
    user.setMail(resultEntry.getAttributeValue(LdapService.FIELD_MAIL));
    user.setUidNumber(resultEntry.getAttributeValue(LdapService.FIELD_UIDNUMBER));
    user.setUsername(resultEntry.getAttributeValue(LdapService.FIELD_UID));
    user.setFirstname(resultEntry.getAttributeValue(LdapService.FIELD_CN));
    user.setLastname(resultEntry.getAttributeValue(LdapService.FIELD_SN));
    user.setPhoto(resultEntry.getAttributeValueBytes(LdapService.FIELD_PHOTO));
    user.setActive(Boolean.TRUE);
   
    return user;
  }
}
