package de.maredit.tar.configs;

import de.maredit.tar.models.formatters.DoubleFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Collection;
import java.util.Collections;

@Configuration
public class ThymeleafConfig extends WebMvcConfigurerAdapter {

  @Autowired
  private final Collection<ITemplateResolver> templateResolvers = Collections
      .emptySet();

  @Autowired(required = false)
  private final Collection<IDialect> dialects = Collections.emptySet();

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addFormatter(new DoubleFormatter());
  }

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

  @Bean
  public MailProperties mailProperties() {
    return new MailProperties();
  }
}
