package de.maredit.tar.models.converters;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Created by czillmann on 24.04.15.
 */
public class LocalDateToStringConverter implements Converter<LocalDate, String> {

  @Override
  public String convert(LocalDate localDate) {
    return localDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
  }
}
