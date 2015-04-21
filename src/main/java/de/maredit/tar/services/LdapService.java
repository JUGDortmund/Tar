package de.maredit.tar.services;

import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
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

@Service
public class LdapService {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  public void synchronizeLdapUser() {
    try {
      LDAPConnection
          ldapConnection =
          new LDAPConnection(new SSLUtil(new TrustAllTrustManager()).createSSLSocketFactory(),
                             "ldap-master.maredit.net", 636,
                             "uid=pplewa,ou=users,o=maredit,dc=de", "Lan2006!");
      Filter searchFilter = Filter.createEqualityFilter("ou", "users");
      SearchRequest searchRequest = new SearchRequest("dc=de", SearchScope.SUB, searchFilter);
      SearchResult searchResult = ldapConnection.search(searchRequest);

      for( SearchResultEntry resultEntry : searchResult.getSearchEntries()) {
        System.out.println("----->" + resultEntry);
      }

    } catch (LDAPException e) {
      e.printStackTrace();
    } catch (GeneralSecurityException e) {
      e.printStackTrace();
    }
  }

  public boolean authenticateUser(String uid, String password) {
    try {
      LDAPConnection
          ldapConnection =
          new LDAPConnection(new SSLUtil(new TrustAllTrustManager()).createSSLSocketFactory(),
                             "ldap-master.maredit.net", 636,
                             "uid=" + uid + ",ou=users,o=maredit,dc=de", password);
      ldapConnection.close();
      return true;
    } catch (LDAPException | GeneralSecurityException e) {
      log.error("Error during LDAP authentication", e);
      return false;
    }
  }

}
