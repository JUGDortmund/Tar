package de.maredit.tar.services;

import com.unboundid.ldap.sdk.LDAPConnectionPool;

import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class LdapService {

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


  public void synchronizeLdapUser() {
    try {
      LDAPConnection ldapConnection =
          new LDAPConnection(new SSLUtil(new TrustAllTrustManager()).createSSLSocketFactory(),
              "ldap-master.maredit.net", 636, "uid=pplewa,ou=users,o=maredit,dc=de", "Lan2006!");
      Filter searchFilter = Filter.createEqualityFilter("ou", "users");
      SearchRequest searchRequest = new SearchRequest("dc=de", SearchScope.SUB, searchFilter);
      SearchResult searchResult = ldapConnection.search(searchRequest);

      for (SearchResultEntry resultEntry : searchResult.getSearchEntries()) {
        System.out.println("----->" + resultEntry);
      }

    } catch (LDAPException e) {
      e.printStackTrace();
    } catch (GeneralSecurityException e) {
      e.printStackTrace();
    }
  }

  public boolean authenticateUser(String uid, String password) throws LDAPException {
      BindResult bindResult =
          connectionPool.bind("uid=" + uid + ",ou=users,o=maredit,dc=de", password);
      return bindResult.getResultCode().equals(ResultCode.SUCCESS);
  }

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
