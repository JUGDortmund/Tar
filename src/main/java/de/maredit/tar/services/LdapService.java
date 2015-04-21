package de.maredit.tar.services;

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

  public List<String> authenticateUser(String uid, String password) {
    LDAPConnection ldapConnection = null;
    try {
      ldapConnection = new LDAPConnection(new SSLUtil(new TrustAllTrustManager()).createSSLSocketFactory(), "ldap-master.maredit.net", 636);
      BindResult bindResult = ldapConnection.bind("uid=" + uid + ",ou=users,o=maredit,dc=de", password);
      if (bindResult.getResultCode().equals(ResultCode.SUCCESS)) {
        ldapConnection.bind("cn=read,cn=ldap,ou=services,o=maredit,dc=de", "7Ey8vHzPPZrdWA");
        SearchRequest searchRequest = new SearchRequest("ou=groups,o=maredit,dc=de", SearchScope.SUBORDINATE_SUBTREE, Filter.createEqualityFilter("memberUid", uid));
        SearchResult searchResults = ldapConnection.search(searchRequest);

        List<String> groups = new ArrayList<>();
        if (searchResults.getEntryCount() > 0) {
          for (SearchResultEntry entry : searchResults.getSearchEntries()) {
            groups.add(entry.getAttribute("cn").getValue());
          }
        }
        return groups;
      }
    } catch (LDAPException | GeneralSecurityException e) {
      // TODO throw an usable exception
      log.error("Error during LDAP authentication", e);
    } finally {
      if (ldapConnection != null) {
        ldapConnection.close();
      }
    }
    return null;
  }

}
