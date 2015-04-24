package de.maredit.tar.configs;

import de.maredit.tar.models.converters.DateToLocalDateConverter;
import de.maredit.tar.models.converters.DateToLocalDateTimeConverter;
import de.maredit.tar.models.converters.LocalDateTimeToDateConverter;
import de.maredit.tar.models.converters.LocalDateToDateConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by czillmann on 24.04.15.
 */

@Configuration
public class ConversionConfiguration {

  private static final Logger LOG =  LoggerFactory.getLogger(ConversionConfiguration.class);

  @Bean
  public ConversionService getConversionService() {
    ConversionServiceFactoryBean bean;
    bean = new ConversionServiceFactoryBean();
    bean.setConverters(getConverters());
    bean.afterPropertiesSet();
    ConversionService service = bean.getObject();
    return service;
  }

  private Set<Converter> getConverters() {
    Set<Converter> converters = new HashSet<Converter>();
    converters.add(new DateToLocalDateTimeConverter());
    converters.add(new LocalDateTimeToDateConverter());
    converters.add(new DateToLocalDateConverter());
    converters.add(new LocalDateToDateConverter());

    LOG.info("++++++++++++++++++++++++YEAH+++++++++++++++++++++++++++++++++");
    return converters;
  }
}