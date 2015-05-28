package de.maredit.tar.configs;

import org.springframework.context.annotation.Profile;

import de.maredit.tar.properties.ExchangeProperties;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;

@Configuration
@Profile({"exchangeMailService", "exchangeCalendarService"})
public class ExchangeConfig {
  
  @Autowired
  private ExchangeProperties properties;

  @Bean(destroyMethod="close")
  public ExchangeService exchangeService() throws URISyntaxException {
    ExchangeService exchangeService = new ExchangeService();
    exchangeService.setUrl(properties.getService());
    exchangeService.setCredentials(new WebCredentials(properties.getUser(), properties.getPassword()));
    return exchangeService;
  }
}
