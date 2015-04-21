package de.maredit.tar.providers;

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
    if (StringUtils.isNotBlank(username) && password != null) {
      List<String> roles = ldapService.authenticateUser(username, String.valueOf(authentication.getCredentials()));
      if (roles != null) {
      List<GrantedAuthority> grantedAuths = new ArrayList<>();
      for (String role : roles) {
        grantedAuths.add(new SimpleGrantedAuthority(role));
      }
      Authentication
          auth =
          new UsernamePasswordAuthenticationToken(authentication.getName(),
                                                  roles,
                                                  grantedAuths);
      return auth;
      }
    }
    return null;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}