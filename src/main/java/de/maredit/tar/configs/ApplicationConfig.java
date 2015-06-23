package de.maredit.tar.configs;

import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import org.springframework.cache.CacheManager;
import de.maredit.tar.beans.NavigationBean;
import de.maredit.tar.beans.VersionBean;
import de.maredit.tar.utils.Log4j2Configurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

@Configuration
public class ApplicationConfig {

  @Value("${spring.log:console}")
  private String logconfig;

  @Bean
  @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public NavigationBean navigationBean() {
    return new NavigationBean();
  }

  @Bean
  @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
  public VersionBean versionBean() {
    return new VersionBean();
  }

  @Bean
  public MethodInvokingFactoryBean log4jInitialization() {
    MethodInvokingFactoryBean factoryBean = new MethodInvokingFactoryBean();
    factoryBean.setTargetClass(Log4j2Configurer.class);
    factoryBean.setTargetMethod("initLogging");
    factoryBean.setArguments(new String[] {"classpath:log4j2-" + logconfig + ".xml"});
    return factoryBean;
  }
  
  @Bean
  public CacheManager cacheManager() {
    return new ConcurrentMapCacheManager("holidays");
  }
}
