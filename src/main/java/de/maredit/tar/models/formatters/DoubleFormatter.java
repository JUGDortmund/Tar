package de.maredit.tar.models.formatters;

import org.springframework.format.Formatter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Created by czillmann on 19.05.15.
 */
public class DoubleFormatter implements Formatter<Double> {

  @Override
  public Double parse(String text, Locale locale) throws ParseException {
    NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
    return numberFormat.parse(text).doubleValue();
  }

  @Override
  public String print(Double object, Locale locale) {
    NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
    return object % 1 == 0 ? String.valueOf(object.intValue()) : numberFormat.format(object);
  }
}
