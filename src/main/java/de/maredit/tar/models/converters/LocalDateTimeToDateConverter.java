package de.maredit.tar.models.converters;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

/**
 * Created by czillmann on 24.04.15.
 */
public class LocalDateTimeToDateConverter implements Converter<LocalDateTime, Date> {

  public Date convert(LocalDateTime source) {
    return source == null?null:Date.from(source.atZone(ZoneId.systemDefault()).toInstant());
  }
}