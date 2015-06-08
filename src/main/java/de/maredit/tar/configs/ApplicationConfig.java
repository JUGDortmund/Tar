package de.maredit.tar.configs;

import de.maredit.tar.beans.NavigationBean;
import de.maredit.tar.beans.VersionBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

@Configuration
public class ApplicationConfig {

  @Bean
  @Scope(value=WebApplicationContext.SCOPE_SESSION, proxyMode=ScopedProxyMode.TARGET_CLASS)
  public NavigationBean navigationBean() {
    return new NavigationBean();
  }

  @Bean
  @Scope(value=ConfigurableBeanFactory.SCOPE_SINGLETON)
  public VersionBean versionBean() {
    return new VersionBean();
  }
}
