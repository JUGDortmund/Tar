package de.maredit.tar.models.converters;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

/**
 * Created by czillmann on 24.04.15.
 */

public class LocalDateToDateConverter implements Converter<LocalDate, Date> {

  @Override
  public Date convert(LocalDate source) {
    return source == null ? null
                          : Date.from(source.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }
}