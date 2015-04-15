package de.maredit.tar.services;

import java.security.GeneralSecurityException;

import org.springframework.stereotype.Service;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

@Service
public class AuthenticationService {
    public boolean authenticated(String username, String password) {
        boolean authenticated = false;
    
        try {
            new LDAPConnection(new SSLUtil(new TrustAllTrustManager()).createSSLSocketFactory(), "ldap-master.maredit.net", 636, "uid=" + username + ",ou=users,o=maredit,dc=de", password);
            authenticated = true;
        } catch (LDAPException | GeneralSecurityException e) {
            //intentionally left blank
        }
        
        return authenticated;
    }
}