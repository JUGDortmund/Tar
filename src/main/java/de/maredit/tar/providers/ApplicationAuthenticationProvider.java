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

    if (StringUtils.isNotBlank(username) && password != null && authenticated(
        authentication.getName(), String.valueOf(authentication.getCredentials()))) {
      List<GrantedAuthority> grantedAuths = new ArrayList<>();
      grantedAuths.add(new SimpleGrantedAuthority(getGroup()));
      Authentication
          auth =
          new UsernamePasswordAuthenticationToken(authentication.getName(),
                                                  String.valueOf(authentication.getCredentials()),
                                                  grantedAuths);

      return auth;
    }

    return null;
  }

  @Override
  public boolean supports(Class<?> authentication) {

    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }

  public boolean authenticated(String username, String password) {

    return ldapService.authenticateUser(username, password);
  }

  public String getGroup() {
    return "ROLE_USER";
  }
}