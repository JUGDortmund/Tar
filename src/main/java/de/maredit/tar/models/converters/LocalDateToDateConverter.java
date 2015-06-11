package de.maredit.tar.models.converters;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * Created by czillmann on 24.04.15.
 */

public class LocalDateToDateConverter implements Converter<LocalDate, Date> {

  @Override
  public Date convert(LocalDate source) {
    return source == null ? null : Date.from(source.atStartOfDay().toInstant(ZoneOffset.UTC));
  }
}
