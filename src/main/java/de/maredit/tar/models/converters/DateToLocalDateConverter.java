package de.maredit.tar.models.converters;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

/**
 * Created by czillmann on 24.04.15.
 */
public class DateToLocalDateConverter implements Converter<Date, LocalDate> {

  @Override
  public LocalDate convert(Date date) {
    System.out.println(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    return date == null ? null : date.toInstant().atOffset(ZoneOffset.UTC).toLocalDate();
  }
}
