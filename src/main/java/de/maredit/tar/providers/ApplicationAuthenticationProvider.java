package de.maredit.tar.providers;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

public class ApplicationAuthenticationProvider implements AuthenticationProvider {
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        Object password = authentication.getCredentials();
        
        if (StringUtils.isNotBlank(username) && password != null && authenticated(authentication.getName(), String.valueOf(authentication.getCredentials()))) {
            List<GrantedAuthority> grantedAuths = new ArrayList<>();
            grantedAuths.add(new SimpleGrantedAuthority(getGroup()));
            Authentication auth = new UsernamePasswordAuthenticationToken(authentication.getName(), String.valueOf(authentication.getCredentials()), grantedAuths);
            
            return auth;
        }
        
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
    
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
    
    public String getGroup() {
        return "ROLE_USER";
    }
}