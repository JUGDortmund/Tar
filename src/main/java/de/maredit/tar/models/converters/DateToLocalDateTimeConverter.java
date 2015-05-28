package de.maredit.tar.models.converters;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * Created by czillmann on 24.04.15.
 */
public class DateToLocalDateTimeConverter implements Converter<Date, LocalDateTime> {

  @Override
  public LocalDateTime convert(Date date) {
    return date == null ? null : date.toInstant().atOffset(ZoneOffset.UTC).toLocalDateTime();
  }
}
