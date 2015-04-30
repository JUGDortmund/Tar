package de.maredit.tar.models.converters;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

/**
 * Created by czillmann on 24.04.15.
 */
public class DateToLocalDateTimeConverter implements Converter<Date, LocalDateTime> {

  @Override
  public LocalDateTime convert(Date date) {
    return date == null ? null : LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
  }
}