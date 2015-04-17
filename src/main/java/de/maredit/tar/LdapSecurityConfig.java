package de.maredit.tar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class LdapSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
				.antMatchers("/css/**").permitAll()
				.anyRequest().fullyAuthenticated()
				.and()
				.formLogin().defaultSuccessUrl("/");
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// A wrong configuration throws a "AlreadyBuiltException" - god knows why
		auth.ldapAuthentication()
				.userDnPatterns("uid={0},ou=users,o=maredit,dc=de").contextSource().url("ldaps://172.30.10.72:636");
	}

}