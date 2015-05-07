package de.maredit.tar.configs;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import de.maredit.tar.providers.ApplicationAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class AuthenticationConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private ApplicationAuthenticationProvider applicationAuthenticationProvider;

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        .antMatchers("/css/**").permitAll()
        .antMatchers("/js/**").permitAll()
        .antMatchers("/fonts/**").permitAll()
        .antMatchers("/plugins/**").permitAll()
        .antMatchers("/dist/**").permitAll()
        .antMatchers("/login").permitAll()
        .anyRequest().authenticated()
        .and()
        .formLogin()
        .loginPage("/login")
        .permitAll()
        .and()
        .logout()
        .permitAll();
    http.csrf().disable();
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(applicationAuthenticationProvider);
  }
  
  @Bean
  public RoleHierarchyVoter roleVoter() {
    return new RoleHierarchyVoter(roleHierarchy());
  }
  
  @Bean
  public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    roleHierarchy.setHierarchy("ROLE_SUPERVISOR > ROLE_USER");
    return roleHierarchy;
  }

}