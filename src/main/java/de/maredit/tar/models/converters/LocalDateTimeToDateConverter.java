package de.maredit.tar.models.converters;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * Created by czillmann on 24.04.15.
 */
public class LocalDateTimeToDateConverter implements Converter<LocalDateTime, Date> {

  public Date convert(LocalDateTime source) {
    return source == null ? null : Date.from(source.toInstant(ZoneOffset.UTC));
  }
}
