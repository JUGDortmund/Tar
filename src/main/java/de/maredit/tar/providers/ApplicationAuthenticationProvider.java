package de.maredit.tar.providers;

import org.springframework.security.authentication.AuthenticationServiceException;

import com.unboundid.ldap.sdk.LDAPException;
import de.maredit.tar.services.LdapService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ApplicationAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private LdapService ldapService;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    Object password = authentication.getCredentials();
    try {
      if (StringUtils.isNotBlank(username)
          && password != null
          && ldapService
              .authenticateUser(username, String.valueOf(authentication.getCredentials()))) {
        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        for (String role : ldapService.getUserGroups(username)) {
          grantedAuths.add(new SimpleGrantedAuthority(role));
        }
        Authentication auth =
            new UsernamePasswordAuthenticationToken(authentication.getName(), password,
                grantedAuths);
        return auth;
      }
    } catch (LDAPException e) {
      throw new AuthenticationServiceException("Error accessing LDAP", e);
    }
    return null;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}
