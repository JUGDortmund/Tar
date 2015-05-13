package de.maredit.tar.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Collection;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThymeleafConfig {

  @Autowired
  private final Collection<ITemplateResolver> templateResolvers = Collections
          .emptySet();

  @Autowired(required = false)
  private final Collection<IDialect> dialects = Collections.emptySet();

  @Bean
  public SpringTemplateEngine templateEngine() {
      SpringTemplateEngine engine = new SpringTemplateEngine();
      for (ITemplateResolver templateResolver : this.templateResolvers) {
          engine.addTemplateResolver(templateResolver);
      }
      for (IDialect dialect : this.dialects) {
          engine.addDialect(dialect);
      }
      engine.addDialect(new SpringSecurityDialect());
      return engine;
  }
}