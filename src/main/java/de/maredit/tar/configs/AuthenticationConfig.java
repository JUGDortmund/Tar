package de.maredit.tar.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import de.maredit.tar.providers.ApplicationAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class AuthenticationConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http
		        .csrf()
		        .disable()
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
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
	    auth.authenticationProvider(new ApplicationAuthenticationProvider());
	}
}