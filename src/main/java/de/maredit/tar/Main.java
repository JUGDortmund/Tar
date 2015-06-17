package de.maredit.tar;

import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import de.maredit.tar.listeners.ContextListener;
import de.maredit.tar.listeners.StartupListener;

@SpringBootApplication
@EnableScheduling
public class Main {
  private static final Logger LOG = LogManager.getLogger(Main.class);
  
  private static Locale locale = LocaleContextHolder.getLocale();

  public static void main(String[] args) {
    locale = LocaleContextHolder.getLocale();
    SpringApplication springApplication = new SpringApplication(Main.class);
    springApplication.addListeners(new StartupListener(), new ContextListener());
    springApplication.run(args);
  }
  
  @Bean
  public LocaleChangeInterceptor localeChangeInterceptor() {
    LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
    localeChangeInterceptor.setParamName("lang");
    return localeChangeInterceptor;
  }

  @Bean(name = "localeResolver")
  public CookieLocaleResolver localeResolver() {
    CookieLocaleResolver localeResolver = new CookieLocaleResolver();
    localeResolver.setCookieName("tarPreferredLocale");
    localeResolver.setDefaultLocale(locale);
    return localeResolver;
  }

  @Bean
  public ResourceBundleMessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename("lang/messages");
    messageSource.setCacheSeconds(0);
    return messageSource;
  }
}
