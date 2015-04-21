package de.maredit.tar.services;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.GeneralSecurityException;

@Component
public class LdapService {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  public boolean authenticateUser(String uid, String password) {
    try {
      LDAPConnection
          ldapConnection =
          new LDAPConnection(new SSLUtil(new TrustAllTrustManager()).createSSLSocketFactory(),
                             "ldap-master.maredit.net", 636,
                             "uid=" + uid + ",ou=users,o=maredit,dc=de", password);
      return true;
    } catch (LDAPException | GeneralSecurityException e) {
      log.error("Error during LDAP authentication", e);
      return false;
    }
  }

}
